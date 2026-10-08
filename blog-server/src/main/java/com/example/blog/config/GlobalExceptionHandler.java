package com.example.blog.config;

import com.example.blog.common.BusinessException;
import com.example.blog.common.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 将异常转换为统一 Result 响应，避免暴露框架异常细节。 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusiness(BusinessException exception) {
        return ResponseEntity.badRequest().body(Result.failure(4000, exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException exception) {
        FieldError error = exception.getBindingResult().getFieldError();
        String message = error == null ? "请求参数不合法" : error.getField() + "：" + error.getDefaultMessage();
        return ResponseEntity.badRequest().body(Result.failure(4001, message));
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Result<Void>> handleParameter(Exception exception) {
        return ResponseEntity.badRequest().body(Result.failure(4001, "请求参数不合法"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<Void>> handleIntegrity(DataIntegrityViolationException exception) {
        return ResponseEntity.badRequest().body(Result.failure(4002, "数据重复或仍被其他记录引用"));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Void>> handleUploadTooLarge(MaxUploadSizeExceededException exception) {
        return ResponseEntity.badRequest().body(Result.failure(4001, "头像图片不能超过 5MB"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleResourceNotFound(NoResourceFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.failure(4040, "资源不存在"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUnexpected(Exception exception) {
        log.error("未处理的服务器异常", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.failure(5000, "服务器处理请求失败"));
    }
}
