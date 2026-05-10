package com.erss.vo;

import java.time.LocalDateTime;

public record NotificationVO(
  Long id,
  String category,
  String title,
  String summary,
  LocalDateTime time,
  Boolean unread,
  String status,
  String actionText,
  String route
) {
}
