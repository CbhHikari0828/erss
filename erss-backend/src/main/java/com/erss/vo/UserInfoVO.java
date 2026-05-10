package com.erss.vo;

import java.math.BigDecimal;

public record UserInfoVO(
  Long id,
  String username,
  String avatar,
  String campus,
  String department,
  String bio,
  String shopName,
  String shopCampus,
  String shopIntro,
  Boolean shopRegistered,
  String role,
  BigDecimal walletBalance
) {
}
