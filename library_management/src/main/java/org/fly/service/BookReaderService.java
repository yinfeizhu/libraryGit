package org.fly.service;

import org.fly.entity.BookReaderParams;
import org.fly.entity.BookReader;
import org.fly.entity.PageResult;
import java.util.List;

public interface BookReaderService {

    PageResult<BookReader> getPage(BookReaderParams params);

    void update(BookReader bookReader) throws Exception;

    void add(BookReader bookReader) throws Exception;

    void deleteByIds(List<Integer> ids);

    BookReader getById(Integer id);
}
