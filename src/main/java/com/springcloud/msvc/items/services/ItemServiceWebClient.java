package com.springcloud.msvc.items.services;

import com.springcloud.msvc.items.models.Item;
import com.springcloud.msvc.items.models.Product;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
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
                    .uri("/api/products/{id}",params)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .map(producto -> new Item(producto,new Random().nextInt(10+1)))
                    .block());
        }catch (WebClientResponseException e){
            return Optional.empty();
        }

    }
}
