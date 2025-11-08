package org.fly.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.fly.entity.BookAdmin;
import org.fly.entity.BorrowRecord;
import org.fly.entity.PageResult;
import org.fly.mapper.BookAdminMapper;
import org.fly.mapper.BorrowRecordMapper;
import org.fly.service.BorrowRecordService;
import org.fly.utils.CurrentHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    @Autowired
    private BookAdminMapper bookAdminMapper;
    @Autowired
    private FineRecordHandler fineRecordHandler;
    //分页查询
    @Override
    public PageResult<BorrowRecord> selectPage(Integer page, Integer pageSize, String readerName, String bookBarcode, String bookTitle) {
        //1.设置分页参数
        PageHelper.startPage(page, pageSize);
        //2.执行查询
        List<BorrowRecord> rows = borrowRecordMapper.selectPage(readerName, bookBarcode, bookTitle);
        //3.返回结果
        Page<BorrowRecord> p = (Page<BorrowRecord>) rows;
        return new PageResult<BorrowRecord>(p.getTotal(), p.getResult());
    }

    //添加
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void add(BorrowRecord borrowRecord) {
        //1.设置创建时间和更新时间,以及操作员id
        borrowRecord.setBorrowTime(LocalDateTime.now());
        //应还时间如果为空，则设置当前时间+30天
        if (borrowRecord.getDueTime() == null) {
            borrowRecord.setDueTime(LocalDateTime.now().plusDays(30));
        }else{
            borrowRecord.setDueTime(borrowRecord.getDueTime());
        }
        //设置操作员id
        borrowRecord.setOperatorId(CurrentHolder.getCurrentUserId());
        //2.调用mapper添加
        borrowRecordMapper.add(borrowRecord);
        //3.更新对应图书的状态
        //3.1获取图书id
        Integer bookId = borrowRecord.getBookAdminId();
        //3.2根据id查询图书
        BookAdmin book = bookAdminMapper.getById(bookId);
        //3.3获取图书状态
        if (book != null&&book.getStatus() == 1) {
            book.setStatus(2);
        }
        //3.4更新图书
        bookAdminMapper.update(book);
    }

    //批量删除
    @Override
    public void deleteByIds(List<Integer> ids) {
        borrowRecordMapper.deleteByIds(ids);
    }

    // 根据id查询
    @Override
    public BorrowRecord getById(Integer id) {
        return borrowRecordMapper.getById(id);
    }

    //更新还书时间
    @Transactional(rollbackFor = {Exception.class}) //事务管理的注解 --  默认抛出运行异常RuntimeException才会回滚
    //    (rollbackFor = {Exception.class})加上后所有异常都会回滚
    @Override
    public void update(Integer id) throws Exception {
       try {
           //1.根据id查询借阅记录
           BorrowRecord record = borrowRecordMapper.getById(id);
           //2.1设置还书时间
           record.setReturnTime(LocalDateTime.now());
           //2.2设置操作员id
           record.setOperatorId(CurrentHolder.getCurrentUserId());
           //3.调用mapper更新
           borrowRecordMapper.update(record);
           //3.更新对应图书的状态
           //3.1获取图书id，存在baId中
           Integer baId = record.getBookAdminId();
           //3.2根据图书id查询图书
           BookAdmin book = bookAdminMapper.getById(baId);
           if (book == null) {
               throw new RuntimeException("未找到对应的图书信息");
           }
           //3.3获取图书状态,如果是2借出状态，则改为1在架
           Integer status = book.getStatus();
           if (status == 2) {
               book.setStatus(1);
               log.info("图书{}已还，更新图书编号为{}的状态为在架", book.getBookTitle(), book.getBarcode());
           } else {
               throw new RuntimeException("图书不是借出状态");
           }
           //3.6更新图书状态
           bookAdminMapper.update(book);
           //4.调用方法创建罚款记录
           if (record.getDueTime().isBefore(LocalDateTime.now())) {
               fineRecordHandler.createFineRecord(record);
           }

       }catch (Exception e){
           log.error("创建罚款记录失败", e);
           throw new RuntimeException(e.getMessage());
       }
    }
}
