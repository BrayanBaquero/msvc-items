package com.springcloud.msvc.items.controllers;

import com.springcloud.msvc.items.models.Item;
import com.springcloud.msvc.items.services.ItemService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<Item> list(){
        return itemService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable Long id){
        Optional<Item> item =  itemService.findById(id);
        if(item.isPresent())
            return ResponseEntity.ok(item.get());
        return ResponseEntity.status(404).body(Collections.singletonMap("message","No existe el producto en el ms product"));
    }
}
