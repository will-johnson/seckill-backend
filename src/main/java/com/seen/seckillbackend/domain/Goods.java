package com.seen.seckillbackend.domain;

import lombok.Data;

@Data
public class Goods {
    private Long id;
    private String name;
    private Integer stock;
    private Double price;
}
