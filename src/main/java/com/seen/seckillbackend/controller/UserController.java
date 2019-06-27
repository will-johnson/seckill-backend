package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.domain.User;
import com.seen.seckillbackend.service.UserService;
import com.seen.seckillbackend.common.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
public class UserController {
    @Autowired
    UserService userService;


    @PostMapping("/login")
    @ResponseBody
    public Result<String> login(HttpServletResponse response, @RequestBody User user) {
        userService.login(response,user);
        return Result.success("登录成功");
    }

    /**
     * Reset
     * @return
     */
    @GetMapping("/login_all")
    @ResponseBody
    public Result<Integer> loginAll(HttpServletResponse response) {
        userService.loginAll(response);
        return Result.success(0);
    }

}
