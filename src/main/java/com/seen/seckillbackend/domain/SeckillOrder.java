package com.seen.seckillbackend.domain;

import lombok.Data;

@Data
public class SeckillOrder {
    private Long id;
    private Long userId;
    private Long goodsId;
}
