package com.erss.controller;

import com.erss.common.ApiResponse;
import com.erss.dto.WithdrawRequest;
import com.erss.service.WalletService;
import com.erss.vo.WalletSummaryVO;
import com.erss.vo.WalletTransactionVO;
import com.erss.vo.WalletWithdrawalVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
  private final WalletService walletService;

  public WalletController(WalletService walletService) {
    this.walletService = walletService;
  }

  @GetMapping("/summary")
  public ApiResponse<WalletSummaryVO> summary() {
    return ApiResponse.ok(walletService.summary());
  }

  @GetMapping("/withdrawals")
  public ApiResponse<List<WalletWithdrawalVO>> withdrawals() {
    return ApiResponse.ok(walletService.listWithdrawals());
  }

  @GetMapping("/transactions")
  public ApiResponse<List<WalletTransactionVO>> transactions() {
    return ApiResponse.ok(walletService.listTransactions());
  }

  @PostMapping("/withdraw")
  public ApiResponse<WalletWithdrawalVO> withdraw(@Valid @RequestBody WithdrawRequest request) {
    return ApiResponse.ok(walletService.withdraw(request));
  }
}
