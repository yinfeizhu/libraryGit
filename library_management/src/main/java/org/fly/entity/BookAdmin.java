package org.fly.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookAdmin {
    private Integer id;//主键id
    private String bookCode;//图书元编号
    private String author;//作者
    private String barcode;//图书条形码
    private double price;// 图书价格
    private Integer status;// 图书状态  1.在架 2.借出 3.遗失 4.损坏
    private LocalDate publishDate;//出版日期
    private String supplier;// 供应商
    private Integer updateAdmin;// 最后操作管理员
    private LocalDateTime createTime;// 创建时间
    private LocalDateTime updateTime;// 更新时间

    //封装操作员姓名
    private String operatorName;
    //封装书籍名称
    private String bookTitle;
    //封装书籍位置
    private String bookLocation;
}
