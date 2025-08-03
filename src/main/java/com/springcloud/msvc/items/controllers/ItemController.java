package com.springcloud.msvc.items.controllers;

import com.springcloud.msvc.items.models.Item;
import com.springcloud.msvc.items.models.Product;
import com.springcloud.msvc.items.services.ItemService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
public class ItemController {

    private final Logger logger= LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;
    private final CircuitBreakerFactory breakerFactory;

    public ItemController(@Qualifier("itemServiceFeign") ItemService itemService,
                          CircuitBreakerFactory breakerFactory) {
        this.itemService = itemService;
        this.breakerFactory= breakerFactory;
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

    @TimeLimiter(name = "items",fallbackMethod = "getFallBackMetohodProduct2")
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
}
