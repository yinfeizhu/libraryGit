package org.fly.service;

import org.fly.entity.BorrowRecord;
import org.fly.entity.PageResult;

import java.util.List;

public interface BorrowRecordService {
    PageResult<BorrowRecord> selectPage(Integer page, Integer pageSize, String readerName, String bookBarcode, String bookTitle);

    void add(BorrowRecord borrowRecord);

    void deleteByIds(List<Integer> ids);

    BorrowRecord getById(Integer id);

    void update(Integer id) throws Exception;
}
