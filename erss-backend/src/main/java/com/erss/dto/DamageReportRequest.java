package com.erss.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record DamageReportRequest(
  @NotBlank(message = "请填写损坏描述")
  String description,

  List<String> images
) {
}
