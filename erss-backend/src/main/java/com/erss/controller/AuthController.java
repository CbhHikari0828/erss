package com.erss.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.erss.common.ApiResponse;
import com.erss.dto.LoginRequest;
import com.erss.service.AuthService;
import com.erss.vo.UserInfoVO;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody(required = false) LoginRequest request) {
    LoginRequest loginRequest = request == null ? new LoginRequest("20260001", "123456") : request;
    String token = authService.login(loginRequest);
    Map<String, Object> result = new HashMap<>();
    result.put("code", 200);
    result.put("token", token);
    return result;
  }

  @GetMapping("/getInfo")
  public ApiResponse<UserInfoVO> getInfo() {
    return ApiResponse.ok(authService.getCurrentUserInfo());
  }

  @PostMapping("/logout")
  public ApiResponse<Void> logout() {
    if (StpUtil.isLogin()) {
      StpUtil.logout();
    }
    return ApiResponse.ok();
  }
}
