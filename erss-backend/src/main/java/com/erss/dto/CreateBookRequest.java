package com.erss.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record CreateBookRequest(
  @NotBlank(message = "请填写书名")
  String title,

  @NotBlank(message = "请选择分类")
  String category,

  String subcategory,

  @NotNull(message = "请填写售价")
  @DecimalMin(value = "0.01", message = "售价必须大于 0")
  BigDecimal price,

  BigDecimal originalPrice,

  Boolean rentalEnabled,

  BigDecimal rentPrice,

  BigDecimal rentalDeposit,

  @NotBlank(message = "请选择成色")
  String condition,

  @NotBlank(message = "请选择校区")
  String campus,

  String cover,
  String summary,
  String description,
  List<String> tags,
  List<String> images
) {
}
