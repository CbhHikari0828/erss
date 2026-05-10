package com.erss.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.erss.common.BizException;
import com.erss.domain.entity.Notification;
import com.erss.domain.entity.UserAccount;
import com.erss.domain.entity.WalletTransaction;
import com.erss.domain.entity.WalletWithdrawal;
import com.erss.dto.WithdrawRequest;
import com.erss.mapper.NotificationMapper;
import com.erss.mapper.UserAccountMapper;
import com.erss.mapper.WalletTransactionMapper;
import com.erss.mapper.WalletWithdrawalMapper;
import com.erss.vo.WalletSummaryVO;
import com.erss.vo.WalletTransactionVO;
import com.erss.vo.WalletWithdrawalVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
  private final UserAccountMapper userAccountMapper;
  private final WalletWithdrawalMapper walletWithdrawalMapper;
  private final NotificationMapper notificationMapper;
  private final WalletTransactionMapper walletTransactionMapper;

  public WalletService(
    UserAccountMapper userAccountMapper,
    WalletWithdrawalMapper walletWithdrawalMapper,
    NotificationMapper notificationMapper,
    WalletTransactionMapper walletTransactionMapper
  ) {
    this.userAccountMapper = userAccountMapper;
    this.walletWithdrawalMapper = walletWithdrawalMapper;
    this.notificationMapper = notificationMapper;
    this.walletTransactionMapper = walletTransactionMapper;
  }

  public WalletSummaryVO summary() {
    UserAccount user = currentUser();
    List<WalletWithdrawal> withdrawals = walletWithdrawalMapper.selectList(Wrappers.<WalletWithdrawal>lambdaQuery()
      .eq(WalletWithdrawal::getUserId, user.getId()));
    BigDecimal amount = withdrawals.stream()
      .filter(item -> "SUCCESS".equals(item.getStatus()))
      .map(WalletWithdrawal::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new WalletSummaryVO(normalizeBalance(user.getWalletBalance()), (long) withdrawals.size(), amount);
  }

  public List<WalletWithdrawalVO> listWithdrawals() {
    UserAccount user = currentUser();
    return walletWithdrawalMapper.selectList(Wrappers.<WalletWithdrawal>lambdaQuery()
        .eq(WalletWithdrawal::getUserId, user.getId())
        .orderByDesc(WalletWithdrawal::getRequestedAt))
      .stream()
      .map(this::toVO)
      .toList();
  }

  public List<WalletTransactionVO> listTransactions() {
    UserAccount user = currentUser();
    return walletTransactionMapper.selectList(Wrappers.<WalletTransaction>lambdaQuery()
        .eq(WalletTransaction::getUserId, user.getId())
        .orderByDesc(WalletTransaction::getCreatedAt))
      .stream()
      .map(tx -> new WalletTransactionVO(
        tx.getId(),
        tx.getType(),
        tx.getAmount(),
        tx.getBalanceAfter(),
        tx.getCategory(),
        tx.getTitle(),
        tx.getReferenceId(),
        tx.getCreatedAt()
      ))
      .toList();
  }

  @Transactional
  public WalletWithdrawalVO withdraw(WithdrawRequest request) {
    UserAccount user = currentUser();
    BigDecimal amount = request.amount();
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BizException("提现金额必须大于 0");
    }

    String channel = normalizeChannel(request.channel());
    if (!"wechat".equals(channel) && !"alipay".equals(channel)) {
      throw new BizException("请选择微信提现方式");
    }

    BigDecimal balance = normalizeBalance(user.getWalletBalance());
    if (balance.compareTo(amount) < 0) {
      throw new BizException("提现金额不能大于当前余额");
    }

    user.setWalletBalance(balance.subtract(amount));
    user.setUpdatedAt(LocalDateTime.now());
    userAccountMapper.updateById(user);

    LocalDateTime now = LocalDateTime.now();
    WalletWithdrawal withdrawal = new WalletWithdrawal();
    withdrawal.setUserId(user.getId());
    withdrawal.setAmount(amount);
    withdrawal.setChannel(channel);
    withdrawal.setDestinationAccount(trimToEmpty(request.destinationAccount()));
    withdrawal.setNote(trimToEmpty(request.note()));
    withdrawal.setStatus("SUCCESS");
    withdrawal.setRequestedAt(now);
    withdrawal.setCompletedAt(now);
    walletWithdrawalMapper.insert(withdrawal);

    WalletTransaction tx = new WalletTransaction();
    tx.setUserId(user.getId());
    tx.setType("EXPENSE");
    tx.setAmount(amount);
    tx.setBalanceAfter(balance.subtract(amount));
    tx.setCategory("WITHDRAW");
    tx.setTitle("提现");
    tx.setReferenceId("withdrawal_" + withdrawal.getId());
    tx.setCreatedAt(now);
    walletTransactionMapper.insert(tx);

    createNotification(user.getId(), "wallet", "提现成功", "你已通过" + channelText(channel) + "提现 " + amount.toPlainString() + " 元，金额已处理完成。", "已提现", "查看钱包", "/wallet");
    return toVO(withdrawal);
  }

  private UserAccount currentUser() {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    UserAccount user = userAccountMapper.selectById(userId);
    if (user == null) {
      throw new BizException(401, "登录用户不存在");
    }
    return user;
  }

  private WalletWithdrawalVO toVO(WalletWithdrawal withdrawal) {
    return new WalletWithdrawalVO(
      withdrawal.getId(),
      withdrawal.getAmount(),
      withdrawal.getChannel(),
      channelText(withdrawal.getChannel()),
      withdrawal.getDestinationAccount(),
      withdrawal.getNote(),
      withdrawal.getStatus(),
      statusText(withdrawal.getStatus()),
      withdrawal.getRequestedAt(),
      withdrawal.getCompletedAt()
    );
  }

  private void createNotification(Long userId, String category, String title, String summary, String status, String actionText, String route) {
    Notification notification = new Notification();
    notification.setUserId(userId);
    notification.setCategory(category);
    notification.setTitle(title);
    notification.setSummary(summary);
    notification.setStatusText(status);
    notification.setActionText(actionText);
    notification.setRoute(route);
    notification.setUnread(true);
    notification.setCreatedAt(LocalDateTime.now());
    notificationMapper.insert(notification);
  }

  private BigDecimal normalizeBalance(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  private String normalizeChannel(String channel) {
    return channel == null ? "" : channel.trim().toLowerCase();
  }

  private String channelText(String channel) {
    return switch (normalizeChannel(channel)) {
      case "wechat" -> "微信";
      case "alipay" -> "支付宝";
      default -> "未知渠道";
    };
  }

  private String statusText(String status) {
    return switch (status) {
      case "SUCCESS" -> "已完成";
      case "PENDING" -> "处理中";
      case "FAILED" -> "失败";
      default -> status;
    };
  }

  private String trimToEmpty(String value) {
    return value == null ? "" : value.trim();
  }
}
