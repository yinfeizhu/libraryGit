package org.fly.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.fly.entity.Result;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 先处理具体的异常类型
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return Result.error(processErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(SQLException.class)
    public Result handleSQLException(SQLException e) {
        log.error("数据库操作异常: ", e);
        return Result.error(processErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Result handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error("SQL语法错误: ", e);
        return Result.error("更新失败");
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        log.error("数据库约束违反异常: ", e);
        String message = e.getMessage();
        if (message.contains("Column 'name' cannot be null")) {
            return Result.error("姓名不能为空");
        }
        if (message.contains("Duplicate entry")) {
            int startIndex = message.indexOf("'") + 1;
            int endIndex = message.indexOf("'", startIndex);
            String duplicateValue = message.substring(startIndex, endIndex);
            return Result.error("数据 '" + duplicateValue + "' 已存在，无法重复添加");
        }
        return Result.error("数据库操作违反约束");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("程序出错: ", e);
        String message = e.getMessage();
        int index = message.indexOf("Duplicate entry");
        String errMsg = message.substring(index);
        String[] arr = errMsg.split(" ");
        return Result.error(arr[2] + "数据已存在");
    }

    @ExceptionHandler(OutOfMemoryError.class)
    public Result handleOutOfMemoryError(OutOfMemoryError e) {
        log.error("内存溢出错误: ", e);
        return Result.error("系统繁忙，请稍后重试");
    }

    // 最后处理通用异常
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("程序出错: ", e);
        return Result.error(processErrorMessage(e.getMessage()));
    }

    /**
     * 处理异常消息，如果包含非中文字符则返回友好的中文提示
     */
    private String processErrorMessage(String message) {
        if (message == null || message.isEmpty()) {
            return "操作失败";
        }

        // 检查是否包含中文字符
        if (!containsChineseCharacters(message)) {
            // 根据异常类型返回不同的友好提示
            if (message.contains("SQLSyntaxErrorException") ||
                    message.contains("SQL syntax error")) {
                return "更新失败，请联系管理员";
            } else if (message.contains("Duplicate entry")) {
                return "已存在";
            } else if (message.contains("constraint violation")) {
                return "数据验证失败";
            } else {
                return "操作失败";
            }
        }

        return message;
    }

    /**
     * 检查字符串是否包含中文字符
     */
    private boolean containsChineseCharacters(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (c >= '\u4e00' && c <= '\u9fff') {
                return true;
            }
        }
        return false;
    }
}
