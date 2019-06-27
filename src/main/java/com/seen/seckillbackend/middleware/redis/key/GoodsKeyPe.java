package com.seen.seckillbackend.middleware.redis.key;

public class GoodsKeyPe extends BaseKeyPe {

    public GoodsKeyPe(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static GoodsKeyPe goodsKeyPe = new GoodsKeyPe(0, "g_stock");
}
