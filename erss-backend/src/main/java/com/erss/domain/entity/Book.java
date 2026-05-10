package com.erss.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("books")
public class Book {
  private Long id;
  private Long sellerId;
  private String title;
  private String category;
  private String subcategory;
  private BigDecimal price;
  private BigDecimal originalPrice;
  private Boolean rentalEnabled;
  private BigDecimal rentPrice;
  private BigDecimal rentalDeposit;
  private String conditionText;
  private String campus;
  private String coverUrl;
  private String summary;
  private String description;
  private String tags;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getSellerId() {
    return sellerId;
  }

  public void setSellerId(Long sellerId) {
    this.sellerId = sellerId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getSubcategory() {
    return subcategory;
  }

  public void setSubcategory(String subcategory) {
    this.subcategory = subcategory;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public BigDecimal getOriginalPrice() {
    return originalPrice;
  }

  public void setOriginalPrice(BigDecimal originalPrice) {
    this.originalPrice = originalPrice;
  }

  public Boolean getRentalEnabled() {
    return rentalEnabled;
  }

  public void setRentalEnabled(Boolean rentalEnabled) {
    this.rentalEnabled = rentalEnabled;
  }

  public BigDecimal getRentPrice() {
    return rentPrice;
  }

  public void setRentPrice(BigDecimal rentPrice) {
    this.rentPrice = rentPrice;
  }

  public BigDecimal getRentalDeposit() {
    return rentalDeposit;
  }

  public void setRentalDeposit(BigDecimal rentalDeposit) {
    this.rentalDeposit = rentalDeposit;
  }

  public String getConditionText() {
    return conditionText;
  }

  public void setConditionText(String conditionText) {
    this.conditionText = conditionText;
  }

  public String getCampus() {
    return campus;
  }

  public void setCampus(String campus) {
    this.campus = campus;
  }

  public String getCoverUrl() {
    return coverUrl;
  }

  public void setCoverUrl(String coverUrl) {
    this.coverUrl = coverUrl;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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
