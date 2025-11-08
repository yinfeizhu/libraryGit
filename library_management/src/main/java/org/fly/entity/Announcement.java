package org.fly.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Announcement {
    private Integer id;
    private String subject;
    private String content;
    private Integer adminId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 添加其他属性
    private String adminName;
}
