package org.fly.controller;


import lombok.extern.slf4j.Slf4j;
import org.fly.entity.LoginInfo;
import org.fly.entity.Result;
import org.fly.entity.User;
import org.fly.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    //登录
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        log.info("用户登录：{}", user);
        LoginInfo loginInfo = userService.login(user);
        if (loginInfo != null) {
            return Result.success(loginInfo);
        } else {
            return Result.error("用户名或密码错误");
        }

    }
    //注册
    @PostMapping("/register")
    public Result register(@RequestBody User user) throws Exception{
        log.info("用户注册：{}", user);
        userService.add(user);
        return Result.success();
    }
}
