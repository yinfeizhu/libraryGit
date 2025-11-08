package org.fly.common.exception;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.fly.entity.FineRecord;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * 数据库中 reason 字段的值为 3
 * 在 FineRecord.FineReason 枚举中确实定义了 LOST(3, "遗失") 这个枚举值
 * 但是 MyBatis 默认使用枚举名称进行映射，而不是枚举的 code 值
 * 需要在 MyBatis 配置中指定如何处理枚举类型
 * 先实现枚举异常处理，之后可以在 application.yml中注册异常处理，前提是用的SpringBoot 框架
 */
public class FineReasonTypeHandler extends BaseTypeHandler<FineRecord.FineReason> {
    @Override
    //
    public void setNonNullParameter(PreparedStatement ps, int i, FineRecord.FineReason parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    //根据列名获取结果
    @Override
    public FineRecord.FineReason getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Integer code = rs.getObject(columnName, Integer.class);
        return FineRecord.FineReason.fromCode(code);
    }

    //根据列索引获取结果
    @Override
    public FineRecord.FineReason getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer code = rs.getObject(columnIndex, Integer.class);
        return FineRecord.FineReason.fromCode(code);
    }

    //根据存储过程获取结果
    @Override
    public FineRecord.FineReason getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer code = cs.getObject(columnIndex, Integer.class);
        return FineRecord.FineReason.fromCode(code);
    }
}
