package com.erss.vo;

import com.erss.domain.entity.SystemTransaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record RevenueLedgerVO(
  List<SystemTransaction> records,
  Map<String, BigDecimal> categoryTotals,
  BigDecimal totalAmount
) {}
