package com.erss.dto;

import jakarta.validation.constraints.Size;

public record ReleasePayoutRequest(
  @Size(max = 255, message = "备注不能超过 255 字")
  String releaseNote
) {
}
