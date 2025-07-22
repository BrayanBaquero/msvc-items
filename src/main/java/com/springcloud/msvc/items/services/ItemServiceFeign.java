package com.springcloud.msvc.items.services;

import com.springcloud.msvc.items.clients.ProductFeignClient;
import com.springcloud.msvc.items.models.Item;
import com.springcloud.msvc.items.models.Product;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ItemServiceFeign implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;
    @Override
    public List<Item> findAll() {
        return productFeignClient.findAll()
                .stream()
                .map(producto -> new Item(producto,new Random().nextInt(10+1)))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(Long id) {
        try {
            Product product =  productFeignClient.details(id);
            return Optional.of(new Item(product,new Random().nextInt(10+1))) ;
        }catch (FeignException e){
            return Optional.empty();
        }
    }
}
