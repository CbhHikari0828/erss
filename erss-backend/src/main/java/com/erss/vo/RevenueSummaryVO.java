package com.erss.vo;

import java.math.BigDecimal;
import java.util.Map;

public record RevenueSummaryVO(
  Map<String, BigDecimal> categoryTotals,
  Map<String, BigDecimal> monthlyTotals
) {}
