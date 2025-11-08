package org.fly.controller;


import lombok.extern.slf4j.Slf4j;
import org.fly.common.anno.Log;
import org.fly.entity.FineRecord;
import org.fly.entity.PageResult;
import org.fly.entity.Result;
import org.fly.service.FineRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/fineRecord")
public class FineRecordController {

    @Autowired
    private FineRecordService fineRecordService;

    //分页查询
    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                        String readerName,String readerPhone,String bookBarcode){
        log.info("查询罚款记录的参数：{},{},{},{},{}", page, pageSize,readerName, readerPhone,bookBarcode);
        PageResult<FineRecord> pageResult = fineRecordService.selectPage(page, pageSize,readerName, readerPhone,bookBarcode);
        return Result.success(pageResult);
    }
    //添加
    @Log
    @PostMapping
    public Result add(@RequestBody FineRecord fineRecord) throws Exception {
        log.info("添加罚款记录：{}", fineRecord);
        fineRecordService.add(fineRecord);
        return Result.success();
    }
    //修改
    @Log
    @PutMapping
    public Result update(@RequestBody FineRecord fineRecord) throws Exception {
        log.info("修改罚款记录：{}", fineRecord);
        fineRecordService.update(fineRecord);
        return Result.success();
    }
    //批量删除
    @Log
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids){
        log.info("删除罚款记录：{}", ids);
        fineRecordService.delete(ids);
        return Result.success();
    }

    //根据ID查询
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("查询罚款记录：{}", id);
        FineRecord fineRecord = fineRecordService.getById(id);
        return Result.success(fineRecord);
    }
}
