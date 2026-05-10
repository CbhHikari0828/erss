package com.erss.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletWithdrawalVO(
  Long id,
  BigDecimal amount,
  String channel,
  String channelText,
  String destinationAccount,
  String note,
  String status,
  String statusText,
  LocalDateTime requestedAt,
  LocalDateTime completedAt
) {
}
