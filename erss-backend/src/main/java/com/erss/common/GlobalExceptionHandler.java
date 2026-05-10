package com.erss.common;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(BizException.class)
  public ApiResponse<Void> handleBizException(BizException ex) {
    return ApiResponse.fail(ex.getCode(), ex.getMessage());
  }

  @ExceptionHandler(NotLoginException.class)
  public ApiResponse<Void> handleNotLogin(NotLoginException ex) {
    return ApiResponse.fail(401, "请先登录");
  }

  @ExceptionHandler({NotRoleException.class, NotPermissionException.class})
  public ApiResponse<Void> handleForbidden(RuntimeException ex) {
    return ApiResponse.fail(403, "没有操作权限");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
      .findFirst()
      .map(error -> error.getDefaultMessage() == null ? "参数不正确" : error.getDefaultMessage())
      .orElse("参数不正确");
    return ApiResponse.fail(400, message);
  }

  @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class})
  public ApiResponse<Void> handleBadRequest(RuntimeException ex) {
    return ApiResponse.fail(400, "请求参数不正确");
  }

  @ExceptionHandler(Exception.class)
  public ApiResponse<Void> handleException(Exception ex) {
    return ApiResponse.fail(500, "服务器内部错误");
  }
}
