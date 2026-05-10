package com.erss.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("payout_requests")
public class PayoutRequest {
  private Long id;
  private Long orderId;
  private Long sellerId;
  private BigDecimal amount;
  private String status;
  private LocalDateTime requestedAt;
  private LocalDateTime releasedAt;
  private Long releasedBy;
  private String releaseNote;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long getSellerId() {
    return sellerId;
  }

  public void setSellerId(Long sellerId) {
    this.sellerId = sellerId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDateTime getRequestedAt() {
    return requestedAt;
  }

  public void setRequestedAt(LocalDateTime requestedAt) {
    this.requestedAt = requestedAt;
  }

  public LocalDateTime getReleasedAt() {
    return releasedAt;
  }

  public void setReleasedAt(LocalDateTime releasedAt) {
    this.releasedAt = releasedAt;
  }

  public Long getReleasedBy() {
    return releasedBy;
  }

  public void setReleasedBy(Long releasedBy) {
    this.releasedBy = releasedBy;
  }

  public String getReleaseNote() {
    return releaseNote;
  }

  public void setReleaseNote(String releaseNote) {
    this.releaseNote = releaseNote;
  }
}
