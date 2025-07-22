package com.springcloud.msvc.items.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    private Product product;
    private int quantity;

    public Double getTotal(){
        return product.getPrice() * quantity;
    }
}
