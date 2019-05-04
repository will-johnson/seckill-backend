package com.seen.seckillbackend.redis.key;

public class OrderKeyPrefix  extends  BaseKeyPrefix{

    private OrderKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static OrderKeyPrefix orderKeyPrefix = new OrderKeyPrefix(0, "order_ug");
}
