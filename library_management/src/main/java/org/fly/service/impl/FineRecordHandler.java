package org.fly.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.fly.entity.BorrowRecord;
import org.fly.entity.FineRecord;
import org.fly.mapper.FineRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class FineRecordHandler {
    
    @Autowired
    private FineRecordMapper fineRecordMapper;

    
    /**
     * 根据借阅记录计算超期天数之后在计算罚款金额,0.50元/天，不足一天的按1天计算
     */
    private BigDecimal calculateAmount(BorrowRecord record) {
        //1.如果现在时间晚于应还时间，计算超期的时间差
        if (record.getDueTime().isBefore(LocalDateTime.now())) {
            long between = LocalDateTime.now().toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli()
                    - record.getDueTime().toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli();
            //2.计算超期天数
            long days = (between + (24 * 60 * 60 * 1000 - 1)) / (24 * 60 * 60 * 1000);
            //3.计算罚款金额,0.50元/天，不足一天的按1天计算
            if (days > 0) {
              return  BigDecimal.valueOf(days * 0.5);
            }
        }
        return BigDecimal.ZERO;//0元


    }
    

    
    /**
     * 创建罚款记录
     */
    public void createFineRecord(BorrowRecord record) {
        //1.计算罚款金额
        BigDecimal fine = calculateAmount(record);
        //2.创建罚款记录
        FineRecord fineRecord = new FineRecord();
        //3.如果金额大于0，为记录赋值
        if (!fine.equals(BigDecimal.ZERO)) {
            fineRecord.setReaderId(record.getReaderId());
            fineRecord.setBorrowId(record.getId());
            fineRecord.setAmount(fine);
            fineRecord.setReasonByCode(1); // 超期罚款
            fineRecord.setStatus(1); // 待支付
            fineRecord.setCreateTime(LocalDateTime.now());
            fineRecord.setUpdateTime(LocalDateTime.now());

        }
        FineRecord existing = fineRecordMapper.getByBorrowId(record.getId());
        if (existing != null) {
            fineRecordMapper.update(fineRecord);
            log.info("该借阅记录已存在罚款记录,执行罚款更新，{}", fineRecord);
        }else{
            fineRecordMapper.add(fineRecord);
            log.info("创建罚款记录成功，{}", fineRecord);
        }

    }


}
