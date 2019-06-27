package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController {

    @Autowired
    SeckillService orderService;
}
