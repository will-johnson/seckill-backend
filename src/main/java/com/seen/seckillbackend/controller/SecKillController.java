package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.domain.User;
import com.seen.seckillbackend.rabbitmq.MQSender;
import com.seen.seckillbackend.rabbitmq.SeckillMessage;
import com.seen.seckillbackend.redis.RedisService;
import com.seen.seckillbackend.redis.key.GoodsKeyPrefix;
import com.seen.seckillbackend.redis.key.OrderKeyPrefix;
import com.seen.seckillbackend.service.GoodsService;
import com.seen.seckillbackend.util.CodeMsg;
import com.seen.seckillbackend.util.Result;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;

public class SecKillController  implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;


    /**
     * 内存标记
     * key : goodsId
     * value : isOver
     */
    private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();

    /**
     * 系统初始化加载进redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<Goods> goodsList = goodsService.getGoodsList();
        if (null == goodsList) {
            return;
        }
        for (Goods goods : goodsList) {
            redisService.set(GoodsKeyPrefix.goodsStockPrefix, "" + goods.getId(), goods.getStock());
            localOverMap.put(goods.getId(), false);
        }
    }

    /**
     * 高并发访问接口
     * 秒杀接口地址隐藏
     */
    @GetMapping("/seckill/{goodsId}")
    public Result<Integer> seckill(User user, @PathVariable long goodsId) {
        if(user == null) {
            return Result.err(CodeMsg.USER_NOT_EXIST);
        }
        // 1.内存标记，减少redis访问
        Boolean isOver = localOverMap.get(goodsId);
        if (isOver) {
            return Result.err(CodeMsg.SECKILL_OVER);
        }

        // 2.预减库存
        Long decr = redisService.decr(GoodsKeyPrefix.goodsStockPrefix, "" + goodsId);
        if (decr <= 0) {
            localOverMap.put(goodsId, true);
            return Result.err(CodeMsg.SECKILL_OVER);
        }
        // 3.判断是否已经秒杀到了, 数据库已添加唯一索引
        SeckillOrder seckillOrder = redisService.get(OrderKeyPrefix.orderKeyPrefix, user.getUsername() + "_" + goodsId, SeckillOrder.class);
        if (null != seckillOrder) {
            return null;
        }

        // 4.入队
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsService.getGoodById(goodsId));
        sender.send(seckillMessage);
        return Result.success(0); //排队中

    }



    public Result<String> getSeckillPath(){
        return null;
    }
}
