package com.erss.controller;

import com.erss.common.ApiResponse;
import com.erss.dto.BanUserRequest;
import com.erss.service.RentalService;
import com.erss.service.AdminUserService;
import com.erss.vo.DamageReportAdminVO;
import com.erss.vo.AdminUserVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  private final RentalService rentalService;
  private final AdminUserService adminUserService;

  public AdminController(RentalService rentalService, AdminUserService adminUserService) {
    this.rentalService = rentalService;
    this.adminUserService = adminUserService;
  }

  @GetMapping("/damage-reports")
  public ApiResponse<List<DamageReportAdminVO>> listDamageReports(@RequestParam(required = false) String status) {
    return ApiResponse.ok(rentalService.listDamageReports(status));
  }

  @GetMapping("/users")
  public ApiResponse<List<AdminUserVO>> listUsers(
    @RequestParam(defaultValue = "all") String scope,
    @RequestParam(required = false) String keyword
  ) {
    return ApiResponse.ok(adminUserService.listUsers(scope, keyword));
  }

  @PostMapping("/users/{id}/ban")
  public ApiResponse<AdminUserVO> banUser(
    @PathVariable Long id,
    @Valid @RequestBody(required = false) BanUserRequest request
  ) {
    return ApiResponse.ok(adminUserService.banUser(id, request));
  }

  @PostMapping("/users/{id}/unban")
  public ApiResponse<AdminUserVO> unbanUser(@PathVariable Long id) {
    return ApiResponse.ok(adminUserService.unbanUser(id));
  }
}
