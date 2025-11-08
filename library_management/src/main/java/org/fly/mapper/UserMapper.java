package org.fly.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.fly.entity.CountNum;
import org.fly.entity.User;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    //分页查询读者信息
    List<User> selectPage(String name, Integer readerType);

    //新增读者
    void add(User user);

    //修改读者信息
    void update(User user);

    //批量删除读者
    void deleteByIds(List<Integer> ids);

    //根据id查询用户信息
    User getById(Integer id);

    //根据用户名和密码查询用户-登录
    User getByUserData(User user);
    //统计用户性别
    List<Map<String, Object>> selectGenderData();
    //统计读者类型
    List<Map<String, Object>> selectUserTypeData();

    //获取新增用户数
    Object getNewUser();
}
