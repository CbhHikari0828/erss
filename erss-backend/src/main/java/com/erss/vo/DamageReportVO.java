package com.erss.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DamageReportVO(
  Long id,
  String status,
  String description,
  List<String> images,
  LocalDateTime createdAt,
  String reviewNote,
  BigDecimal compensationAmount
) {
}
