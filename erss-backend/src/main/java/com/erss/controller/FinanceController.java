package com.erss.controller;

import com.erss.common.ApiResponse;
import com.erss.dto.ReleasePayoutRequest;
import com.erss.service.FinanceService;
import com.erss.vo.FinanceSummaryVO;
import com.erss.vo.PayoutAdminVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/finance")
public class FinanceController {
  private final FinanceService financeService;

  public FinanceController(FinanceService financeService) {
    this.financeService = financeService;
  }

  @GetMapping("/summary")
  public ApiResponse<FinanceSummaryVO> summary() {
    return ApiResponse.ok(financeService.summary());
  }

  @GetMapping("/payouts")
  public ApiResponse<List<PayoutAdminVO>> payouts(@RequestParam(required = false) String status) {
    return ApiResponse.ok(financeService.listPayouts(status));
  }

  @PostMapping("/payouts/{id}/release")
  public ApiResponse<PayoutAdminVO> release(
    @PathVariable Long id,
    @Valid @RequestBody(required = false) ReleasePayoutRequest request
  ) {
    return ApiResponse.ok(financeService.releasePayout(id, request));
  }
}
