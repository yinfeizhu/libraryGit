package org.fly.controller;


import lombok.extern.slf4j.Slf4j;
import org.fly.common.anno.Log;
import org.fly.entity.PageResult;
import org.fly.entity.User;
import org.fly.entity.Result;
import org.fly.entity.ChangePassword;
import org.fly.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    //分页查询用户信息
    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       String name, Integer readerType ) {
        log.info("查询参数：{},{},{},{}", page, pageSize, name, readerType);
        PageResult<User> pageResult = userService.selectPage(page, pageSize, name, readerType);
        return Result.success(pageResult);
    }
    //新增用户
    @Log
    @PostMapping
    public Result add(@RequestBody User user) throws Exception {
        log.info("新增用户：{}", user);
        userService.add(user);
        return Result.success();
    }
    //修改用户信息
    @Log
    @PutMapping
    public Result update(@RequestBody User user) throws Exception{
        log.info("修改用户信息：{}", user);
        userService.update(user);
        return Result.success();
    }
    //批量用户读者
    @Log
    @DeleteMapping
    public Result deleteByIds(@RequestParam List<Integer> ids) {
        log.info("删除用户：{}", ids);
        userService.deleteByIds(ids);
        return Result.success();
    }
    //根据id查询用户信息
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("查询用户信息：{}", id);
        User user = userService.getById(id);
        return Result.success(user);
    }
    //根据id修改密码
    @PutMapping("/password")
    public Result updatePassword(@RequestBody ChangePassword changePassword)
     throws Exception {
        log.info("修改密码：{}", changePassword);
        userService.updatePassword(changePassword);
        return Result.success();
    }

}
