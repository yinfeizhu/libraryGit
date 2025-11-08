package org.fly.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 
 * @author fly
 * @since 2025
 */
@Data
public class OperateLog {

    private Integer id; // 日志ID，主键，自增长
    
    private Integer adminId; // 操作管理员ID，逻辑外键关联管理员表
    
    private Integer operationTarget; // 操作目标：0其它, 1管理员图书, 2读者图书, 3借阅记录, 4罚款记录, 5用户信息
    
    private Integer operationType; // 1新增2删除3修改4还书5借书
    
    private String ipAddress; // 操作IP地址
    
    private String returnResult; // 返回结果

    private String methodParams;// 参数

    private LocalDateTime operationTime; // 操作时间，默认当前时间
    
    private Long costTime; // 操作耗时，ms


    //封装管理员名称
    private String operatorName;
}