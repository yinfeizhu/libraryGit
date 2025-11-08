package org.fly.entity;

@lombok.Data
public class HotBook {
    private String bookTitle;// 书名
    private String bookAuthor;// 作者
    private Integer count;// 借阅次数
    private String bookCover;// 封面
    private String bookDescription;// 描述
}
