package com.erss.dto;

import jakarta.validation.constraints.Size;

public record BanUserRequest(
  @Size(max = 255, message = "封号原因不能超过 255 字")
  String banReason
) {
}
