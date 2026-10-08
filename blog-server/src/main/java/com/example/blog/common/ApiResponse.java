package com.example.blog.common;

/** 兼容既有控制器的旧名称；后续新接口应统一使用 {@link Result}。 */
@Deprecated
public class ApiResponse<T> extends Result<T> {
    public ApiResponse(int code, String message, T data) { super(code, message, data); }
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(0, "success", data); }
    public static <T> ApiResponse<T> fail(String message) { return new ApiResponse<>(-1, message, null); }
}
