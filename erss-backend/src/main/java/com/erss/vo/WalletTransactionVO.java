package com.erss.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletTransactionVO(
  Long id,
  String type,
  BigDecimal amount,
  BigDecimal balanceAfter,
  String category,
  String title,
  String referenceId,
  LocalDateTime createdAt
) {
}
