package com.springcloud.msvc.items.clients;

import com.springcloud.msvc.items.models.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "msvc-products")
public interface ProductFeignClient {
    @GetMapping(path = "/api/products")
    List<Product> findAll();

    @GetMapping("/api/products/{id}")
    Product details(@PathVariable Long id);
}
