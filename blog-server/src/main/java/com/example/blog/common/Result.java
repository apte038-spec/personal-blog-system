package com.example.blog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 统一 HTTP 接口响应体。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    /** 业务状态码：0 表示成功，其他值表示失败。 */
    private Integer code;
    /** 面向调用方的结果说明。 */
    private String message;
    /** 成功时的业务载荷；失败时为 null。 */
    private T data;

    public static <T> Result<T> success(T data) { return new Result<>(0, "success", data); }
    public static <T> Result<T> success(String message, T data) { return new Result<>(0, message, data); }
    public static Result<Void> success() { return success(null); }
    public static <T> Result<T> failure(Integer code, String message) { return new Result<>(code, message, null); }
    public static <T> Result<T> failure(String message) { return failure(-1, message); }
}
