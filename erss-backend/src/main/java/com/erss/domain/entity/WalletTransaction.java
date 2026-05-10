package com.erss.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("wallet_transactions")
public class WalletTransaction {
  private Long id;
  private Long userId;
  private String type;
  private BigDecimal amount;
  private BigDecimal balanceAfter;
  private String category;
  private String title;
  private String referenceId;
  private LocalDateTime createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }

  public BigDecimal getBalanceAfter() { return balanceAfter; }
  public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getReferenceId() { return referenceId; }
  public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
