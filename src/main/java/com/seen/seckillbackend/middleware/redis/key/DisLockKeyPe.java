package com.seen.seckillbackend.middleware.redis.key;

public class DisLockKeyPe extends BaseKeyPe {

    public static DisLockKeyPe disLockKeyPe = new DisLockKeyPe("DistriLock");

    public DisLockKeyPe(String prefix) {
        super(5, prefix);
    }

    public DisLockKeyPe(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
