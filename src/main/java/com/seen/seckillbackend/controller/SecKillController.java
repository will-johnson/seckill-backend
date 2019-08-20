package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.Result;
import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.middleware.redis.key.AccessKeyPe;
import com.seen.seckillbackend.middleware.redis.key.GoodsKeyPe;
import com.seen.seckillbackend.middleware.redis.key.OrderKeyPe;
import com.seen.seckillbackend.middleware.redis.single.RedisService;
import com.seen.seckillbackend.service.GoodsService;
import com.seen.seckillbackend.service.SeckillService;
import com.seen.seckillbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Slf4j
public class SecKillController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    SeckillService orderService;

    @Autowired
    UserService userService;


    /**
     * 系统初始化, 秒杀商品库存加载进redis
     */
    @Override
    public void afterPropertiesSet() {
        orderService.resetDatabaseOrder();
        goodsService.resetDatabaseGoods();
        redisService.delPrefix(AccessKeyPe.accessKeyPe(1));
        redisService.delPrefix(GoodsKeyPe.goodsKeyPe);
        redisService.delPrefix(OrderKeyPe.orderKeyPe);

        List<Goods> goodsList = goodsService.getGoodsList();
        if (null == goodsList) {
            return;
        }
        for (Goods goods : goodsList) {
            long stock = goodsService.getGoodsStockById(goods.getId());
            redisService.set(GoodsKeyPe.goodsKeyPe, "" + goods.getId(), stock);
            log.info("redis预缓存 GoodsId:{}, Stock:{}", goods.getId(), stock);
            seckillService.reSetLocalMap(goods.getId());
        }
    }

    /**
     * 高并发访问接口
     */
//    @AccessLimit(seconds = 10, maxCount = 100)
    @GetMapping("/seckill/{goodsId}")
    @ResponseBody
    public Result<Integer> seckill(Long userId, @PathVariable Long goodsId) {
        if (userId == null) {
            log.info("用户未登录");
            return Result.err(CodeMsg.USER_NEEDS_LOGIN);
        }
        log.info("用户：{} 开始秒杀", userId);
        seckillService.preSeckill(userId, goodsId);
        return Result.success(0);
    }
}
