package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @GetMapping("/goods")
    @ResponseBody
    public List<Goods> goodsList() {
        return goodsService.getGoodsList();
    }

    @GetMapping("/goods/{id}")
    @ResponseBody
    public Goods getGoodById(@PathVariable int id) {
        return goodsService.getGoodById(id);
    }

    @GetMapping("/goods/{id}/sold")
    @ResponseBody
    public boolean reduceStockById(@PathVariable int id) {
        return goodsService.reduceStockById(id);
    }
}
