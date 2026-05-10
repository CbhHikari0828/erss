package com.erss.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("trade_orders")
public class TradeOrder {
  private Long id;
  private String orderNo;
  private String orderType;
  private Long bookId;
  private Long sellerId;
  private Long buyerId;
  private BigDecimal unitPrice;
  private Integer quantity;
  private String rentalPeriod;
  private BigDecimal depositAmount;
  private BigDecimal deliveryFee;
  private BigDecimal totalAmount;
  private String deliveryMethod;
  private String paymentMethod;
  private String status;
  private String escrowStatus;
  private LocalDateTime sellerShippedAt;
  private LocalDateTime autoConfirmAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public Long getBookId() {
    return bookId;
  }

  public void setBookId(Long bookId) {
    this.bookId = bookId;
  }

  public Long getSellerId() {
    return sellerId;
  }

  public void setSellerId(Long sellerId) {
    this.sellerId = sellerId;
  }

  public Long getBuyerId() {
    return buyerId;
  }

  public void setBuyerId(Long buyerId) {
    this.buyerId = buyerId;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public String getRentalPeriod() {
    return rentalPeriod;
  }

  public void setRentalPeriod(String rentalPeriod) {
    this.rentalPeriod = rentalPeriod;
  }

  public BigDecimal getDepositAmount() {
    return depositAmount;
  }

  public void setDepositAmount(BigDecimal depositAmount) {
    this.depositAmount = depositAmount;
  }

  public BigDecimal getDeliveryFee() {
    return deliveryFee;
  }

  public void setDeliveryFee(BigDecimal deliveryFee) {
    this.deliveryFee = deliveryFee;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getDeliveryMethod() {
    return deliveryMethod;
  }

  public void setDeliveryMethod(String deliveryMethod) {
    this.deliveryMethod = deliveryMethod;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getEscrowStatus() {
    return escrowStatus;
  }

  public void setEscrowStatus(String escrowStatus) {
    this.escrowStatus = escrowStatus;
  }

  public LocalDateTime getSellerShippedAt() {
    return sellerShippedAt;
  }

  public void setSellerShippedAt(LocalDateTime sellerShippedAt) {
    this.sellerShippedAt = sellerShippedAt;
  }

  public LocalDateTime getAutoConfirmAt() {
    return autoConfirmAt;
  }

  public void setAutoConfirmAt(LocalDateTime autoConfirmAt) {
    this.autoConfirmAt = autoConfirmAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
