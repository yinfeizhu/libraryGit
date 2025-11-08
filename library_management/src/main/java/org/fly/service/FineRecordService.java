package org.fly.service;

import org.fly.entity.FineRecord;
import org.fly.entity.PageResult;

import java.util.List;

public interface FineRecordService {
    PageResult<FineRecord> selectPage(Integer page, Integer pageSize, String readerName,String readerPhone, String bookBarCode);

    void add(FineRecord fineRecord);

    void update(FineRecord fineRecord);

    void delete(List<Integer> ids);

    FineRecord getById(Integer id);
}
