package com.erss.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookVO(
  Long id,
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
  String seller,
  String sellerTag,
  BigDecimal sellerRating,
  List<String> images,
  String cover,
  String summary,
  List<String> tags,
  String description,
  String status,
  LocalDateTime createdAt,
  Long sellerId
) {
}
