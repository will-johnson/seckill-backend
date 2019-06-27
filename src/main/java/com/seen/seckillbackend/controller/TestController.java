package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.common.response.Result;
import com.seen.seckillbackend.middleware.redis.cluster.RedisClusterService;
import com.seen.seckillbackend.middleware.redis.key.GoodsKeyPe;
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
            redisClusterService.set(GoodsKeyPe.goodsKeyPe, String.valueOf(i), i);
        }
        return true;
    }

    @GetMapping("/flush")
    @ResponseBody
    public boolean flush(){
        redisClusterService.deleteAll();
        return true;
    }

    @GetMapping("/exception")
    @ResponseBody
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
