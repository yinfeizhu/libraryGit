package org.fly.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfo {
    private Integer id;//主键id
    private String username;//用户名
    private String name;// 姓名
    private Integer role;//角色：1管理员/2读者
    private String token;// token
}
