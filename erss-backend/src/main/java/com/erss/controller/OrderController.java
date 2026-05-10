package com.erss.controller;

import com.erss.common.ApiResponse;
import com.erss.dto.CreateOrderRequest;
import com.erss.service.OrderService;
import com.erss.vo.OrderVO;
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
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public ApiResponse<Map<String, Long>> create(@Valid @RequestBody CreateOrderRequest request) {
    return ApiResponse.ok(Map.of("orderId", orderService.createOrder(request)));
  }

  @GetMapping("/buyer")
  public ApiResponse<List<OrderVO>> buyerOrders() {
    return ApiResponse.ok(orderService.listBuyerOrders());
  }

  @GetMapping("/seller")
  public ApiResponse<List<OrderVO>> sellerOrders() {
    return ApiResponse.ok(orderService.listSellerOrders());
  }

  @PostMapping("/{id}/ship")
  public ApiResponse<Void> ship(@PathVariable Long id) {
    orderService.shipOrder(id);
    return ApiResponse.ok();
  }

  @PostMapping("/{id}/confirm")
  public ApiResponse<Void> confirm(@PathVariable Long id) {
    orderService.confirmReceipt(id);
    return ApiResponse.ok();
  }
}
