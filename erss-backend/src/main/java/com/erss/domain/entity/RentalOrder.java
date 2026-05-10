package com.erss.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("rental_orders")
public class RentalOrder {
  private Long id;
  private Long tradeOrderId;
  private Long bookId;
  private Long ownerId;
  private Long borrowerId;
  private String periodText;
  private BigDecimal depositAmount;
  private String returnDeliveryMethod;
  private String status;
  private LocalDateTime dueAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTradeOrderId() {
    return tradeOrderId;
  }

  public void setTradeOrderId(Long tradeOrderId) {
    this.tradeOrderId = tradeOrderId;
  }

  public Long getBookId() {
    return bookId;
  }

  public void setBookId(Long bookId) {
    this.bookId = bookId;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public Long getBorrowerId() {
    return borrowerId;
  }

  public void setBorrowerId(Long borrowerId) {
    this.borrowerId = borrowerId;
  }

  public String getPeriodText() {
    return periodText;
  }

  public void setPeriodText(String periodText) {
    this.periodText = periodText;
  }

  public BigDecimal getDepositAmount() {
    return depositAmount;
  }

  public void setDepositAmount(BigDecimal depositAmount) {
    this.depositAmount = depositAmount;
  }

  public String getReturnDeliveryMethod() {
    return returnDeliveryMethod;
  }

  public void setReturnDeliveryMethod(String returnDeliveryMethod) {
    this.returnDeliveryMethod = returnDeliveryMethod;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDateTime getDueAt() {
    return dueAt;
  }

  public void setDueAt(LocalDateTime dueAt) {
    this.dueAt = dueAt;
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
