package com.erss.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
  @NotBlank(message = "请输入昵称")
  String username,

  @NotBlank(message = "请输入学院")
  String department,

  @NotBlank(message = "请选择校区")
  String campus,

  String avatar,
  String bio,

  String currentPassword,

  @Size(min = 6, max = 20, message = "新密码长度需为 6-20 位")
  String newPassword
) {
}
