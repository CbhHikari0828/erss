package com.erss.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
  @NotNull(message = "请选择书籍")
  Long bookId,

  @NotBlank(message = "请选择订单类型")
  String orderType,

  String rentalPeriod,

  @NotBlank(message = "请选择收货方式")
  String deliveryMethod,

  @NotBlank(message = "请选择付款方式")
  String paymentMethod
) {
}
