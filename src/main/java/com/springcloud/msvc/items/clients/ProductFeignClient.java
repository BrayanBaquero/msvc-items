package com.springcloud.msvc.items.clients;

import com.springcloud.msvc.items.models.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "msvc-products")
public interface ProductFeignClient {
    @GetMapping
    List<Product> findAll();

    @GetMapping("/{id}")
    Product details(@PathVariable Long id);

    @PostMapping
    public Product create(Product product);

    @PutMapping("/{id}")
    public Product update(Product product, @PathVariable Long id);

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id);
}
