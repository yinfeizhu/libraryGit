package org.fly.mapper;

import org.apache.ibatis.annotations.*;
import org.fly.entity.OperateLog;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OperatorLogMapper {
    //添加操作日志
    @Insert("insert into operate_log(id, admin_id, operation_target, operation_type, ip_address, return_result, method_params, operation_time, cost_time)  " +
            "values(#{id}, #{adminId}, #{operationTarget}, #{operationType}, #{ipAddress}, #{returnResult}, #{methodParams}, #{operationTime}, #{costTime})")
    void insert(OperateLog olog);

    //查询操作日志
    @Select("select ol.*,a.name as operator_name  from operate_log ol left join user a on ol.admin_id = a.id order by operation_time desc")
    List<OperateLog> selectPage();

    // 删除指定时间之前的日志
    @Delete("DELETE FROM operate_log WHERE operation_time < #{expireTime}")
    void deleteByTimeBefore(LocalDateTime expireTime);

}
