package com.erss.dto;

import java.math.BigDecimal;
import java.util.List;

public record UpdateBookRequest(
  String title,
  String category,
  String subcategory,
  BigDecimal price,
  BigDecimal originalPrice,
  Boolean rentalEnabled,
  BigDecimal rentPrice,
  BigDecimal rentalDeposit,
  String condition,
  String campus,
  String cover,
  String summary,
  String description,
  List<String> tags,
  List<String> images,
  String status
) {
}
