package com.erss.controller;

import com.erss.common.ApiResponse;
import com.erss.dto.DamageReportRequest;
import com.erss.dto.DamageReviewRequest;
import com.erss.service.RentalService;
import com.erss.vo.RentalOrderVO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
  private final RentalService rentalService;

  public RentalController(RentalService rentalService) {
    this.rentalService = rentalService;
  }

  @GetMapping
  public ApiResponse<List<RentalOrderVO>> listMine() {
    return ApiResponse.ok(rentalService.listMine());
  }

  @PostMapping("/{id}/damage-reports")
  public ApiResponse<Void> submitDamageReport(@PathVariable Long id, @Valid @RequestBody DamageReportRequest request) {
    rentalService.submitDamageReport(id, request);
    return ApiResponse.ok();
  }

  @PostMapping("/damage-reports/{id}/review")
  public ApiResponse<Void> reviewDamageReport(@PathVariable Long id, @RequestBody DamageReviewRequest request) {
    rentalService.reviewDamageReport(id, request);
    return ApiResponse.ok();
  }

  @PostMapping("/{id}/initiate-return")
  public ApiResponse<RentalOrderVO> initiateReturn(@PathVariable Long id, @RequestBody Map<String, String> body) {
    return ApiResponse.ok(rentalService.initiateReturn(id, body.getOrDefault("deliveryMethod", "pickup")));
  }

  @PostMapping("/{id}/confirm-return")
  public ApiResponse<Void> confirmReturn(@PathVariable Long id) {
    rentalService.confirmReturn(id);
    return ApiResponse.ok();
  }

  @PostMapping("/{id}/confirm-receipt")
  public ApiResponse<RentalOrderVO> confirmReceipt(@PathVariable Long id) {
    return ApiResponse.ok(rentalService.confirmReceipt(id));
  }
}
