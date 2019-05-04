package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.domain.User;
import com.seen.seckillbackend.service.UserService;
import com.seen.seckillbackend.util.Result;
import com.seen.seckillbackend.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
    @Autowired
    UserService userService;


    @PostMapping("/login")
    @ResponseBody
    public Result<String> login(HttpServletResponse response, @RequestBody User user) {
        String token = userService.login(response,user);
        return Result.success(token);
    }
}
