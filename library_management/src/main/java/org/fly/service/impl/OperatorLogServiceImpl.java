package org.fly.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.fly.entity.OperateLog;
import org.fly.entity.PageResult;
import org.fly.mapper.OperatorLogMapper;
import org.fly.service.OperatorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OperatorLogServiceImpl implements OperatorLogService {
    @Autowired
    private OperatorLogMapper operatorLogMapper;
    @Override
    public void add(OperateLog olog) {
        operatorLogMapper.insert(olog);
    }

    @Override
    public PageResult<OperateLog> selectPage(Integer page, Integer pageSize) {
        //1.设置分页参数
        PageHelper.startPage(page, pageSize);
        //2.执行查询
        List<OperateLog> rows = operatorLogMapper.selectPage();
        //3.封装结果
        Page<OperateLog> p = (Page<OperateLog>) rows;
        return new PageResult<OperateLog>(p.getTotal(), p.getResult());
    }

    @Value("${schedule.log-cleanup.retention-days}")
    private int retentionDays;
    @Scheduled(cron = "${schedule.log-cleanup.cron}") // 秒 分 时 日 月 星期
    public void cleanExpiredLogs() {
        //.minusDays(n)作用是减去n天
        log.info("自动开始清理{}天前的日志",retentionDays);
        LocalDateTime expireTime = LocalDateTime.now().minusDays(retentionDays);
        operatorLogMapper.deleteByTimeBefore(expireTime);
    }

    //手动清理超过day天的日志
    public void cleanExpiredLogsManual(Integer day) {
        log.info("手动开始清理{}天前的日志", day);
        LocalDateTime expireTime = LocalDateTime.now().minusDays(day);
        operatorLogMapper.deleteByTimeBefore(expireTime);
    }

}
