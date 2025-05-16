package com.xcg.aitripassistant.handler;


import com.xcg.aitripassistant.utils.Result;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理数据库唯一键冲突异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> handleDuplicateKeyException(DuplicateKeyException ex) {
        // 可以记录日志
        // log.warn("Duplicate key exception occurred: ", ex);

        return Result.fail("用户名已存在！");
    }

    /**
     * 可以继续添加其他全局异常处理方法
     */
}

