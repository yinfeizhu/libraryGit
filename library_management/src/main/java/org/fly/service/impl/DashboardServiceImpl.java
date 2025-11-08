package org.fly.service.impl;

import org.fly.entity.BookReader;
import org.fly.entity.BookReaderParams;
import org.fly.entity.HotBook;
import org.fly.mapper.BookAdminMapper;
import org.fly.mapper.BookReaderMapper;
import org.fly.mapper.BorrowRecordMapper;
import org.fly.mapper.UserMapper;
import org.fly.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private BookAdminMapper bookAdminMapper;
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    @Autowired
    private BookReaderMapper bookReaderMapper;
    @Autowired
    private UserMapper userMapper;
    //获取图书总数
    @Override
    public Object getBookCount() {
        return bookAdminMapper.getBookCount();
    }
    //即将到期的借阅记录
    @Override
    public Object getSoonExpire() {
        return borrowRecordMapper.getSoonExpire();
    }
    //热门借阅图书
    @Override
    public List<HotBook> getHotBookData() {
        return borrowRecordMapper.getHotBookData();
    }

    //推荐图书
    @Override
    public List<HotBook> getRecommendBookData() {
        List<HotBook> hotBooks = borrowRecordMapper.getHotBookData();
        List<HotBook> recommendBooks = new ArrayList<>();
        Set<String> recommendedTitles = new HashSet<>(); // 已推荐的书名
        Set<String> hotBookTitles = new HashSet<>();     // 热门图书标题集合（防止推荐热门书）

        // 提前收集所有热门图书标题
        if (hotBooks != null) {
            for (HotBook hb : hotBooks) {
                hotBookTitles.add(hb.getBookTitle());
            }
        }
        // 判断是否满足推荐条件
        if (hotBooks != null && !hotBooks.isEmpty()) {
            for (HotBook hotBook : hotBooks) {
                if (recommendBooks.size() >= 3) {
                    break;// 最多推荐3本
                }

                BookReaderParams params = new BookReaderParams();// 创建参数对象
                params.setBookTitle(hotBook.getBookTitle());// 设置参数书 名
                List<BookReader> books = bookReaderMapper.listPage(params);

                if (books != null && !books.isEmpty()) {
                    String category = books.get(0).getCategory();// 获取类别，get(0)是获取第一个类别
                    if (category != null && !category.isEmpty()) {
                        BookReaderParams categoryParams = new BookReaderParams();
                        categoryParams.setCategory(category);// 设置类别
                        categoryParams.setPageSize(10);
                        List<BookReader> sameCategoryBooks = bookReaderMapper.listPage(categoryParams);

                        for (BookReader book : sameCategoryBooks) {
                            String title = book.getTitle();
                            // 忽略当前热门图书自身
                            if (title.equals(hotBook.getBookTitle())) {
                                continue;
                            }
                            // 忽略其他热门图书（避免推荐热门书）
                            if (hotBookTitles.contains(title)) {
                                continue;
                            }
                            // 避免重复推荐
                            if (recommendedTitles.contains(title)) {
                                continue;
                            }

                            HotBook recommendBook = new HotBook();
                            recommendBook.setBookTitle(title);
                            recommendBook.setBookAuthor(book.getBookAuthor());
                            recommendBook.setBookCover(book.getCoverImage());
                            recommendBook.setBookDescription(book.getDescription());
                            recommendBooks.add(recommendBook);
                            recommendedTitles.add(title);// 记录已推荐
                            break; // 每个类别只推荐一本
                        }
                    }
                }
            }
        }

        return recommendBooks;
    }



    //获取超时借阅记录数
    @Override
    public Object getOverdue() {
        return borrowRecordMapper.getOverdue();
    }
    //获取新增用户数
    @Override
    public Object getNewUser() {
        return userMapper.getNewUser();
    }
    //获取新增图书数
    @Override
    public Object getNewBook() {
        return bookAdminMapper.getNewBookCount();
    }


}
