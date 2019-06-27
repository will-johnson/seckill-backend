package com.seen.seckillbackend.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Goods {
    private Long id;
    private String name;
    private Long stock;
    private BigDecimal price;
}
