package org.fly.service;

import org.fly.entity.ChangePassword;
import org.fly.entity.LoginInfo;
import org.fly.entity.PageResult;
import org.fly.entity.User;

import java.util.List;

public interface UserService {
    PageResult<User> selectPage(Integer page, Integer pageSize, String name, Integer readerType);

    void add(User user) throws Exception;

    void update(User user) throws Exception;

    void deleteByIds(List<Integer> ids);

    User getById(Integer id);

    LoginInfo login(User user);

    void updatePassword(ChangePassword changePassword);
}
