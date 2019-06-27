package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.common.access.AccessLimit;
import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.middleware.rabbitmq.MQSender;
import com.seen.seckillbackend.middleware.rabbitmq.SeckillMessage;
import com.seen.seckillbackend.middleware.redis.single.RedisService;
import com.seen.seckillbackend.middleware.redis.key.GoodsKeyPrefix;
import com.seen.seckillbackend.middleware.redis.key.OrderKeyPrefix;
import com.seen.seckillbackend.service.GoodsService;
import com.seen.seckillbackend.service.SeckillService;
import com.seen.seckillbackend.service.UserService;
import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

@Controller
@Slf4j
public class SecKillController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @Autowired
    SeckillService orderService;

    @Autowired
    UserService userService;


    /**
     * 内存标记Map
     * key : goodsId
     * value : isOver, true is over.
     */
    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * 系统初始化, 秒杀商品库存加载进redis
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        orderService.reset();
        goodsService.reset();
        redisService.deleteAll();
        List<Goods> goodsList = goodsService.getGoodsList();
        if (null == goodsList) {
            return;
        }
        for (Goods goods : goodsList) {
            long stock = goodsService.getGoodsStockById(goods.getId());
            redisService.set(GoodsKeyPrefix.goodsStockPrefix, "" + goods.getId(), (int)(stock * 1.5));
            localOverMap.put(goods.getId(), false);
        }
    }

    /**
     * 高并发访问接口
     */
    @AccessLimit(seconds = 5, maxCount = 5)
    @GetMapping("/seckill/{goodsId}")
    @ResponseBody
    public Result<Integer> seckill(Long userId, @PathVariable long goodsId) {
        if (userId == null) {
            log.info("用户未登录");
            return Result.err(CodeMsg.USER_NEEDS_LOGIN);
        }

        // 1.内存标记，减少redis访问
        if (localOverMap.get(goodsId)) {
            return Result.err(CodeMsg.SECKILL_OVER);
        }

        /**
         * 2.预减库存
         * 2. 先判断是否已经秒杀过了
         * 3. 预减库存
         * 4. 入队
         */
        SeckillOrder seckillOrder = redisService.get(OrderKeyPrefix.orderKeyPrefix, userId + "_" + goodsId, SeckillOrder.class);

        if (null != seckillOrder) {
            log.error("错误：重复购买");
            return null;
        } else {
            // 预减库存
            Long decr = redisService.decr(GoodsKeyPrefix.goodsStockPrefix, "" + goodsId);
            if (decr < 0) {
                localOverMap.put(goodsId, true);
                return Result.err(CodeMsg.SECKILL_OVER);
            }
            log.info("预减库存成功");
        }

        // 4.入队
        SeckillMessage seckillMessage = new SeckillMessage(userId, goodsService.getGoodById(goodsId));
        sender.send(seckillMessage);
        return Result.success(0); //排队中
    }

}
