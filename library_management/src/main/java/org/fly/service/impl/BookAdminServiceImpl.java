package org.fly.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.fly.entity.BookAdmin;
import org.fly.entity.PageResult;
import org.fly.mapper.BookAdminMapper;
import org.fly.service.BookAdminService;
import org.fly.utils.CurrentHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookAdminServiceImpl implements BookAdminService {

    @Autowired
    private BookAdminMapper bookAdminMapper;

    //分页查询
    @Override
    public PageResult<BookAdmin> getPage(Integer page, Integer pageSize, String bookTitle, String barcode ,Integer status) {
        //1.设置分页参数
        PageHelper.startPage(page, pageSize);
        //2.查询数据
        List<BookAdmin> rows = bookAdminMapper.listPage(bookTitle, barcode, status);
        //3.使用Page读取数据，Page提供了getTotal()和getResult()方法,获取总记录数和数据行
        Page<BookAdmin> p = (Page<BookAdmin>) rows;
        return new PageResult<BookAdmin>(p.getTotal(), p.getResult());
    }

    @Override
    public void update(BookAdmin bookAdmin) {
        //1.设置更新时间
        bookAdmin.setUpdateTime(LocalDateTime.now());
        //2.设置最后操作管理员
        bookAdmin.setUpdateAdmin(CurrentHolder.getCurrentUserId());
        //3.调用bookAdminMapper更新
        bookAdminMapper.update(bookAdmin);
    }

    @Override
    public void add(BookAdmin bookAdmin) {
        //1.设置创建时间和更新时间
        bookAdmin.setCreateTime(LocalDateTime.now());
        bookAdmin.setUpdateTime(LocalDateTime.now());
        //2.设置最后操作管理员
        bookAdmin.setUpdateAdmin(CurrentHolder.getCurrentUserId());
        //3.调用bookAdminMapper添加
        bookAdminMapper.add(bookAdmin);
    }

    @Override
    public void deleteByIds(List<Integer> ids) {
        bookAdminMapper.deleteByIds(ids);
    }

    @Override
    public BookAdmin getById(Integer id) {
        return bookAdminMapper.getById(id);
    }
}
