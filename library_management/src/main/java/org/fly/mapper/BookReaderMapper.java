package org.fly.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.fly.entity.BookReaderParams;
import org.fly.entity.BookReader;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookReaderMapper {
    //分页查询
    List<BookReader> listPage(BookReaderParams params);

    //根据ID更新
    void updateById(BookReader bookReader);

    void add(BookReader bookReader);

    void deleteByIds(List<Integer> ids);

    BookReader getById(Integer id);

    List<Map<String, Object>> selectBookTypeData();
}
