package com.cybersec.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

    private int code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> ApiResult<T> success() {
        return success(null);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "success", data, System.currentTimeMillis());
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data, System.currentTimeMillis());
    }

    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null, System.currentTimeMillis());
    }

    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(500, message, null, System.currentTimeMillis());
    }

    public static <T> ApiResult<T> badRequest(String message) {
        return new ApiResult<>(400, message, null, System.currentTimeMillis());
    }

    public static <T> ApiResult<T> unauthorized(String message) {
        return new ApiResult<>(401, message, null, System.currentTimeMillis());
    }

    public static <T> ApiResult<T> forbidden(String message) {
        return new ApiResult<>(403, message, null, System.currentTimeMillis());
    }

    public static <T> ApiResult<T> notFound(String message) {
        return new ApiResult<>(404, message, null, System.currentTimeMillis());
    }

    public boolean isSuccess() {
        return code == 200;
    }
}
