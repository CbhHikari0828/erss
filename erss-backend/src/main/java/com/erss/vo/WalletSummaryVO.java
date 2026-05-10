package com.erss.vo;

import java.math.BigDecimal;

public record WalletSummaryVO(
  BigDecimal walletBalance,
  Long withdrawCount,
  BigDecimal withdrawAmount
) {
}
