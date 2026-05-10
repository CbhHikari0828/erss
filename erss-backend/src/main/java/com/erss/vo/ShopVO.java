package com.erss.vo;

public record ShopVO(
  Long id,
  Long userId,
  String shopName,
  String shopCampus,
  String shopIntro
) {
}
