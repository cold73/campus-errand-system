package com.cold73.campuserrand.exception;

/**
 * 业务异常：用于 Service 层抛出的可预期错误
 * Controller 层捕获后转为 Result.error(msg) 返回前端
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
