package com.erss.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.erss.domain.entity.Notification;
import com.erss.mapper.NotificationMapper;
import com.erss.vo.NotificationVO;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
  private final NotificationMapper notificationMapper;

  public MessageService(NotificationMapper notificationMapper) {
    this.notificationMapper = notificationMapper;
  }

  public List<NotificationVO> listMine(String category) {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    return notificationMapper.selectList(Wrappers.<Notification>lambdaQuery()
        .eq(Notification::getUserId, userId)
        .eq(category != null && !category.isBlank() && !"all".equals(category), Notification::getCategory, category)
        .orderByDesc(Notification::getCreatedAt))
      .stream()
      .map(this::toNotificationVO)
      .toList();
  }

  public void markRead(Long id) {
    Notification notification = notificationMapper.selectById(id);
    if (notification == null) {
      return;
    }
    notification.setUnread(false);
    notificationMapper.updateById(notification);
  }

  public void markAllRead() {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    notificationMapper.update(null, Wrappers.<Notification>lambdaUpdate()
      .eq(Notification::getUserId, userId)
      .set(Notification::getUnread, false));
  }

  public void deleteMine(Long id) {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    notificationMapper.delete(Wrappers.<Notification>lambdaQuery()
      .eq(Notification::getId, id)
      .eq(Notification::getUserId, userId));
  }

  private NotificationVO toNotificationVO(Notification notification) {
    return new NotificationVO(
      notification.getId(),
      notification.getCategory(),
      notification.getTitle(),
      notification.getSummary(),
      notification.getCreatedAt(),
      Boolean.TRUE.equals(notification.getUnread()),
      notification.getStatusText(),
      notification.getActionText(),
      notification.getRoute()
    );
  }
}
