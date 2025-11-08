package org.fly.controller;


import lombok.extern.slf4j.Slf4j;
import org.fly.common.anno.Log;
import org.fly.entity.BookAdmin;
import org.fly.entity.PageResult;
import org.fly.entity.Result;
import org.fly.service.BookAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookAdmin")
public class BookAdminController {

    @Autowired
    private BookAdminService bookAdminService;

    //分页查询
    @GetMapping
    public Result  page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                        String bookTitle, String barcode,
                        Integer status) {
        log.info("查询参数：{},{},{},{},{}", page, pageSize, bookTitle, barcode, status );
        PageResult<BookAdmin> pageResult = bookAdminService.getPage( page, pageSize, bookTitle, barcode, status);
        return Result.success(pageResult);
    }

    //修改
    @Log
    @PutMapping
    public Result update(@RequestBody BookAdmin bookAdmin) throws Exception {
        log.info("修改图书信息：{}", bookAdmin);
        bookAdminService.update(bookAdmin);
        return Result.success();
    }
    //添加
    @Log
    @PostMapping
    public Result add(@RequestBody BookAdmin bookAdmin) throws Exception {
        log.info("添加图书信息：{}", bookAdmin);
        bookAdminService.add(bookAdmin);
        return Result.success();
    }
    //批量删除
    @Log
    @DeleteMapping
    public Result deleteByIds(@RequestParam List<Integer> ids) {
        log.info("删除图书信息：{}", ids);
        bookAdminService.deleteByIds(ids);
        return Result.success();
    }
    //根据id查询
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("查询图书信息：{}", id);
        BookAdmin bookAdmin = bookAdminService.getById(id);
        return Result.success(bookAdmin);
    }
}
