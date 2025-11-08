package org.fly.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.fly.entity.BookAdmin;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookAdminMapper {
    List<BookAdmin> listPage(String bookTitle, String barcode, Integer status);

    void update(BookAdmin bookAdmin);

    void add(BookAdmin bookAdmin);

    void deleteByIds(List<Integer> ids);

    BookAdmin getById(Integer id);

    List<Map<String, Object>> selectBookStatusData();

    Object getBookCount();

    Object getNewBookCount();
}
