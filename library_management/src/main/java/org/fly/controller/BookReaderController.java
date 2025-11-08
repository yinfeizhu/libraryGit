package org.fly.controller;

import lombok.extern.slf4j.Slf4j;
import org.fly.common.anno.Log;
import org.fly.entity.BookReaderParams;
import org.fly.entity.BookReader;
import org.fly.entity.PageResult;
import org.fly.entity.Result;
import org.fly.service.BookReaderService;
import org.fly.utils.AliyunOSSOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookReader")
public class BookReaderController {

    @Autowired
    private BookReaderService bookReaderService;

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws Exception {
        log.info("文件上传: {}", file.getOriginalFilename());

        //将文件交给OSS存储管理
        String url = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename());
        log.info("文件上传OSS, url: {}", url);

        return Result.success(url);
    }

    // 分页查询
    @GetMapping
    public Result page(BookReaderParams params) {
        log.info("查询参数：{}", params);
        PageResult<BookReader> pageResult = bookReaderService.getPage(params);
        return Result.success(pageResult);
    }

    //修改
    @Log
    @PutMapping
    public Result update(@RequestBody BookReader bookReader) throws Exception {
        log.info("修改图书信息：{}", bookReader);
        bookReaderService.update(bookReader);
        return Result.success();
    }
    //添加
    @Log
    @PostMapping
    public Result add(@RequestBody BookReader bookReader) throws Exception {
        log.info("添加图书信息：{}", bookReader);
        bookReaderService.add(bookReader);
        return Result.success();
    }

    //批量删除
    @Log
    @DeleteMapping
    public Result deleteByIds(@RequestParam List<Integer> ids) {
        log.info("删除图书信息：{}", ids);
        bookReaderService.deleteByIds(ids);
        return Result.success();
    }
    //根据id查询
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("查询图书信息：{}", id);
        BookReader bookReader = bookReaderService.getById(id);
        return Result.success(bookReader);
    }
}
