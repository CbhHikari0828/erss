package com.erss.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("damage_reports")
public class DamageReport {
  private Long id;
  private Long rentalOrderId;
  private Long reporterId;
  private String description;
  private String images;
  private String status;
  private String reviewNote;
  private BigDecimal compensationAmount;
  private LocalDateTime createdAt;
  private LocalDateTime reviewedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getRentalOrderId() {
    return rentalOrderId;
  }

  public void setRentalOrderId(Long rentalOrderId) {
    this.rentalOrderId = rentalOrderId;
  }

  public Long getReporterId() {
    return reporterId;
  }

  public void setReporterId(Long reporterId) {
    this.reporterId = reporterId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImages() {
    return images;
  }

  public void setImages(String images) {
    this.images = images;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getReviewNote() {
    return reviewNote;
  }

  public void setReviewNote(String reviewNote) {
    this.reviewNote = reviewNote;
  }

  public BigDecimal getCompensationAmount() {
    return compensationAmount;
  }

  public void setCompensationAmount(BigDecimal compensationAmount) {
    this.compensationAmount = compensationAmount;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getReviewedAt() {
    return reviewedAt;
  }

  public void setReviewedAt(LocalDateTime reviewedAt) {
    this.reviewedAt = reviewedAt;
  }
}
