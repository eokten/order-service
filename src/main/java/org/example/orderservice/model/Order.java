package org.example.orderservice.model;

import lombok.Data;

import java.util.List;

@Data
public class Order {

    private Long id;

    private Long timestamp;

    private List<Long> productIds;
}
