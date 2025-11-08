package org.fly.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.fly.entity.BorrowRecord;
import org.fly.entity.HotBook;

import java.util.List;
import java.util.Map;

@Mapper
public interface BorrowRecordMapper {
    //分页查询
    List<BorrowRecord> selectPage(String readerName, String bookBarcode, String bookTitle);
    //添加
    void add(BorrowRecord borrowRecord);
    //删除
    void deleteByIds(List<Integer> ids);
    //根据id查询
    BorrowRecord getById(Integer id);
    //更新
    void update(BorrowRecord borrowRecord);
    //获取即将到期的借阅记录
    Object getSoonExpire();
    //获取热门借阅图书
    List<HotBook> getHotBookData();
    //获取超期借阅
    Object getOverdue();
}
