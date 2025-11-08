package org.fly.service;

import org.fly.entity.OperateLog;
import org.fly.entity.PageResult;

import java.util.List;

public interface OperatorLogService {
    void add(OperateLog olog);

    PageResult<OperateLog> selectPage(Integer page, Integer pageSize);

    void cleanExpiredLogsManual(Integer day);
}
