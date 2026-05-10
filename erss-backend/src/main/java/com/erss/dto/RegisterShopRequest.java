package com.erss.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterShopRequest(
  @NotBlank(message = "请填写商铺名称")
  String shopName,

  @NotBlank(message = "请选择校区")
  String shopCampus,

  @NotBlank(message = "请填写商铺简介")
  String shopIntro
) {
}
