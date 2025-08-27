package com.springcloud.msvc.items.services;

import com.libs.msvc.commons.entities.Product;
import com.springcloud.msvc.items.models.Item;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;

@AllArgsConstructor
//@Primary
@Service
public class ItemServiceWebClient implements  ItemService{
    private  final WebClient.Builder client;
    @Override
    public List<Item> findAll() {
        return client.build()
                .get()
           //     .uri("/api/products")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Product.class)
                .map(producto -> new Item(producto,new Random().nextInt(10+1)))
                .collectList()
                .block();
    }

    @Override
    public Optional<Item> findById(Long id) {
        Map<String,Object> params=new HashMap<>();
        params.put("id",id);
        try {
            return Optional.ofNullable(client.build()
                    .get()
                    .uri("/{id}",params)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .map(producto -> new Item(producto,new Random().nextInt(10+1)))
                    .block());
        }catch (WebClientResponseException e){
            return Optional.empty();
        }

    }

    @Override
    public Product save(Product product) {
        return client.build().
                post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .block();
    }

    @Override
    public Product update(Product product, Long id) {
        Map<String,Object> params=new HashMap<>();
        params.put("id",id);
        return client.build()
                .put()
                .uri("/{id}",params)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .block();
    }

    @Override
    public void delete(Long id) {
        Map<String,Object> params=new HashMap<>();
        params.put("id",id);
        client.build().delete().uri("/{id}",params)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
