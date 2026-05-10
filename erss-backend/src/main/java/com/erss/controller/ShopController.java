package com.erss.controller;

import com.erss.common.ApiResponse;
import com.erss.dto.RegisterShopRequest;
import com.erss.service.ShopService;
import com.erss.vo.ShopVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop")
public class ShopController {
  private final ShopService shopService;

  public ShopController(ShopService shopService) {
    this.shopService = shopService;
  }

  @PostMapping
  public ApiResponse<ShopVO> register(@Valid @RequestBody RegisterShopRequest request) {
    return ApiResponse.ok(shopService.register(request));
  }
}
