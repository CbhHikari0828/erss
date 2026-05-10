package com.erss.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminUserVO(
  Long id,
  String studentId,
  String username,
  String campus,
  String department,
  String role,
  Boolean merchant,
  Boolean shopRegistered,
  String shopName,
  Boolean banned,
  String banReason,
  LocalDateTime bannedAt,
  BigDecimal walletBalance,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
