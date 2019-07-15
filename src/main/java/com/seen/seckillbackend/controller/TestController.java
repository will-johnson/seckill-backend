package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.common.response.Result;
import com.seen.seckillbackend.middleware.redis.cluster.RedisClusterService;
import com.seen.seckillbackend.middleware.redis.key.GoodsKeyPe;
import com.seen.seckillbackend.middleware.redis.sentinel.SentinelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    @Autowired
    SentinelService sentinelService;

    @Autowired
    RedisClusterService redisClusterService;

    @GetMapping("/cluster")
    public String cluster(){
        return redisClusterService.set("stephen", "curry");
    }

    @GetMapping("/sentinel")
    public Boolean sentinelTest() {
        return sentinelService.set(GoodsKeyPe.goodsKeyPe, "will", "johnson");
    }


    @GetMapping("/exception")
    public Result<String> excep() {
        try {
            int i = 0;
            int j = 10 / i;
        } catch (Exception e) {
           throw e;
        }
        return Result.success("123");
    }
}
