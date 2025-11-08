package org.fly.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.fly.entity.BorrowRecord;
import org.fly.entity.FineRecord;
import org.fly.entity.PageResult;
import org.fly.mapper.BorrowRecordMapper;
import org.fly.mapper.FineRecordMapper;
import org.fly.service.FineRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FineRecordServiceImpl implements FineRecordService {

    @Autowired
    private FineRecordMapper fineRecordMapper;
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    @Autowired
    private FineRecordHandler fineRecordHandler;

    @Override
    public PageResult<FineRecord> selectPage(Integer page, Integer pageSize,
                                             String readerName, String readerPhone, String bookBarcode) {
        //1.设置分页参数
        PageHelper.startPage(page, pageSize);
        //2.执行查询
        List<FineRecord> rows = fineRecordMapper.selectPage(readerName,readerPhone, bookBarcode);
        //3.返回结果
        Page<FineRecord> p = (Page<FineRecord>) rows;
        return new PageResult<FineRecord>(p.getTotal(), p.getResult());
    }

    @Override
    public void add(FineRecord fineRecord) {
        //1.设置创建时间和更新时间
        fineRecord.setCreateTime(LocalDateTime.now());
        fineRecord.setUpdateTime(LocalDateTime.now());
        //2.调用mapper添加
        fineRecordMapper.add(fineRecord);
    }

    @Override
    public void update(FineRecord fineRecord) {
        //1.设置更新时间
        fineRecord.setUpdateTime(LocalDateTime.now());
        //2.调用mapper更新
        fineRecordMapper.update(fineRecord);
    }

    @Override
    public void delete(List<Integer> ids) {
        fineRecordMapper.deleteByIds(ids);
    }

    @Override
    public FineRecord getById(Integer id) {
        return fineRecordMapper.getById(id);
    }

    /**
     * 定期检测借阅记录超期，并生成罚款记录
     */
    @Transactional(rollbackFor = {Exception.class})
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkOverdue() {
        //1.查询所有的借阅记录
        List<BorrowRecord> records = borrowRecordMapper.selectPage(null, null, null);
        for (BorrowRecord record : records) {
            //1.判断还书时间是否为空，处理为空的情况
            if (record.getReturnTime() == null && record.getDueTime().isBefore(LocalDateTime.now())) {
                try {
                    fineRecordHandler.createFineRecord(record);
                } catch (Exception e) {
                    log.error("创建罚款记录失败，记录ID: {}", record.getId(), e);
                    // 继续处理下一个记录
                }
            }

        }
    }

}
