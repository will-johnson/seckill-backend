package com.seen.seckillbackend.middleware.redis.key;

public abstract class BaseKeyPe implements KeyPe {
    private int expireSeconds;
    private String prefix;

    public BaseKeyPe(String prefix) {//0代表永不过期
        this(0, prefix);
    }

    public BaseKeyPe(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }


    @Override
    public int getExpireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        return this.getClass().getSimpleName()+":"+ prefix;
    }
}
