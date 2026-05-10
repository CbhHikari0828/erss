package com.erss.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("system_transactions")
public class SystemTransaction {

  @TableId(type = IdType.AUTO)
  private Long id;
  private String category;
  private BigDecimal amount;
  private String description;
  private String referenceId;
  private LocalDateTime createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }

  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public String getReferenceId() { return referenceId; }
  public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
