package com.erss.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderVO(
  Long id,
  String orderNo,
  String orderType,
  Long bookId,
  String title,
  String sellerName,
  String buyerName,
  BigDecimal unitPrice,
  Integer quantity,
  String rentalPeriod,
  BigDecimal depositAmount,
  BigDecimal deliveryFee,
  BigDecimal totalAmount,
  String deliveryMethod,
  String paymentMethod,
  String status,
  String escrowStatus,
  LocalDateTime autoConfirmAt,
  LocalDateTime createdAt
) {
}
