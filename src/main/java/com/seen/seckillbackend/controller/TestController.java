package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.middleware.redis.cluster.RedisClusterService;
import com.seen.seckillbackend.middleware.redis.key.GoodsKeyPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @Autowired
    RedisClusterService redisClusterService;

    @GetMapping("/redis_cluster")
    @ResponseBody
    public boolean redisCluster() {
        for (int i = 0; i < 10; i++) {
            redisClusterService.set(GoodsKeyPrefix.goodsStockPrefix, String.valueOf(i), i);
        }
        return true;
    }

    @GetMapping("/flush")
    @ResponseBody
    public boolean flush(){
        redisClusterService.deleteAll();
        return true;
    }
}
