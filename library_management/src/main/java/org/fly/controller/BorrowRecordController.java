package org.fly.controller;

import lombok.extern.slf4j.Slf4j;
import org.fly.common.anno.Log;
import org.fly.entity.BorrowRecord;
import org.fly.entity.PageResult;
import org.fly.entity.Result;
import org.fly.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/borrowRecord")
public class BorrowRecordController {
    @Autowired
    private BorrowRecordService borrowRecordService;

    //分页查询
    @GetMapping
    public Result selectPage(@RequestParam(defaultValue = "1")Integer page,
                             @RequestParam(defaultValue = "10")Integer pageSize,
                             String readerName, String bookBarcode, String bookTitle) {
        log.info("查询参数：{},{},{},{},{}", page, pageSize, readerName, bookBarcode,bookTitle);
        PageResult<BorrowRecord> pageResult = borrowRecordService.selectPage(page, pageSize, readerName, bookBarcode, bookTitle);
        return Result.success(pageResult);
    }
    //借书
    @Log
    @PostMapping("/borrow")
    public Result borrowBook(@RequestBody BorrowRecord borrowRecord) {
        log.info("添加参数：{}", borrowRecord);
        borrowRecordService.add(borrowRecord);
        return Result.success();
    }
    //还书
    @Log
    @PutMapping("/return/{id}")
    public Result returnBook(@PathVariable Integer id) throws Exception{
        log.info("进行还书操作，{}", id );
        borrowRecordService.update(id);
        return Result.success();
    }
    //删除
    @Log
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids){
        log.info("删除参数：{}", ids);
        borrowRecordService.deleteByIds(ids);
        return Result.success();
    }

    //根据id查询
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("查询参数：{}", id);
        BorrowRecord borrowRecord = borrowRecordService.getById(id);
        return Result.success(borrowRecord);
    }
}
