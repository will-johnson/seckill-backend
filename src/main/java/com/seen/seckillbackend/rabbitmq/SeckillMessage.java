package com.seen.seckillbackend.rabbitmq;

import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.User;
import lombok.Data;

@Data
public class SeckillMessage {
    private User user;
    private Goods goods;

    public SeckillMessage(User user, Goods goods) {
        this.user = user;
        this.goods = goods;
    }
}
