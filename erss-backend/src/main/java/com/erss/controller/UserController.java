package com.erss.controller;

import com.erss.common.ApiResponse;
import com.erss.dto.UpdateUserProfileRequest;
import com.erss.service.AuthService;
import com.erss.vo.UserInfoVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private final AuthService authService;

  public UserController(AuthService authService) {
    this.authService = authService;
  }

  @PutMapping("/profile")
  public ApiResponse<UserInfoVO> updateProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
    return ApiResponse.ok(authService.updateCurrentUserProfile(request));
  }
}
