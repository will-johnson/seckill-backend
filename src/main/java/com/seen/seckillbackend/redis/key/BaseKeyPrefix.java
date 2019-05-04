package com.seen.seckillbackend.redis.key;

public abstract class BaseKeyPrefix implements KeyPrefix {
    private int expireSeconds;
    private String prefix;

    public BaseKeyPrefix(String prefix) {//0代表永不过期
        this(0, prefix);
    }

    public BaseKeyPrefix( int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }


    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        return this.getClass().getSimpleName()+":"+ prefix;
    }
}
