package com.erss.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record WithdrawRequest(
  @NotNull(message = "请输入提现金额")
  @DecimalMin(value = "0.01", message = "提现金额必须大于 0")
  BigDecimal amount,

  @NotBlank(message = "请选择提现方式")
  String channel,

  @Size(max = 128, message = "收款账号不能超过 128 字")
  String destinationAccount,

  @Size(max = 255, message = "备注不能超过 255 字")
  String note
) {
}
