package com.seen.seckillbackend.rabbitmq;

import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeckillMessage {
    private Long uid;
    private Goods goods;
}
