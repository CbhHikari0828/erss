package com.erss.dto;

import java.math.BigDecimal;

public record DamageReviewRequest(
  String reviewNote,
  BigDecimal compensationAmount
) {
}
