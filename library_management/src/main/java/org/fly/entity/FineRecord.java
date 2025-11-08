package org.fly.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 罚款记录实体类
 * 
 * @author fly
 * @since 2025
 */
@Data
public class FineRecord {
    
    /**
     * 罚款原因枚举
     */
    @Getter
    public enum FineReason {
        OVERDUE(1, "逾期"),
        DAMAGE(2, "损坏"),
        LOST(3, "遗失");
        
        private final int code;
        private final String description;
        
        FineReason(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static FineReason fromCode(Integer code) {
            if (code == null) return null;
            for (FineReason reason : values()) {
                if (reason.code == code) {
                    return reason;
                }
            }
            return null;
        }
    }

    private Integer id; // 罚款ID
    private Integer readerId; // 读者ID
    private Integer borrowId;// 借阅记录 Id
    private BigDecimal amount; // 罚款金额
    private FineReason reason; // 罚款原因
    private Integer status; // 罚款状态，默认未支付
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

    //封装罚款读者
    private String readerName;
    //封装罚款读者手机号
    private String readerPhone;
    //封装罚款图书条码号
    private String bookBarcode;

    /**
     * 设置罚款原因（通过code）
     */
    public void setReasonByCode(Integer code) {
        this.reason = FineReason.fromCode(code);
    }
    
    /**
     * 获取罚款原因code
     */
    public Integer getReasonCode() {
        return reason != null ? reason.getCode() : null;
    }
    /**
     * 设置罚款原因code（用于接收前端参数）因为前端把reasonCode作为参数传递的，所以需要设置转化为code
     * 如果用reason传递参数就不需要设置
     */
//    public void setReasonCode(Integer reasonCode) {
//        this.reason = FineReason.fromCode(reasonCode);
//    }


}