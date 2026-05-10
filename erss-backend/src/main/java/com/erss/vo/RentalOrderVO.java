package com.erss.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RentalOrderVO(
  Long id,
  Long bookId,
  String title,
  String period,
  BigDecimal deposit,
  String returnDeliveryMethod,
  String status,
  LocalDateTime dueAt,
  String borrowerName,
  Boolean ownedByMe,
  Boolean canReportDamage,
  Boolean canConfirmReturn,
  Boolean canInitiateReturn,
  DamageReportVO damageReport,
  Integer overdueDays
) {
}
