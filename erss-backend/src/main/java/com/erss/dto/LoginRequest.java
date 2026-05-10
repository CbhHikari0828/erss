package com.erss.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
  @NotBlank(message = "请输入学生号")
  String studentId,

  @NotBlank(message = "请输入密码")
  @Size(min = 6, max = 20, message = "密码长度需为 6-20 位")
  String password
) {
}
