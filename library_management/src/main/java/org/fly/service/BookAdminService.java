package org.fly.service;

import org.fly.entity.BookAdmin;
import org.fly.entity.PageResult;

import java.util.List;

public interface BookAdminService {
    PageResult<BookAdmin> getPage(Integer page, Integer pageSize, String bookTitle, String barcode , Integer status);

    void update(BookAdmin bookAdmin);

    void add(BookAdmin bookAdmin);

    void deleteByIds(List<Integer> ids);

    BookAdmin getById(Integer id);
}
