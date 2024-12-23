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
}
