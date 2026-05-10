package com.erss.controller;

import com.erss.common.ApiResponse;
import com.erss.service.RevenueService;
import com.erss.vo.RevenueLedgerVO;
import com.erss.vo.RevenueSummaryVO;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/revenue")
public class RevenueController {

  private final RevenueService revenueService;

  public RevenueController(RevenueService revenueService) {
    this.revenueService = revenueService;
  }

  @GetMapping("/ledger")
  public ApiResponse<RevenueLedgerVO> listLedger(
    @RequestParam(required = false) String category,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
  ) {
    return ApiResponse.ok(revenueService.listLedger(category, startDate, endDate));
  }

  @GetMapping("/summary")
  public ApiResponse<RevenueSummaryVO> summary() {
    return ApiResponse.ok(revenueService.summary());
  }
}
