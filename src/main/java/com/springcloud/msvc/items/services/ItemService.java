package com.springcloud.msvc.items.services;

import com.libs.msvc.commons.entities.Product;
import com.springcloud.msvc.items.models.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<Item> findAll();
    Optional<Item> findById(Long id);
    Product save(Product product);
    Product update(Product product, Long id);
    void delete(Long id);
}
