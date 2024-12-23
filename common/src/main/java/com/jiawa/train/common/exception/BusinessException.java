package com.jiawa.train.common.exception;

/**
 * ClassName: BusinessException
 * Package: com.jiawa.train.common.exception
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/23 19:50
 * @Version 1.0
 */
public class BusinessException extends RuntimeException {
    private BusinessExceptionEnum e;
    public BusinessException(BusinessExceptionEnum e) {
        this.e = e;
    }

    public BusinessExceptionEnum getE() {
        return e;
    }

    /**
     * 这个方法是重写父类方法，防止返回堆栈信息
     * 因为返回堆栈信息，前端页面会显示，但是前端页面不显示，不会影响业务逻辑
     * 更加的简便
     * @return
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
