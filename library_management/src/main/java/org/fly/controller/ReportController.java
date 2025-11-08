package org.fly.controller;


import lombok.extern.slf4j.Slf4j;
import org.fly.entity.CountNum;
import org.fly.entity.Result;
import org.fly.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    //统计用户性别
    @GetMapping("/getGenderCount")
    public Result getGenderCount() {
        CountNum countNum = reportService.getGenderData();
        log.info("统计用户性别：{}", countNum);
        return Result.success(countNum);
    }
    //统计读者的类型1学生/2教师/3职工/4其他
    @GetMapping("/getReaderTypeCount")
    public Result getReaderTypeCount() {
        CountNum countNum = reportService.getReaderTypeCount();
        log.info("统计读者类型：{}", countNum);
        return Result.success(countNum);
    }
    //统计每个类别有多少种书（不关心副本数量）
    @GetMapping("/getBookTypeCount")
    public Result getBookTypeCount() {
        CountNum countNum = reportService.getBookTypeCount();
        log.info("统计每个类别有多少种书：{}", countNum);
        return Result.success(countNum);
    }
    //统计书籍状态
    @GetMapping("/getBookStatusCount")
    public Result getBookStatusCount() {
        CountNum countNum = reportService.getBookStatusCount();
        log.info("统计书籍状态：{}", countNum);
        return Result.success(countNum);
    }
    //罚款原因统计
    @GetMapping("/getFineReasonCount")
    public Result getFineReasonCount() {
        CountNum countNum = reportService.getFineReasonCount();
        log.info("统计罚款原因：{}", countNum);
        return Result.success(countNum);
    }
    //支付状态统计
    @GetMapping("/getPayStatusCount")
    public Result getPayStatusCount() {
        CountNum countNum = reportService.getPayStatusCount();
        log.info("统计支付状态：{}", countNum);
        return Result.success(countNum);
    }
}
