package com.mvc.exception;

/**
 * @Program: simplify_mvc
 * @Description: 自定义异常
 * @Author: ZJamss
 * @Create: 2022-07-31 17:22
 **/
public class ContextException extends RuntimeException{
    public ContextException(String message) {
        super(message);
    }

    public ContextException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
