package org.fly.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 读者信息实体类
 * 
 * @author fly
 * @since 2025
 */
@Data
public class User {
    private Integer id; // 读者ID，主键，自增长
    private String userName; // 读者证号，唯一，不能为空
    private String password; // 登录密码，不能为空
    private String name; // 姓名，不能为空
    private Integer gender; // 性别：1男/2女
    private String phone; // 电话
    private String address; // 地址
    private Integer readerType; // 读者类型：1学生/2教师/3职工/4其他
    private Integer borrowedNum; // 当前借书数量，默认0
    private LocalDateTime createTime; // 注册时间，默认当前时间
    private LocalDateTime updateTime; // 更新时间，默认当前时间
    private Integer status; // 状态：1正常/0冻结
    private Integer role;//角色 1普通用户 2管理员 3超级管理员
}