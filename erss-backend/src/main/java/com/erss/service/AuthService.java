package com.erss.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.erss.common.BizException;
import com.erss.domain.entity.Shop;
import com.erss.domain.entity.Notification;
import com.erss.domain.entity.UserAccount;
import com.erss.domain.entity.WalletTransaction;
import com.erss.dto.LoginRequest;
import com.erss.dto.UpdateUserProfileRequest;
import com.erss.mapper.NotificationMapper;
import com.erss.mapper.ShopMapper;
import com.erss.mapper.UserAccountMapper;
import com.erss.mapper.WalletTransactionMapper;
import com.erss.util.PasswordHasher;
import com.erss.vo.UserInfoVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserAccountMapper userAccountMapper;
  private final ShopMapper shopMapper;
  private final NotificationMapper notificationMapper;
  private final WalletTransactionMapper walletTransactionMapper;

  public AuthService(UserAccountMapper userAccountMapper, ShopMapper shopMapper, NotificationMapper notificationMapper, WalletTransactionMapper walletTransactionMapper) {
    this.userAccountMapper = userAccountMapper;
    this.shopMapper = shopMapper;
    this.notificationMapper = notificationMapper;
    this.walletTransactionMapper = walletTransactionMapper;
  }

  @Transactional
  public String login(LoginRequest request) {
    if (request.studentId() == null || request.studentId().isBlank()) {
      throw new BizException("请输入学生号");
    }
    if (request.password() == null || request.password().isBlank()) {
      throw new BizException("请输入密码");
    }

    UserAccount user = userAccountMapper.selectOne(Wrappers.<UserAccount>lambdaQuery()
      .eq(UserAccount::getStudentId, request.studentId())
      .last("limit 1"));

    if (user == null) {
      user = createStudent(request);
    }
    else if (!PasswordHasher.sha256(request.password()).equals(user.getPasswordHash())) {
      throw new BizException("学生号或密码错误");
    }

    if (Boolean.TRUE.equals(user.getBanned())) {
      throw new BizException(403, latestBanMessage(user.getId()));
    }

    if (StpUtil.isLogin()) {
      StpUtil.logout();
    }
    StpUtil.login(user.getId());
    return StpUtil.getTokenInfo().getTokenValue();
  }

  public UserInfoVO getCurrentUserInfo() {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    UserAccount user = userAccountMapper.selectById(userId);
    if (user == null) {
      throw new BizException(401, "登录用户不存在");
    }

    Shop shop = shopMapper.selectOne(Wrappers.<Shop>lambdaQuery()
      .eq(Shop::getUserId, userId)
      .last("limit 1"));

    return new UserInfoVO(
      user.getId(),
      user.getUsername(),
      user.getAvatar(),
      user.getCampus(),
      user.getDepartment(),
      user.getBio(),
      shop == null ? "" : shop.getShopName(),
      shop == null ? "" : shop.getShopCampus(),
      shop == null ? "" : shop.getShopIntro(),
      Boolean.TRUE.equals(user.getShopRegistered()),
      user.getRole(),
      user.getWalletBalance() == null ? BigDecimal.ZERO : user.getWalletBalance()
    );
  }

  @Transactional
  public UserInfoVO updateCurrentUserProfile(UpdateUserProfileRequest request) {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    UserAccount user = userAccountMapper.selectById(userId);
    if (user == null) {
      throw new BizException(401, "登录用户不存在");
    }

    user.setUsername(request.username());
    user.setDepartment(request.department());
    user.setCampus(request.campus());
    if (request.avatar() != null) {
      user.setAvatar(request.avatar());
    }
    if (request.bio() != null) {
      user.setBio(request.bio());
    }

    boolean hasPasswordChange = request.newPassword() != null && !request.newPassword().isBlank();
    if (hasPasswordChange) {
      if (request.currentPassword() == null || request.currentPassword().isBlank()) {
        throw new BizException("请输入当前密码");
      }
      if (!PasswordHasher.sha256(request.currentPassword()).equals(user.getPasswordHash())) {
        throw new BizException("当前密码错误");
      }
      user.setPasswordHash(PasswordHasher.sha256(request.newPassword()));
    }

    user.setUpdatedAt(LocalDateTime.now());
    userAccountMapper.updateById(user);
    return getCurrentUserInfo();
  }

  private UserAccount createStudent(LoginRequest request) {
    LocalDateTime now = LocalDateTime.now();
    UserAccount user = new UserAccount();
    user.setStudentId(request.studentId());
    user.setPasswordHash(PasswordHasher.sha256(request.password()));
    user.setUsername("学生" + request.studentId().substring(Math.max(0, request.studentId().length() - 4)));
    user.setCampus("主校区");
    user.setDepartment("");
    user.setBio("");
    user.setRole("student");
    user.setShopRegistered(false);
    user.setBanned(false);
    user.setBanReason("");
    user.setWalletBalance(new java.math.BigDecimal("2.00"));
    user.setCreatedAt(now);
    user.setUpdatedAt(now);
    userAccountMapper.insert(user);

    WalletTransaction tx = new WalletTransaction();
    tx.setUserId(user.getId());
    tx.setType("INCOME");
    tx.setAmount(new java.math.BigDecimal("2.00"));
    tx.setBalanceAfter(new java.math.BigDecimal("2.00"));
    tx.setCategory("REGISTER_BONUS");
    tx.setTitle("注册赠送余额");
    tx.setCreatedAt(now);
    walletTransactionMapper.insert(tx);

    return user;
  }

  private String latestBanMessage(Long userId) {
    Notification notification = notificationMapper.selectOne(Wrappers.<Notification>lambdaQuery()
      .eq(Notification::getUserId, userId)
      .eq(Notification::getCategory, "account")
      .orderByDesc(Notification::getCreatedAt)
      .last("limit 1"));
    if (notification == null) {
      return "账号已被封禁，请联系管理员";
    }
    return notification.getTitle() + "：" + notification.getSummary();
  }
}
