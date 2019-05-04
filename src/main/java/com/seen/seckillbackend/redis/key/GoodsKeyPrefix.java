package com.seen.seckillbackend.redis.key;

public class GoodsKeyPrefix extends BaseKeyPrefix {

    public GoodsKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static GoodsKeyPrefix goodsStockPrefix= new GoodsKeyPrefix(0, "g_stock");
}
