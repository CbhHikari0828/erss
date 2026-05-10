package com.erss.vo;

import java.math.BigDecimal;

public record FinanceSummaryVO(
  BigDecimal systemEscrowBalance,
  Long holdingPayoutCount,
  BigDecimal holdingPayoutAmount,
  Long pendingPayoutCount,
  BigDecimal pendingPayoutAmount,
  Long releasedPayoutCount,
  BigDecimal releasedPayoutAmount
) {
}
