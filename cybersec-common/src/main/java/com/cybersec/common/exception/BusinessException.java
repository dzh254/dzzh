package com.cybersec.common.exception;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /** 资源不存在 */
    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }

    /** 参数错误 */
    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }

    /** 未授权 */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, message);
    }

    /** 禁止操作 */
    public static BusinessException forbidden(String message) {
        return new BusinessException(403, message);
    }
}
