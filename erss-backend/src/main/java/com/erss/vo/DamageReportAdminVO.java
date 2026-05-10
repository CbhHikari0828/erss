package com.erss.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DamageReportAdminVO(
  Long id,
  Long rentalOrderId,
  Long tradeOrderId,
  Long bookId,
  String bookTitle,
  BigDecimal bookPrice,
  BigDecimal bookOriginalPrice,
  BigDecimal rentalUnitPrice,
  BigDecimal rentalDepositAmount,
  BigDecimal rentalDeliveryFee,
  BigDecimal rentalTotalAmount,
  String orderNo,
  String reporterName,
  String ownerName,
  String borrowerName,
  String description,
  List<String> images,
  String status,
  String reviewNote,
  BigDecimal compensationAmount,
  String rentalStatus,
  String periodText,
  LocalDateTime dueAt,
  LocalDateTime createdAt,
  LocalDateTime reviewedAt
) {
}
