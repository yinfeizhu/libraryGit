package org.fly.controller;

import lombok.extern.slf4j.Slf4j;
import org.fly.entity.BorrowRecord;
import org.fly.entity.HotBook;
import org.fly.entity.Result;
import org.fly.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    //获取图书总数
    @GetMapping("/bookCount")
    public Result getBookCount() {
        log.info("获取图书总数");
        return Result.success(dashboardService.getBookCount());
    }
    //即将到期的借阅记录
    @GetMapping("/soonExpire")
    public Result getSoonExpire() {
        log.info("获取即将到期的借阅记录");
        return Result.success(dashboardService.getSoonExpire());
    }
    //热门借阅图书
    @GetMapping("/hotBook")
    public Result getHotBook() {
        log.info("获取热门借阅图书");
        List<HotBook> hotBookList = dashboardService.getHotBookData();
        return Result.success(hotBookList);
    }

    //基于内容推荐图书
    @GetMapping("/recommendBook")
    public Result getRecommendBook() {
        log.info("基于内容推荐图书");
        List<HotBook> recommendBookList = dashboardService.getRecommendBookData();
        return Result.success(recommendBookList);
    }
    //逾期借阅记录数
    @GetMapping("/overdue")
    public Result getOverdue() {
        log.info("获取逾期借阅记录数");
        return Result.success(dashboardService.getOverdue());
    }
    //最近一周的新读者
    @GetMapping("/newUser")
    public Result getNewUser() {
        log.info("获取最近一周的新读者");
        return Result.success(dashboardService.getNewUser());
    }
    //最近一周的新增书籍数量
    @GetMapping("/newBook")
    public Result getNewBook() {
        log.info("获取最近一周的新增书籍数量");
        return Result.success(dashboardService.getNewBook());
    }

}
