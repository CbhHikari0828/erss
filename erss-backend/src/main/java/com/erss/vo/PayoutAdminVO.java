package com.erss.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PayoutAdminVO(
  Long id,
  Long orderId,
  String orderNo,
  Long sellerId,
  String sellerName,
  Long buyerId,
  String buyerName,
  String bookTitle,
  String orderType,
  String paymentMethod,
  BigDecimal amount,
  String status,
  LocalDateTime requestedAt,
  LocalDateTime releasedAt,
  String releaseNote
) {
}
