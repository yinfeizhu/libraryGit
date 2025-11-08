package org.fly.controller;


import lombok.extern.slf4j.Slf4j;
import org.fly.entity.OperateLog;
import org.fly.entity.PageResult;
import org.fly.entity.Result;
import org.fly.service.OperatorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/operatorLog")
public class OperatorLogController {
    @Autowired
    private OperatorLogService operatorLogService;

    //分页查询
    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("日志查询参数：{},{}", page, pageSize);
        PageResult<OperateLog> pageResult =operatorLogService.selectPage(page, pageSize);
        return Result.success(pageResult);
    }
    //删除day天前的日志
    @DeleteMapping("/{day}")
    public Result delete(@PathVariable Integer day) {
        log.info("删除{}天前的日志参数：", day);
        operatorLogService.cleanExpiredLogsManual(day);
        return Result.success();
    }


}
