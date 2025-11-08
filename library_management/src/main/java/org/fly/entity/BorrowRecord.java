package org.fly.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 * 
 * @author fly
 * @since 2025
 */
@Data
public class BorrowRecord {
    
    private Integer id; // 借阅ID
    private Integer bookAdminId; // 图书ID
    private Integer readerId; // 读者ID
    private LocalDateTime borrowTime; // 借书时间
    private LocalDateTime dueTime; // 应还时间
    private LocalDateTime returnTime; // 实际归还时间
    private Integer operatorId; // 操作员ID


    //书名也是关联表中获取
    private String bookTitle;
    //用户电话
    private String readerPhone;
    //读者名也是关联表中获取
    private String readerName;
    //管理员
    private String operatorName;
    //书籍条码号
    private String bookBarcode;
}