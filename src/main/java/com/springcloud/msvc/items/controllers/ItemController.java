package com.springcloud.msvc.items.controllers;

import com.libs.msvc.commons.entities.Product;
import com.springcloud.msvc.items.models.Item;
import com.springcloud.msvc.items.services.ItemService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RefreshScope
@RestController
public class ItemController {

    private final Logger logger= LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;
    private final CircuitBreakerFactory breakerFactory;

    @Value("${configuration.texto}")
    private String text;

    @Autowired
    private Environment env;
    //itemServiceFeign , itemServiceWebClient
    public ItemController(@Qualifier("itemServiceFeign") ItemService itemService,
                          CircuitBreakerFactory breakerFactory) {
        this.itemService = itemService;
        this.breakerFactory= breakerFactory;
    }

    @GetMapping("/fetch-configs")
    public ResponseEntity<?> fetchConfigs(@Value("${server.port}") String port){
        Map<String, String> json = new HashMap<>();
        json.put("text", text);
        json.put("port", port);

        if(env.getActiveProfiles().length>0 && env.getActiveProfiles()[0].equals("dev")) {
            json.put("autor.nombre", env.getProperty("configuration.autor.nombre"));
            json.put("autor.email", env.getProperty("configuration.autor.email"));
        } else {
            json.put("profile", "default");
        }
        logger.info(port);
        logger.info(text);
        return ResponseEntity.ok(json);

    }

    @GetMapping
    public List<Item> list(@RequestParam(name = "name",required = false) String name,
                           @RequestHeader(name = "token-request", required = false) String nameToken){
        System.out.println(name);
        System.out.println(nameToken);
        return itemService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable Long id){
        Optional<Item> item =  breakerFactory.create("items").run(() -> itemService.findById(id),
                e->{
                    logger.error(e.getMessage());
                    Product product = Product.builder()
                            .createdAt(LocalDate.now())
                            .id(1L)
                            .name("Camara sony")
                            .price(500.00)
                            .build();
                    return Optional.of(new Item(product,5));
                });
        if(item.isPresent())
            return ResponseEntity.ok(item.get());
        return ResponseEntity.status(404).body(Collections.singletonMap("message","No existe el producto en el ms product"));
    }

    @CircuitBreaker(name = "items",fallbackMethod = "getFallBackMetohodProduct")
    @GetMapping("/details/{id}")
    public ResponseEntity<?> details2(@PathVariable Long id){
        Optional<Item> item =   itemService.findById(id);
        if(item.isPresent())
            return ResponseEntity.ok(item.get());
        return ResponseEntity.status(404).body(Collections.singletonMap("message","No existe el producto en el ms product"));
    }

   @CircuitBreaker(name = "items",fallbackMethod = "getFallBackMetohodProduct2")
    @TimeLimiter(name = "items"/*,fallbackMethod = "getFallBackMetohodProduct2"*/)
    @GetMapping("/details2/{id}")
    public CompletableFuture<?> details3(@PathVariable Long id){
        return CompletableFuture.supplyAsync(()->{
            Optional<Item> item =   itemService.findById(id);
            if(item.isPresent())
                return ResponseEntity.ok(item.get());
            return ResponseEntity.status(404).body(Collections.singletonMap("message","No existe el producto en el ms product"));
        });

    }

    public ResponseEntity<?> getFallBackMetohodProduct(Throwable e){
        logger.error(e.getMessage());
        Product product = Product.builder()
                .createdAt(LocalDate.now())
                .id(1L)
                .name("Camara sony")
                .price(500.00)
                .build();
        return ResponseEntity.ok(new Item(product,5));
    }

    public CompletableFuture<?> getFallBackMetohodProduct2(Throwable e){
        return CompletableFuture.supplyAsync(()->{
            logger.error(e.getMessage());
            Product product = Product.builder()
                    .createdAt(LocalDate.now())
                    .id(1L)
                    .name("Camara sony")
                    .price(500.00)
                    .build();
            return ResponseEntity.ok(new Item(product,5));
        });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product){
        return itemService.save(product);
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Product update(@RequestBody Product product, @PathVariable Long id){
        return itemService.update(product,id);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        itemService.delete(id);
    }

}
