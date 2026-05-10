package com.erss.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.erss.common.BizException;
import com.erss.domain.entity.Shop;
import com.erss.domain.entity.Notification;
import com.erss.domain.entity.UserAccount;
import com.erss.dto.BanUserRequest;
import com.erss.mapper.NotificationMapper;
import com.erss.mapper.ShopMapper;
import com.erss.mapper.UserAccountMapper;
import com.erss.vo.AdminUserVO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class AdminUserService {
  private final UserAccountMapper userAccountMapper;
  private final ShopMapper shopMapper;
  private final NotificationMapper notificationMapper;

  public AdminUserService(UserAccountMapper userAccountMapper, ShopMapper shopMapper, NotificationMapper notificationMapper) {
    this.userAccountMapper = userAccountMapper;
    this.shopMapper = shopMapper;
    this.notificationMapper = notificationMapper;
  }

  public List<AdminUserVO> listUsers(String scope, String keyword) {
    StpUtil.checkRole("admin");
    var query = Wrappers.<UserAccount>lambdaQuery();
    if (StringUtils.hasText(keyword)) {
      String like = keyword.trim();
      query.and(wrapper -> wrapper
        .like(UserAccount::getStudentId, like)
        .or()
        .like(UserAccount::getUsername, like)
        .or()
        .like(UserAccount::getCampus, like)
        .or()
        .like(UserAccount::getDepartment, like));
    }

    if ("merchant".equalsIgnoreCase(scope)) {
      query.and(wrapper -> wrapper.eq(UserAccount::getRole, "seller")
        .or()
        .eq(UserAccount::getShopRegistered, true));
    }
    else if ("student".equalsIgnoreCase(scope)) {
      query.eq(UserAccount::getRole, "student")
        .eq(UserAccount::getShopRegistered, false);
    }
    else if ("banned".equalsIgnoreCase(scope)) {
      query.eq(UserAccount::getBanned, true);
    }

    List<UserAccount> users = userAccountMapper.selectList(query.orderByDesc(UserAccount::getBanned)
      .orderByAsc(UserAccount::getRole)
      .orderByAsc(UserAccount::getStudentId));

    List<Long> userIds = users.stream().map(UserAccount::getId).filter(Objects::nonNull).toList();
    Map<Long, Shop> shopMap = shopMapper.selectList(Wrappers.<Shop>lambdaQuery()
      .in(!userIds.isEmpty(), Shop::getUserId, userIds))
      .stream()
      .collect(Collectors.toMap(Shop::getUserId, shop -> shop, (left, right) -> left));

    return users.stream()
      .map(user -> {
        Shop shop = shopMap.get(user.getId());
        boolean merchant = "seller".equalsIgnoreCase(user.getRole()) || Boolean.TRUE.equals(user.getShopRegistered());
        return new AdminUserVO(
          user.getId(),
          user.getStudentId(),
          user.getUsername(),
          user.getCampus(),
          user.getDepartment(),
          user.getRole(),
          merchant,
          Boolean.TRUE.equals(user.getShopRegistered()),
          shop == null ? "" : shop.getShopName(),
          Boolean.TRUE.equals(user.getBanned()),
          user.getBanReason(),
          user.getBannedAt(),
          user.getWalletBalance(),
          user.getCreatedAt(),
          user.getUpdatedAt()
        );
      })
      .toList();
  }

  @Transactional
  public AdminUserVO banUser(Long userId, BanUserRequest request) {
    StpUtil.checkRole("admin");
    UserAccount user = requireUser(userId);
    if ("admin".equalsIgnoreCase(user.getRole())) {
      throw new BizException("管理员账号不能封禁");
    }

    user.setBanned(true);
    user.setBanReason(normalizeBanReason(request == null ? null : request.banReason()));
    user.setBannedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    userAccountMapper.updateById(user);
    insertBanNotification(user);
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        StpUtil.kickout(user.getId());
      }
    });
    return toVO(user);
  }

  @Transactional
  public AdminUserVO unbanUser(Long userId) {
    StpUtil.checkRole("admin");
    UserAccount user = requireUser(userId);
    user.setBanned(false);
    user.setBanReason("");
    user.setBannedAt(null);
    user.setUpdatedAt(LocalDateTime.now());
    userAccountMapper.updateById(user);
    return toVO(user);
  }

  private UserAccount requireUser(Long userId) {
    UserAccount user = userAccountMapper.selectById(userId);
    if (user == null) {
      throw new BizException(404, "用户不存在");
    }
    return user;
  }

  private String normalizeBanReason(String banReason) {
    if (!StringUtils.hasText(banReason)) {
      return "管理员封禁";
    }
    return banReason.trim();
  }

  private void insertBanNotification(UserAccount user) {
    Notification notification = new Notification();
    notification.setUserId(user.getId());
    notification.setCategory("account");
    notification.setTitle("账号已封禁");
    notification.setSummary("你的账号因「" + user.getBanReason() + "」被封禁，当前登录状态已被清除。");
    notification.setStatusText("封禁");
    notification.setActionText("查看原因");
    notification.setRoute("/messages");
    notification.setUnread(true);
    notificationMapper.insert(notification);
  }

  private AdminUserVO toVO(UserAccount user) {
    Shop shop = shopMapper.selectOne(Wrappers.<Shop>lambdaQuery()
      .eq(Shop::getUserId, user.getId())
      .last("limit 1"));
    boolean merchant = "seller".equalsIgnoreCase(user.getRole()) || Boolean.TRUE.equals(user.getShopRegistered());
    return new AdminUserVO(
      user.getId(),
      user.getStudentId(),
      user.getUsername(),
      user.getCampus(),
      user.getDepartment(),
      user.getRole(),
      merchant,
      Boolean.TRUE.equals(user.getShopRegistered()),
      shop == null ? "" : shop.getShopName(),
      Boolean.TRUE.equals(user.getBanned()),
      user.getBanReason(),
      user.getBannedAt(),
      user.getWalletBalance(),
      user.getCreatedAt(),
      user.getUpdatedAt()
    );
  }
}
