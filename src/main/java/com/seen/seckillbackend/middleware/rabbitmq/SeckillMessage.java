package com.seen.seckillbackend.middleware.rabbitmq;

import com.seen.seckillbackend.domain.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeckillMessage {
    private Long userId;
    private Goods goods;
}
