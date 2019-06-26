package com.seen.seckillbackend.middleware.redis.key;

public class OrderKeyPrefix  extends  BaseKeyPrefix{

    private OrderKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static OrderKeyPrefix orderKeyPrefix = new OrderKeyPrefix(0, "order_ug");
}
