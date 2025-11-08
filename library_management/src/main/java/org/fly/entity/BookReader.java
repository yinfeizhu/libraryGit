package org.fly.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书元信息实体类 - 用户使用
 * 
 * @author fly
 * @since 2025
 */
@Data
public class BookReader {

    private Integer id;// 图书ID
    private String code;// 书籍编码
    private String title;// 书名
    private String coverImage;// 封面图片
    private String location;// 位置
    private String description;// 描述
    private String publisher;// 出版社
    private LocalDate acquireDate;// 上架日期
    private String category;// 类别
    private LocalDateTime createTime;// 创建时间
    private LocalDateTime updateTime;// 更新时间

    //作者也是关联表中获取
    private String bookAuthor;
    //统计书籍可用 数量
    private Integer availableNum;
}