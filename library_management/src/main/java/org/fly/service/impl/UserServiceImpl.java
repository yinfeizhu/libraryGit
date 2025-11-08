package org.fly.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.fly.entity.*;
import org.fly.mapper.UserMapper;
import org.fly.service.UserService;
import org.fly.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<User> selectPage(Integer page, Integer pageSize, String name, Integer readerType) {
        //1.设置分页参数
        PageHelper.startPage(page, pageSize);
        //2.执行查询
        List<User> rows = userMapper.selectPage(name, readerType);
        //3.封装结果
        Page<User> p = (Page<User>) rows;
        return new PageResult<User>(p.getTotal(), p.getResult());
    }

    @Override
    public void add(User user) throws Exception{
        //1.设置创建时间和更新时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        //如果status为空的话，设置默认值0，否则使用用户传递的值
        if (user.getStatus() == null) {
            user.setStatus(0);
        }
        //如果role为空，设置默认值1，否则使用用户传递的值
        if (user.getRole() == null) {
            user.setRole(1);
        }
        //2.调用readerMapper添加
        userMapper.add(user);
    }

    @Override
    public void update(User user) throws Exception {
        //1.设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        //2.调用readerMapper修改
        log.info("修改的完整参数：{}", user);
        userMapper.update(user);
    }

    @Override
    public void deleteByIds(List<Integer> ids) {
        userMapper.deleteByIds(ids);
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }

    @Transactional
    @Override
    public LoginInfo login(User user) {
        //1.根据用户名和密码查询用户
        User dbUser = userMapper.getByUserData(user);
        //2.判断用户是否存在,如果存在
        if (dbUser != null) {
            //判断用户是否冻结
            if (dbUser.getStatus() == 0) {
                throw new RuntimeException("用户被冻结");
            }
            //3.生成jwt 令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", dbUser.getId());
            claims.put("username", dbUser.getUserName());
            claims.put("role", dbUser.getRole());
            String jwt = JwtUtils.generateToken(claims);
            log.info("生成令牌：{}", jwt);
            log.info("员工登录成功，员工响应数据：{},{},{},{},{}", dbUser.getId(), dbUser.getUserName(), dbUser.getName(), dbUser.getRole(), jwt);
            return new LoginInfo(dbUser.getId(), dbUser.getUserName(), dbUser.getName(), dbUser.getRole(), jwt);
        }
        //4.员工不存在，返回null
        return null;
    }

    //修改密码
    @Override
    public void updatePassword(ChangePassword changePassword) {
        //1.根据id查询用户
        User user = userMapper.getById(changePassword.getId());
        if (user != null && user.getPassword().equals(changePassword.getOldPassword())) {
            //2.设置新密码
            user.setPassword(changePassword.getNewPassword());
            //3.调用mapper修改
            userMapper.update(user);
        }else{
            //4.密码错误
            throw new RuntimeException("旧密码错误");
        }
    }
}
