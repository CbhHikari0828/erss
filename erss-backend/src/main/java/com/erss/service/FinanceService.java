package com.erss.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.erss.common.BizException;
import com.erss.domain.entity.Book;
import com.erss.domain.entity.Notification;
import com.erss.domain.entity.PayoutRequest;
import com.erss.domain.entity.SystemWallet;
import com.erss.domain.entity.TradeOrder;
import com.erss.domain.entity.UserAccount;
import com.erss.domain.entity.WalletTransaction;
import com.erss.dto.ReleasePayoutRequest;
import com.erss.mapper.BookMapper;
import com.erss.mapper.NotificationMapper;
import com.erss.mapper.PayoutRequestMapper;
import com.erss.mapper.SystemWalletMapper;
import com.erss.mapper.TradeOrderMapper;
import com.erss.mapper.UserAccountMapper;
import com.erss.mapper.WalletTransactionMapper;
import com.erss.vo.FinanceSummaryVO;
import com.erss.vo.PayoutAdminVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class FinanceService {
  private final SystemWalletMapper systemWalletMapper;
  private final PayoutRequestMapper payoutRequestMapper;
  private final TradeOrderMapper tradeOrderMapper;
  private final BookMapper bookMapper;
  private final UserAccountMapper userAccountMapper;
  private final NotificationMapper notificationMapper;
  private final WalletTransactionMapper walletTransactionMapper;

  public FinanceService(
    SystemWalletMapper systemWalletMapper,
    PayoutRequestMapper payoutRequestMapper,
    TradeOrderMapper tradeOrderMapper,
    BookMapper bookMapper,
    UserAccountMapper userAccountMapper,
    NotificationMapper notificationMapper,
    WalletTransactionMapper walletTransactionMapper
  ) {
    this.systemWalletMapper = systemWalletMapper;
    this.payoutRequestMapper = payoutRequestMapper;
    this.tradeOrderMapper = tradeOrderMapper;
    this.bookMapper = bookMapper;
    this.userAccountMapper = userAccountMapper;
    this.notificationMapper = notificationMapper;
    this.walletTransactionMapper = walletTransactionMapper;
  }

  public FinanceSummaryVO summary() {
    StpUtil.checkRole("admin");
    SystemWallet wallet = getOrCreateWallet();
    BigDecimal holdingAmount = payoutRequestMapper.selectList(Wrappers.<PayoutRequest>lambdaQuery()
      .eq(PayoutRequest::getStatus, "ESCROW_HOLDING"))
      .stream()
      .map(PayoutRequest::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
    Long holdingCount = payoutRequestMapper.selectCount(Wrappers.<PayoutRequest>lambdaQuery()
      .eq(PayoutRequest::getStatus, "ESCROW_HOLDING"));
    BigDecimal pendingAmount = payoutRequestMapper.selectList(Wrappers.<PayoutRequest>lambdaQuery()
      .eq(PayoutRequest::getStatus, "PENDING"))
      .stream()
      .map(PayoutRequest::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
    Long pendingCount = payoutRequestMapper.selectCount(Wrappers.<PayoutRequest>lambdaQuery()
      .eq(PayoutRequest::getStatus, "PENDING"));
    Long releasedCount = payoutRequestMapper.selectCount(Wrappers.<PayoutRequest>lambdaQuery()
      .eq(PayoutRequest::getStatus, "PAID"));
    BigDecimal releasedAmount = payoutRequestMapper.selectList(Wrappers.<PayoutRequest>lambdaQuery()
      .eq(PayoutRequest::getStatus, "PAID"))
      .stream()
      .map(PayoutRequest::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new FinanceSummaryVO(
      wallet.getEscrowBalance(),
      holdingCount,
      holdingAmount,
      pendingCount,
      pendingAmount,
      releasedCount,
      releasedAmount
    );
  }

  public List<PayoutAdminVO> listPayouts(String status) {
    StpUtil.checkRole("admin");
    String normalizedStatus = normalizeStatus(status);
    return payoutRequestMapper.selectList(Wrappers.<PayoutRequest>lambdaQuery()
        .eq(normalizedStatus != null, PayoutRequest::getStatus, normalizedStatus)
        .orderByDesc(PayoutRequest::getRequestedAt))
      .stream()
      .map(this::toVO)
      .toList();
  }

  @Transactional
  public PayoutAdminVO releasePayout(Long id, ReleasePayoutRequest request) {
    StpUtil.checkRole("admin");
    PayoutRequest payoutRequest = payoutRequestMapper.selectById(id);
    if (payoutRequest == null) {
      throw new BizException("待发放记录不存在");
    }
    if (!"PENDING".equals(payoutRequest.getStatus())) {
      throw new BizException("该笔款项已处理");
    }

    TradeOrder order = tradeOrderMapper.selectById(payoutRequest.getOrderId());
    if (order == null) {
      throw new BizException("订单不存在");
    }

    SystemWallet wallet = getOrCreateWallet();
    if (wallet.getEscrowBalance().compareTo(payoutRequest.getAmount()) < 0) {
      throw new BizException("系统托管余额不足");
    }

    UserAccount seller = userAccountMapper.selectById(payoutRequest.getSellerId());
    if (seller == null) {
      throw new BizException("卖家不存在");
    }

    wallet.setEscrowBalance(normalizeBalance(wallet.getEscrowBalance()).subtract(payoutRequest.getAmount()));
    wallet.setUpdatedAt(LocalDateTime.now());
    systemWalletMapper.updateById(wallet);

    BigDecimal afterBalance = normalizeBalance(seller.getWalletBalance()).add(payoutRequest.getAmount());
    seller.setWalletBalance(afterBalance);
    seller.setUpdatedAt(LocalDateTime.now());
    userAccountMapper.updateById(seller);

    WalletTransaction tx = new WalletTransaction();
    tx.setUserId(seller.getId());
    tx.setType("INCOME");
    tx.setAmount(payoutRequest.getAmount());
    tx.setBalanceAfter(afterBalance);
    tx.setCategory("PAYOUT_RELEASE");
    tx.setTitle("售书收入到账");
    tx.setReferenceId("payout_" + payoutRequest.getId());
    tx.setCreatedAt(LocalDateTime.now());
    walletTransactionMapper.insert(tx);

    payoutRequest.setStatus("PAID");
    payoutRequest.setReleasedAt(LocalDateTime.now());
    payoutRequest.setReleasedBy(Long.valueOf(String.valueOf(StpUtil.getLoginId())));
    payoutRequest.setReleaseNote(StringUtils.hasText(request == null ? null : request.releaseNote())
      ? request.releaseNote().trim()
      : "管理员发放");
    payoutRequestMapper.updateById(payoutRequest);

    order.setStatus("COMPLETED");
    order.setEscrowStatus("ESCROW_RELEASED");
    order.setUpdatedAt(LocalDateTime.now());
    tradeOrderMapper.updateById(order);

    Book book = bookMapper.selectById(order.getBookId());
    String bookTitle = book == null ? "商品" : book.getTitle();
    createNotification(seller.getId(), "trade", "资金已发放", "你在订单「" + bookTitle + "」的待发放金额已进入钱包，当前余额可提现。", "已发放", "查看钱包", "/my-shop");
    createNotification(order.getBuyerId(), "trade", "订单已完成", "你已确认收货，平台已完成对卖家的资金发放。", "已完成", "查看订单", "/orders");

    return toVO(payoutRequest);
  }

  private SystemWallet getOrCreateWallet() {
    SystemWallet wallet = systemWalletMapper.selectById(1L);
    if (wallet != null) {
      return wallet;
    }
    wallet = new SystemWallet();
    wallet.setId(1L);
    wallet.setEscrowBalance(BigDecimal.ZERO);
    wallet.setCreatedAt(LocalDateTime.now());
    wallet.setUpdatedAt(LocalDateTime.now());
    systemWalletMapper.insert(wallet);
    return wallet;
  }

  private PayoutAdminVO toVO(PayoutRequest request) {
    TradeOrder order = tradeOrderMapper.selectById(request.getOrderId());
    Book book = order == null ? null : bookMapper.selectById(order.getBookId());
    UserAccount seller = request.getSellerId() == null ? null : userAccountMapper.selectById(request.getSellerId());
    UserAccount buyer = order == null ? null : userAccountMapper.selectById(order.getBuyerId());
    return new PayoutAdminVO(
      request.getId(),
      request.getOrderId(),
      order == null ? "" : order.getOrderNo(),
      request.getSellerId(),
      seller == null ? "" : seller.getUsername(),
      order == null ? null : order.getBuyerId(),
      buyer == null ? "" : buyer.getUsername(),
      book == null ? "" : book.getTitle(),
      order == null ? "" : order.getOrderType(),
      order == null ? "" : order.getPaymentMethod(),
      request.getAmount(),
      request.getStatus(),
      request.getRequestedAt(),
      request.getReleasedAt(),
      request.getReleaseNote()
    );
  }

  private BigDecimal normalizeBalance(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  private String normalizeStatus(String status) {
    if (status == null || status.isBlank() || "all".equalsIgnoreCase(status)) {
      return null;
    }
    return status.trim().toUpperCase();
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
}
