package org.fly.entity;

import lombok.Data;

@Data
public class BookReaderParams {
    // 查询参数
    private Integer page = 1;// 当前页码
    private Integer pageSize = 10;// 每页显示的记录数
    private String bookTitle;// 书籍标题
    private String category;// 类别
}
