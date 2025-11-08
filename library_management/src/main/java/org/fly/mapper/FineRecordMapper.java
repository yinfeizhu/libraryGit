package org.fly.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.fly.entity.FineRecord;

import java.util.List;
import java.util.Map;

@Mapper
public interface FineRecordMapper {
    List<FineRecord> selectPage(String readerName, String readerPhone, String bookBarCode);

    void add(FineRecord fineRecord);

    void update(FineRecord fineRecord);

    void deleteByIds(List<Integer> ids);

    FineRecord getById(Integer id);

    FineRecord getByBorrowId(Integer borrowId);

    List<Map<String, Object>> selectFineReasonData();

    List<Map<String, Object>> selectPayStatusData();
}
