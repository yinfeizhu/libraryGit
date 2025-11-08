package org.fly.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.fly.entity.BookReaderParams;
import org.fly.entity.BookReader;
import org.fly.entity.PageResult;
import org.fly.mapper.BookReaderMapper;
import org.fly.service.BookReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class BookReaderServiceImpl implements BookReaderService {

    @Autowired
    private BookReaderMapper bookReaderMapper;

    @Override
    public PageResult<BookReader> getPage(BookReaderParams params) {
        //1.设置分页参数
        PageHelper.startPage(params.getPage(), params.getPageSize());
        //2.查询数据
        List<BookReader> rows = bookReaderMapper.listPage(params);
        //3.使用Page读取数据，Page提供了getTotal()和getResult()方法,获取总记录数和数据行
        Page<BookReader> p = (Page<BookReader>) rows;
        return new PageResult<BookReader>(p.getTotal(), p.getResult());
    }

    //修改
    @Override
    public void update(BookReader bookReader) throws Exception{
        //1.设置更新时间
        bookReader.setUpdateTime(LocalDateTime.now());
        //2.调用bookReaderMapper更新
        bookReaderMapper.updateById(bookReader);
    }

    //添加
    @Override
    public void add(BookReader bookReader) throws Exception{
        //1.设置创建时间和更新时间
        bookReader.setCreateTime(LocalDateTime.now());
        bookReader.setUpdateTime(LocalDateTime.now());
        //2.调用bookReaderMapper添加
        bookReaderMapper.add(bookReader);
    }

    @Override
    public void deleteByIds(List<Integer> ids) {
        bookReaderMapper.deleteByIds(ids);
    }

    @Override
    public BookReader getById(Integer id) {
        return  bookReaderMapper.getById(id);

    }
}