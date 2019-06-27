package com.seen.seckillbackend.middleware.redis.key;

public class OrderKeyPe extends BaseKeyPe {

    private OrderKeyPe(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static OrderKeyPe orderKeyPe = new OrderKeyPe(0, "order_ug");
}
