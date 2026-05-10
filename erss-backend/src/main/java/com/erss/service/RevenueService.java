package com.erss.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.erss.domain.entity.SystemTransaction;
import com.erss.mapper.SystemTransactionMapper;
import com.erss.vo.RevenueLedgerVO;
import com.erss.vo.RevenueSummaryVO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RevenueService {

  private final SystemTransactionMapper systemTransactionMapper;

  public RevenueService(SystemTransactionMapper systemTransactionMapper) {
    this.systemTransactionMapper = systemTransactionMapper;
  }

  public RevenueLedgerVO listLedger(String category, LocalDate startDate, LocalDate endDate) {
    StpUtil.checkRole("admin");

    var query = Wrappers.<SystemTransaction>lambdaQuery()
      .eq(category != null && !"all".equalsIgnoreCase(category),
        SystemTransaction::getCategory, category)
      .ge(startDate != null, SystemTransaction::getCreatedAt,
        startDate != null ? startDate.atStartOfDay() : null)
      .le(endDate != null, SystemTransaction::getCreatedAt,
        endDate != null ? endDate.atTime(LocalTime.MAX) : null)
      .orderByDesc(SystemTransaction::getCreatedAt);

    List<SystemTransaction> records = systemTransactionMapper.selectList(query);

    Map<String, BigDecimal> categoryTotals = records.stream()
      .collect(Collectors.groupingBy(
        SystemTransaction::getCategory,
        Collectors.reducing(BigDecimal.ZERO, SystemTransaction::getAmount, BigDecimal::add)
      ));

    BigDecimal totalAmount = records.stream()
      .map(SystemTransaction::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new RevenueLedgerVO(records, categoryTotals, totalAmount);
  }

  public RevenueSummaryVO summary() {
    StpUtil.checkRole("admin");

    List<SystemTransaction> all = systemTransactionMapper.selectList(
      Wrappers.<SystemTransaction>lambdaQuery()
        .orderByAsc(SystemTransaction::getCreatedAt));

    Map<String, BigDecimal> categoryTotals = all.stream()
      .collect(Collectors.groupingBy(
        SystemTransaction::getCategory,
        Collectors.reducing(BigDecimal.ZERO, SystemTransaction::getAmount, BigDecimal::add)
      ));

    DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("yyyy-MM");
    Map<String, BigDecimal> monthlyTotals = all.stream()
      .collect(Collectors.groupingBy(
        tx -> tx.getCreatedAt().format(monthFmt),
        Collectors.reducing(BigDecimal.ZERO, SystemTransaction::getAmount, BigDecimal::add)
      ));

    return new RevenueSummaryVO(categoryTotals, monthlyTotals);
  }
}
