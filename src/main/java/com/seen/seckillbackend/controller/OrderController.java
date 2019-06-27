package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.Result;
import com.seen.seckillbackend.service.OrderService;
import com.seen.seckillbackend.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/cancel/{orderId}")
    public Result<CodeMsg> cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }

}
