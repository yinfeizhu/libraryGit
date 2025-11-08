package org.fly.entity;

import lombok.Data;

@Data
public class ChangePassword {
    private Integer id;
    private String oldPassword;
    private String newPassword;
}
