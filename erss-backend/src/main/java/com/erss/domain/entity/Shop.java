package com.erss.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("shops")
public class Shop {
  private Long id;
  private Long userId;
  private String shopName;
  private String shopCampus;
  private String shopIntro;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getShopCampus() {
    return shopCampus;
  }

  public void setShopCampus(String shopCampus) {
    this.shopCampus = shopCampus;
  }

  public String getShopIntro() {
    return shopIntro;
  }

  public void setShopIntro(String shopIntro) {
    this.shopIntro = shopIntro;
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
