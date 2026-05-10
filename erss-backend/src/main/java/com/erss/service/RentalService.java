package com.erss.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.erss.common.BizException;
import com.erss.domain.entity.Book;
import com.erss.domain.entity.DamageReport;
import com.erss.domain.entity.Notification;
import com.erss.domain.entity.PayoutRequest;
import com.erss.domain.entity.RentalOrder;
import com.erss.domain.entity.SystemTransaction;
import com.erss.domain.entity.SystemWallet;
import com.erss.domain.entity.TradeOrder;
import com.erss.domain.entity.UserAccount;
import com.erss.domain.entity.WalletTransaction;
import com.erss.dto.DamageReportRequest;
import com.erss.dto.DamageReviewRequest;
import com.erss.mapper.BookMapper;
import com.erss.mapper.DamageReportMapper;
import com.erss.mapper.NotificationMapper;
import com.erss.mapper.PayoutRequestMapper;
import com.erss.mapper.RentalOrderMapper;
import com.erss.mapper.SystemTransactionMapper;
import com.erss.mapper.SystemWalletMapper;
import com.erss.mapper.TradeOrderMapper;
import com.erss.mapper.UserAccountMapper;
import com.erss.mapper.WalletTransactionMapper;
import com.erss.util.TextLists;
import com.erss.vo.DamageReportAdminVO;
import com.erss.vo.DamageReportVO;
import com.erss.vo.RentalOrderVO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RentalService {
  private final RentalOrderMapper rentalOrderMapper;
  private final DamageReportMapper damageReportMapper;
  private final BookMapper bookMapper;
  private final UserAccountMapper userAccountMapper;
  private final NotificationMapper notificationMapper;
  private final TradeOrderMapper tradeOrderMapper;
  private final SystemWalletMapper systemWalletMapper;
  private final WalletTransactionMapper walletTransactionMapper;
  private final PayoutRequestMapper payoutRequestMapper;
  private final SystemTransactionMapper systemTransactionMapper;

  public RentalService(
    RentalOrderMapper rentalOrderMapper,
    DamageReportMapper damageReportMapper,
    BookMapper bookMapper,
    UserAccountMapper userAccountMapper,
    NotificationMapper notificationMapper,
    TradeOrderMapper tradeOrderMapper,
    SystemWalletMapper systemWalletMapper,
    WalletTransactionMapper walletTransactionMapper,
    PayoutRequestMapper payoutRequestMapper,
    SystemTransactionMapper systemTransactionMapper
  ) {
    this.rentalOrderMapper = rentalOrderMapper;
    this.damageReportMapper = damageReportMapper;
    this.bookMapper = bookMapper;
    this.userAccountMapper = userAccountMapper;
    this.notificationMapper = notificationMapper;
    this.tradeOrderMapper = tradeOrderMapper;
    this.systemWalletMapper = systemWalletMapper;
    this.walletTransactionMapper = walletTransactionMapper;
    this.payoutRequestMapper = payoutRequestMapper;
    this.systemTransactionMapper = systemTransactionMapper;
  }

  public List<RentalOrderVO> listMine() {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    return rentalOrderMapper.selectList(Wrappers.<RentalOrder>lambdaQuery()
        .and(query -> query.eq(RentalOrder::getBorrowerId, userId).or().eq(RentalOrder::getOwnerId, userId))
        .orderByDesc(RentalOrder::getCreatedAt))
      .stream()
      .map(this::toRentalOrderVO)
      .toList();
  }

  public List<DamageReportAdminVO> listDamageReports(String status) {
    StpUtil.checkRole("admin");
    String normalizedStatus = normalizeDamageStatus(status);
    return damageReportMapper.selectList(Wrappers.<DamageReport>lambdaQuery()
        .eq(normalizedStatus != null, DamageReport::getStatus, normalizedStatus)
        .orderByDesc(DamageReport::getCreatedAt))
      .stream()
      .map(this::toDamageReportAdminVO)
      .toList();
  }

  @Transactional
  public void submitDamageReport(Long rentalOrderId, DamageReportRequest request) {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    RentalOrder rentalOrder = rentalOrderMapper.selectById(rentalOrderId);
    if (rentalOrder == null) {
      throw new BizException("租借订单不存在");
    }
    if (!rentalOrder.getOwnerId().equals(userId)) {
      throw new BizException("只有卖家可以提交报损");
    }
    if (!isRentalTrade(rentalOrder)) {
      throw new BizException("只有租赁订单可以报损");
    }
    DamageReport existing = damageReportMapper.selectOne(Wrappers.<DamageReport>lambdaQuery()
      .eq(DamageReport::getRentalOrderId, rentalOrderId)
      .last("limit 1"));
    if (existing != null) {
      throw new BizException("该订单已提交报损");
    }

    DamageReport report = new DamageReport();
    report.setRentalOrderId(rentalOrderId);
    report.setReporterId(userId);
    report.setDescription(request.description());
    report.setImages(TextLists.join(request.images()));
    report.setStatus("PENDING");
    report.setCreatedAt(LocalDateTime.now());
    damageReportMapper.insert(report);

    rentalOrder.setStatus("DAMAGE_PENDING");
    rentalOrder.setUpdatedAt(LocalDateTime.now());
    rentalOrderMapper.updateById(rentalOrder);
  }

  @Transactional
  public void reviewDamageReport(Long reportId, DamageReviewRequest request) {
    StpUtil.checkRole("admin");
    DamageReport report = damageReportMapper.selectById(reportId);
    if (report == null) {
      throw new BizException("报损记录不存在");
    }
    if ("REVIEWED".equals(report.getStatus())) {
      throw new BizException("报损记录已审核");
    }
    RentalOrder rentalOrder = rentalOrderMapper.selectById(report.getRentalOrderId());
    if (rentalOrder == null) {
      throw new BizException("租借订单不存在");
    }
    UserAccount owner = userAccountMapper.selectById(rentalOrder.getOwnerId());
    UserAccount borrower = userAccountMapper.selectById(rentalOrder.getBorrowerId());
    if (owner == null) {
      throw new BizException("书籍所有人不存在");
    }
    if (borrower == null) {
      throw new BizException("租借人不存在");
    }

    BigDecimal deposit = normalizeBalance(rentalOrder.getDepositAmount());
    BigDecimal compensationAmount = normalizeBalance(request == null ? null : request.compensationAmount());
    if (compensationAmount.compareTo(BigDecimal.ZERO) < 0) {
      throw new BizException("赔付金额不能小于 0");
    }
    if (compensationAmount.compareTo(deposit) > 0) {
      throw new BizException("赔付金额不能超过押金");
    }
    BigDecimal refundAmount = deposit.subtract(compensationAmount);

    report.setStatus("REVIEWED");
    report.setReviewNote(request == null ? "" : request.reviewNote());
    report.setCompensationAmount(compensationAmount.compareTo(BigDecimal.ZERO) > 0 ? compensationAmount : null);
    report.setReviewedAt(LocalDateTime.now());
    damageReportMapper.updateById(report);

    releaseDeposit(rentalOrder, borrower, owner, refundAmount, compensationAmount);

    rentalOrder.setStatus(compensationAmount.compareTo(BigDecimal.ZERO) == 0 ? "RETURNABLE" : "COMPENSATION_REQUIRED");
    rentalOrder.setUpdatedAt(LocalDateTime.now());
    rentalOrderMapper.updateById(rentalOrder);

    String statusText = compensationAmount.compareTo(BigDecimal.ZERO) == 0 ? "通过" : "通过并赔付";
    String borrowerSummary = compensationAmount.compareTo(BigDecimal.ZERO) == 0
      ? "报损已审核，无需赔付，押金 " + refundAmount.toPlainString() + " 元已退回你的钱包。"
      : "报损已审核，押金中 " + compensationAmount.toPlainString() + " 元赔付给书主，" + refundAmount.toPlainString() + " 元已退回你的钱包。";
    createNotification(borrower.getId(), "rental", "报损审核结果", borrowerSummary, statusText, "查看钱包", "/wallet");
    String ownerSummary = compensationAmount.compareTo(BigDecimal.ZERO) == 0
      ? "报损已审核，无需赔付，租客押金已退回。"
      : "报损已审核，你收到报损赔付 " + compensationAmount.toPlainString() + " 元，已记入你的余额。";
    createNotification(owner.getId(), "rental", "报损审核结果", ownerSummary, statusText, "查看详情", "/message");
    if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
      createNotification(borrower.getId(), "wallet", "押金已退回", "租赁押金退回 " + refundAmount.toPlainString() + " 元，已记入你的钱包余额。", "已退回", "查看钱包", "/wallet");
    }
    if (compensationAmount.compareTo(BigDecimal.ZERO) > 0) {
      createNotification(owner.getId(), "wallet", "赔付已入账", "你收到报损赔付 " + compensationAmount.toPlainString() + " 元，已记入余额。", "已入账", "查看钱包", "/wallet");
    }
  }

  @Transactional
  public RentalOrderVO initiateReturn(Long rentalOrderId, String deliveryMethod) {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    RentalOrder rentalOrder = rentalOrderMapper.selectById(rentalOrderId);
    if (rentalOrder == null || !rentalOrder.getBorrowerId().equals(userId)) {
      throw new BizException("租借订单不存在");
    }
    if ("RETURNED".equals(rentalOrder.getStatus()) || "RETURNING".equals(rentalOrder.getStatus())) {
      throw new BizException("当前状态不能发起还书");
    }
    if ("DAMAGE_PENDING".equals(rentalOrder.getStatus())) {
      throw new BizException("报损审核中，请等待管理员鉴定后再还书");
    }
    if ("WAIT_SHIP".equals(rentalOrder.getStatus()) || "WAIT_RECEIVE".equals(rentalOrder.getStatus())) {
      throw new BizException("请先确认收货后再发起还书");
    }
    TradeOrder tradeOrder = tradeOrderMapper.selectById(rentalOrder.getTradeOrderId());
    if (tradeOrder != null && !"WAIT_RECEIVE".equals(tradeOrder.getStatus())) {
      throw new BizException("书籍尚未送达，无法还书");
    }

    String normalizedMethod = normalizeDeliveryMethod(deliveryMethod);
    boolean isStation = "station".equals(normalizedMethod);

    if (isStation) {
      UserAccount borrower = userAccountMapper.selectById(rentalOrder.getBorrowerId());
      if (borrower == null) {
        throw new BizException("租借人不存在");
      }
      BigDecimal fee = new BigDecimal("3.00");
      BigDecimal balance = normalizeBalance(borrower.getWalletBalance());
      if (balance.compareTo(fee) < 0) {
        throw new BizException("钱包余额不足，无法支付还书驿站费");
      }
      BigDecimal after = balance.subtract(fee);
      borrower.setWalletBalance(after);
      borrower.setUpdatedAt(LocalDateTime.now());
      userAccountMapper.updateById(borrower);

      SystemWallet wallet = getOrCreateWallet();
      wallet.setEscrowBalance(normalizeBalance(wallet.getEscrowBalance()).add(fee));
      wallet.setUpdatedAt(LocalDateTime.now());
      systemWalletMapper.updateById(wallet);

      WalletTransaction tx = new WalletTransaction();
      tx.setUserId(borrower.getId());
      tx.setType("EXPENSE");
      tx.setAmount(fee);
      tx.setBalanceAfter(after);
      tx.setCategory("RETURN_DELIVERY_FEE");
      tx.setTitle("还书驿站服务费");
      tx.setReferenceId("rental_" + rentalOrder.getId());
      tx.setCreatedAt(LocalDateTime.now());
      walletTransactionMapper.insert(tx);

      SystemTransaction stx = new SystemTransaction();
      stx.setCategory("RETURN_DELIVERY_FEE");
      stx.setAmount(fee);
      stx.setDescription("还书驿站费 租借#" + rentalOrder.getId());
      stx.setReferenceId("rental_" + rentalOrder.getId());
      stx.setCreatedAt(LocalDateTime.now());
      systemTransactionMapper.insert(stx);
    }

    rentalOrder.setReturnDeliveryMethod(normalizedMethod);
    rentalOrder.setStatus("RETURNING");
    rentalOrder.setUpdatedAt(LocalDateTime.now());
    rentalOrderMapper.updateById(rentalOrder);

    String methodText = isStation ? "快递驿站" : "上门自提";
    UserAccount borrower = userAccountMapper.selectById(rentalOrder.getBorrowerId());
    createNotification(rentalOrder.getOwnerId(), "rental", "买家已发起还书",
      (borrower == null ? "买家" : borrower.getUsername()) + "已发起还书（" + methodText + "），请确认还书。",
      "待确认", "查看租借", "/my-shop");

    return toRentalOrderVO(rentalOrder);
  }

  @Transactional
  public RentalOrderVO confirmReceipt(Long rentalOrderId) {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    RentalOrder rentalOrder = rentalOrderMapper.selectById(rentalOrderId);
    if (rentalOrder == null || !rentalOrder.getBorrowerId().equals(userId)) {
      throw new BizException("租借订单不存在");
    }
    if (!"WAIT_RECEIVE".equals(rentalOrder.getStatus())) {
      throw new BizException("当前状态不能确认收货");
    }
    TradeOrder tradeOrder = tradeOrderMapper.selectById(rentalOrder.getTradeOrderId());
    if (tradeOrder == null || !"WAIT_RECEIVE".equals(tradeOrder.getStatus())) {
      throw new BizException("书籍尚未发货，无法确认收货");
    }

    int days = rentalDays(rentalOrder.getPeriodText());
    LocalDateTime now = LocalDateTime.now();
    rentalOrder.setStatus("ACTIVE");
    rentalOrder.setDueAt(now.plusDays(days));
    rentalOrder.setUpdatedAt(now);
    rentalOrderMapper.updateById(rentalOrder);

    return toRentalOrderVO(rentalOrder);
  }

  @Transactional
  public void confirmReturn(Long rentalOrderId) {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    RentalOrder rentalOrder = rentalOrderMapper.selectById(rentalOrderId);
    if (rentalOrder == null || !rentalOrder.getOwnerId().equals(userId)) {
      throw new BizException("租借订单不存在");
    }
    if ("RETURNED".equals(rentalOrder.getStatus())) {
      return;
    }
    if (!"RETURNING".equals(rentalOrder.getStatus()) && !"RETURNABLE".equals(rentalOrder.getStatus()) && !"COMPENSATION_REQUIRED".equals(rentalOrder.getStatus())) {
      throw new BizException("买家尚未发起还书，无法确认");
    }
    DamageReport report = damageReportMapper.selectOne(Wrappers.<DamageReport>lambdaQuery()
      .eq(DamageReport::getRentalOrderId, rentalOrderId)
      .last("limit 1"));
    if (report != null && "PENDING".equals(report.getStatus())) {
      throw new BizException("报损审核中，暂不能确认还书");
    }

    TradeOrder tradeOrder = tradeOrderMapper.selectById(rentalOrder.getTradeOrderId());
    if (tradeOrder == null) {
      throw new BizException("关联订单不存在");
    }

    LocalDateTime now = LocalDateTime.now();
    boolean hasDamage = report != null && "REVIEWED".equals(report.getStatus());
    BigDecimal overduePenalty = calculateOverduePenalty(rentalOrder, tradeOrder, now);

    if (!hasDamage) {
      UserAccount borrower = userAccountMapper.selectById(rentalOrder.getBorrowerId());
      if (borrower != null) {
        BigDecimal deposit = normalizeBalance(rentalOrder.getDepositAmount());
        BigDecimal refundAmount = deposit.subtract(overduePenalty);
        if (refundAmount.compareTo(BigDecimal.ZERO) < 0) {
          refundAmount = BigDecimal.ZERO;
        }
        refundDeposit(rentalOrder, borrower, refundAmount);
        String penaltyNote = overduePenalty.compareTo(BigDecimal.ZERO) > 0
          ? "（逾期扣减 " + overduePenalty.toPlainString() + " 元）"
          : "";
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
          createNotification(borrower.getId(), "wallet", "押金已退回", "租赁归还完成，押金退还 " + refundAmount.toPlainString() + " 元" + penaltyNote + "。", "已退回", "查看钱包", "/wallet");
        } else if (overduePenalty.compareTo(BigDecimal.ZERO) > 0) {
          createNotification(borrower.getId(), "wallet", "押金已抵扣", "租赁归还完成，押金已全额抵扣逾期费用" + penaltyNote + "。", "已退回", "查看钱包", "/wallet");
        }
      }
    }

    rentalOrder.setStatus("RETURNED");
    rentalOrder.setUpdatedAt(now);
    rentalOrderMapper.updateById(rentalOrder);

    Book book = bookMapper.selectById(rentalOrder.getBookId());
    if (book != null && "RENTED".equals(book.getStatus())) {
      book.setStatus("ON_SALE");
      book.setUpdatedAt(now);
      bookMapper.updateById(book);
    }

    BigDecimal basePayout = resolvePayoutAmount(tradeOrder);
    BigDecimal damageServiceFee = hasDamage ? new BigDecimal("1.00") : BigDecimal.ZERO;
    BigDecimal sellerOverdueShare = overduePenalty.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
    BigDecimal systemOverdueShare = overduePenalty.subtract(sellerOverdueShare);

    if (hasDamage) {
      PayoutRequest existing = payoutRequestMapper.selectOne(Wrappers.<PayoutRequest>lambdaQuery()
        .eq(PayoutRequest::getOrderId, tradeOrder.getId())
        .last("limit 1"));
      if (existing != null) {
        payoutRequestMapper.deleteById(existing.getId());
      }

      BigDecimal systemRevenue = basePayout.add(systemOverdueShare).add(damageServiceFee);
      if (systemRevenue.compareTo(BigDecimal.ZERO) > 0) {
        SystemWallet wallet = getOrCreateWallet();
        wallet.setEscrowBalance(normalizeBalance(wallet.getEscrowBalance()).add(systemRevenue));
        wallet.setUpdatedAt(now);
        systemWalletMapper.updateById(wallet);
      }
      if (damageServiceFee.compareTo(BigDecimal.ZERO) > 0) {
        SystemTransaction stx = new SystemTransaction();
        stx.setCategory("DAMAGE_SERVICE_FEE");
        stx.setAmount(damageServiceFee);
        stx.setDescription("报损服务费 租借#" + rentalOrder.getId());
        stx.setReferenceId("rental_" + rentalOrder.getId());
        stx.setCreatedAt(now);
        systemTransactionMapper.insert(stx);
      }
      if (systemOverdueShare.compareTo(BigDecimal.ZERO) > 0) {
        SystemTransaction stx = new SystemTransaction();
        stx.setCategory("OVERDUE_PENALTY_SHARE");
        stx.setAmount(systemOverdueShare);
        stx.setDescription("逾期罚金系统分成 租借#" + rentalOrder.getId());
        stx.setReferenceId("rental_" + rentalOrder.getId());
        stx.setCreatedAt(now);
        systemTransactionMapper.insert(stx);
      }
      if (basePayout.compareTo(BigDecimal.ZERO) > 0) {
        SystemTransaction stx = new SystemTransaction();
        stx.setCategory("DAMAGE_SERVICE_FEE");
        stx.setAmount(basePayout);
        stx.setDescription("报损订单租金收入 租借#" + rentalOrder.getId());
        stx.setReferenceId("rental_" + rentalOrder.getId());
        stx.setCreatedAt(now);
        systemTransactionMapper.insert(stx);
      }

      tradeOrder.setStatus("COMPLETED");
      tradeOrder.setEscrowStatus("ESCROW_RELEASED");
      tradeOrder.setUpdatedAt(now);
      tradeOrderMapper.updateById(tradeOrder);
    } else {
      BigDecimal finalPayout = basePayout.add(sellerOverdueShare);
      if (finalPayout.compareTo(BigDecimal.ZERO) < 0) {
        finalPayout = BigDecimal.ZERO;
      }

      if (systemOverdueShare.compareTo(BigDecimal.ZERO) > 0) {
        SystemWallet wallet = getOrCreateWallet();
        wallet.setEscrowBalance(normalizeBalance(wallet.getEscrowBalance()).add(systemOverdueShare));
        wallet.setUpdatedAt(now);
        systemWalletMapper.updateById(wallet);

        SystemTransaction stx = new SystemTransaction();
        stx.setCategory("OVERDUE_PENALTY_SHARE");
        stx.setAmount(systemOverdueShare);
        stx.setDescription("逾期罚金系统分成 租借#" + rentalOrder.getId());
        stx.setReferenceId("rental_" + rentalOrder.getId());
        stx.setCreatedAt(now);
        systemTransactionMapper.insert(stx);
      }

      PayoutRequest payoutRequest = payoutRequestMapper.selectOne(Wrappers.<PayoutRequest>lambdaQuery()
        .eq(PayoutRequest::getOrderId, tradeOrder.getId())
        .last("limit 1"));
      if (payoutRequest == null) {
        payoutRequest = new PayoutRequest();
        payoutRequest.setOrderId(tradeOrder.getId());
        payoutRequest.setSellerId(tradeOrder.getSellerId());
        payoutRequest.setAmount(finalPayout);
        payoutRequest.setStatus("PENDING");
        payoutRequest.setRequestedAt(now);
        payoutRequestMapper.insert(payoutRequest);
      } else {
        payoutRequest.setAmount(finalPayout);
        payoutRequest.setStatus("PENDING");
        payoutRequest.setRequestedAt(now);
        payoutRequest.setReleasedAt(null);
        payoutRequest.setReleasedBy(null);
        payoutRequest.setReleaseNote("");
        payoutRequestMapper.updateById(payoutRequest);
      }

      tradeOrder.setStatus("PAYOUT_PENDING");
      tradeOrder.setEscrowStatus("ESCROW_RELEASE_PENDING");
      tradeOrder.setUpdatedAt(now);
      tradeOrderMapper.updateById(tradeOrder);
    }

    if (hasDamage) {
      createNotification(tradeOrder.getSellerId(), "trade", "还书确认完成",
        "买家已归还，报损赔付已直接入账，订单完结。", "已完成", "查看订单", "/orders");
      createNotification(tradeOrder.getBuyerId(), "trade", "还书确认完成",
        "卖家已确认还书，订单完结。", "已完成", "查看订单", "/orders");
    } else {
      String feeNote = overduePenalty.compareTo(BigDecimal.ZERO) > 0 ? "（已扣除逾期罚金系统分成）" : "";
      createNotification(tradeOrder.getSellerId(), "trade", "待发放资金",
        "买家已归还，订单金额 " + basePayout.add(sellerOverdueShare).toPlainString() + " 元" + feeNote + " 已进入待发放状态，等待管理员处理。", "待发放", "查看资金", "/my-shop");
      createNotification(tradeOrder.getBuyerId(), "trade", "还书确认完成",
        "卖家已确认还书，系统将进入资金发放流程。", "已完成", "查看订单", "/orders");

      List<UserAccount> admins = userAccountMapper.selectList(Wrappers.<UserAccount>lambdaQuery()
        .eq(UserAccount::getRole, "admin"));
      for (UserAccount admin : admins) {
        createNotification(admin.getId(), "system", "新的待发放资金",
          "租赁订单「" + tradeOrder.getOrderNo() + "」还书已确认，进入待发放状态。", "待处理", "进入资金管理", "/finance");
      }
    }
  }

  private RentalOrderVO toRentalOrderVO(RentalOrder rentalOrder) {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    Book book = bookMapper.selectById(rentalOrder.getBookId());
    UserAccount borrower = userAccountMapper.selectById(rentalOrder.getBorrowerId());
    DamageReport report = damageReportMapper.selectOne(Wrappers.<DamageReport>lambdaQuery()
      .eq(DamageReport::getRentalOrderId, rentalOrder.getId())
      .last("limit 1"));
    boolean ownedByMe = rentalOrder.getOwnerId().equals(userId);
    boolean isRental = isRentalTrade(rentalOrder);
    String status = rentalOrder.getStatus();
    boolean canReport = ownedByMe && isRental && report == null
      && ("RETURNING".equals(status) || "RETURNABLE".equals(status) || "COMPENSATION_REQUIRED".equals(status));
    boolean canConfirm = ownedByMe && ("RETURNING".equals(status) || "RETURNABLE".equals(status) || "COMPENSATION_REQUIRED".equals(status));
    TradeOrder tradeOrder = tradeOrderMapper.selectById(rentalOrder.getTradeOrderId());
    boolean tradeReceived = tradeOrder != null && "WAIT_RECEIVE".equals(tradeOrder.getStatus());
    boolean canInitiate = !ownedByMe && ("ACTIVE".equals(status) || "RETURNABLE".equals(status) || "COMPENSATION_REQUIRED".equals(status)) && tradeReceived;
    int overdueDays = 0;
    if (rentalOrder.getDueAt() != null) {
      LocalDateTime now = LocalDateTime.now();
      if (now.isAfter(rentalOrder.getDueAt())) {
        long seconds = Duration.between(rentalOrder.getDueAt(), now).getSeconds();
        overdueDays = (int) Math.ceil(seconds / 86400.0);
      }
    }
    return new RentalOrderVO(
      rentalOrder.getId(),
      rentalOrder.getBookId(),
      book == null ? "" : book.getTitle(),
      rentalOrder.getPeriodText(),
      rentalOrder.getDepositAmount(),
      rentalOrder.getReturnDeliveryMethod(),
      rentalStatusText(status),
      rentalOrder.getDueAt(),
      borrower == null ? "" : borrower.getUsername(),
      ownedByMe,
      canReport,
      canConfirm,
      canInitiate,
      report == null ? null : toDamageReportVO(report),
      overdueDays
    );
  }

  private boolean isRentalTrade(RentalOrder rentalOrder) {
    if (rentalOrder == null || rentalOrder.getTradeOrderId() == null) {
      return false;
    }
    TradeOrder tradeOrder = tradeOrderMapper.selectById(rentalOrder.getTradeOrderId());
    return tradeOrder != null && "rental".equals(tradeOrder.getOrderType());
  }

  private DamageReportVO toDamageReportVO(DamageReport report) {
    return new DamageReportVO(
      report.getId(),
      damageStatusText(report.getStatus()),
      report.getDescription(),
      TextLists.split(report.getImages()),
      report.getCreatedAt(),
      report.getReviewNote(),
      report.getCompensationAmount()
    );
  }

  private DamageReportAdminVO toDamageReportAdminVO(DamageReport report) {
    RentalOrder rentalOrder = rentalOrderMapper.selectById(report.getRentalOrderId());
    TradeOrder tradeOrder = rentalOrder == null ? null : tradeOrderMapper.selectById(rentalOrder.getTradeOrderId());
    Book book = rentalOrder == null ? null : bookMapper.selectById(rentalOrder.getBookId());
    UserAccount reporter = userAccountMapper.selectById(report.getReporterId());
    UserAccount owner = rentalOrder == null ? null : userAccountMapper.selectById(rentalOrder.getOwnerId());
    UserAccount borrower = rentalOrder == null ? null : userAccountMapper.selectById(rentalOrder.getBorrowerId());
    return new DamageReportAdminVO(
      report.getId(),
      report.getRentalOrderId(),
      rentalOrder == null ? null : rentalOrder.getTradeOrderId(),
      rentalOrder == null ? null : rentalOrder.getBookId(),
      book == null ? "" : book.getTitle(),
      book == null ? null : book.getPrice(),
      book == null ? null : book.getOriginalPrice(),
      tradeOrder == null ? null : tradeOrder.getUnitPrice(),
      rentalOrder == null ? null : rentalOrder.getDepositAmount(),
      tradeOrder == null ? null : tradeOrder.getDeliveryFee(),
      tradeOrder == null ? null : tradeOrder.getTotalAmount(),
      tradeOrder == null ? "" : tradeOrder.getOrderNo(),
      reporter == null ? "" : reporter.getUsername(),
      owner == null ? "" : owner.getUsername(),
      borrower == null ? "" : borrower.getUsername(),
      report.getDescription(),
      TextLists.split(report.getImages()),
      damageStatusText(report.getStatus()),
      report.getReviewNote(),
      report.getCompensationAmount(),
      rentalOrder == null ? "" : rentalStatusText(rentalOrder.getStatus()),
      rentalOrder == null ? "" : rentalOrder.getPeriodText(),
      rentalOrder == null ? null : rentalOrder.getDueAt(),
      report.getCreatedAt(),
      report.getReviewedAt()
    );
  }

  private String rentalStatusText(String status) {
    return switch (status) {
      case "WAIT_SHIP" -> "等待发货";
      case "WAIT_RECEIVE" -> "待确认收货";
      case "ACTIVE" -> "租赁中";
      case "RETURNING" -> "待卖家确认归还";
      case "DAMAGE_PENDING" -> "待鉴定";
      case "RETURNABLE" -> "可归还";
      case "COMPENSATION_REQUIRED" -> "已鉴定";
      case "RETURNED" -> "已归还";
      default -> status;
    };
  }

  private String damageStatusText(String status) {
    return switch (status) {
      case "PENDING" -> "pending";
      case "REVIEWED" -> "reviewed";
      default -> status;
    };
  }

  private String normalizeDamageStatus(String status) {
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

  private void releaseDeposit(RentalOrder rentalOrder, UserAccount borrower, UserAccount owner, BigDecimal refundAmount, BigDecimal compensationAmount) {
    BigDecimal deposit = normalizeBalance(rentalOrder.getDepositAmount());
    SystemWallet wallet = getOrCreateWallet();
    if (normalizeBalance(wallet.getEscrowBalance()).compareTo(deposit) < 0) {
      throw new BizException("系统托管押金不足");
    }

    wallet.setEscrowBalance(normalizeBalance(wallet.getEscrowBalance()).subtract(deposit));
    wallet.setUpdatedAt(LocalDateTime.now());
    systemWalletMapper.updateById(wallet);

    LocalDateTime now = LocalDateTime.now();
    if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
      BigDecimal after = normalizeBalance(borrower.getWalletBalance()).add(refundAmount);
      borrower.setWalletBalance(after);
      borrower.setUpdatedAt(now);
      userAccountMapper.updateById(borrower);

      WalletTransaction tx = new WalletTransaction();
      tx.setUserId(borrower.getId());
      tx.setType("INCOME");
      tx.setAmount(refundAmount);
      tx.setBalanceAfter(after);
      tx.setCategory("RENTAL_DEPOSIT_REFUND");
      tx.setTitle("租赁押金退还");
      tx.setReferenceId("rental_" + rentalOrder.getId());
      tx.setCreatedAt(now);
      walletTransactionMapper.insert(tx);
    }
    if (compensationAmount.compareTo(BigDecimal.ZERO) > 0) {
      BigDecimal after = normalizeBalance(owner.getWalletBalance()).add(compensationAmount);
      owner.setWalletBalance(after);
      owner.setUpdatedAt(now);
      userAccountMapper.updateById(owner);

      WalletTransaction tx = new WalletTransaction();
      tx.setUserId(owner.getId());
      tx.setType("INCOME");
      tx.setAmount(compensationAmount);
      tx.setBalanceAfter(after);
      tx.setCategory("RENTAL_COMPENSATION");
      tx.setTitle("租赁损坏赔偿");
      tx.setReferenceId("rental_" + rentalOrder.getId());
      tx.setCreatedAt(now);
      walletTransactionMapper.insert(tx);
    }
  }

  private BigDecimal calculateOverduePenalty(RentalOrder rentalOrder, TradeOrder tradeOrder, LocalDateTime now) {
    if (rentalOrder.getDueAt() == null || !now.isAfter(rentalOrder.getDueAt())) {
      return BigDecimal.ZERO;
    }
    long seconds = Duration.between(rentalOrder.getDueAt(), now).getSeconds();
    if (seconds <= 0) {
      return BigDecimal.ZERO;
    }
    int overdueDays = (int) Math.ceil(seconds / 86400.0);
    BigDecimal dailyRent = normalizeBalance(tradeOrder.getUnitPrice());
    BigDecimal penaltyPerDay = dailyRent.multiply(new BigDecimal("2")).divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
    return penaltyPerDay.multiply(BigDecimal.valueOf(overdueDays));
  }

  private void refundDeposit(RentalOrder rentalOrder, UserAccount borrower, BigDecimal deposit) {
    SystemWallet wallet = getOrCreateWallet();
    if (normalizeBalance(wallet.getEscrowBalance()).compareTo(deposit) < 0) {
      throw new BizException("系统托管押金不足");
    }

    wallet.setEscrowBalance(normalizeBalance(wallet.getEscrowBalance()).subtract(deposit));
    wallet.setUpdatedAt(LocalDateTime.now());
    systemWalletMapper.updateById(wallet);

    if (deposit.compareTo(BigDecimal.ZERO) > 0) {
      BigDecimal after = normalizeBalance(borrower.getWalletBalance()).add(deposit);
      borrower.setWalletBalance(after);
      borrower.setUpdatedAt(LocalDateTime.now());
      userAccountMapper.updateById(borrower);

      WalletTransaction tx = new WalletTransaction();
      tx.setUserId(borrower.getId());
      tx.setType("INCOME");
      tx.setAmount(deposit);
      tx.setBalanceAfter(after);
      tx.setCategory("RENTAL_DEPOSIT_REFUND");
      tx.setTitle("退租押金退还");
      tx.setReferenceId("rental_" + rentalOrder.getId());
      tx.setCreatedAt(LocalDateTime.now());
      walletTransactionMapper.insert(tx);
    }
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

  private BigDecimal normalizeBalance(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  private String normalizeDeliveryMethod(String deliveryMethod) {
    if ("station".equalsIgnoreCase(deliveryMethod)) {
      return "station";
    }
    if ("pickup".equalsIgnoreCase(deliveryMethod)) {
      return "pickup";
    }
    throw new BizException("请选择正确的还书方式");
  }

  private BigDecimal resolvePayoutAmount(TradeOrder order) {
    if (!"rental".equals(order.getOrderType())) {
      return normalizeBalance(order.getUnitPrice()).multiply(BigDecimal.valueOf(order.getQuantity() == null ? 1 : order.getQuantity()));
    }
    return normalizeBalance(order.getUnitPrice()).multiply(BigDecimal.valueOf(rentalDays(order.getRentalPeriod())));
  }

  private int rentalDays(String rentalPeriod) {
    if (rentalPeriod == null || rentalPeriod.isBlank()) {
      return 7;
    }
    String digits = rentalPeriod.replaceAll("\\D+", "");
    if (digits.isBlank()) {
      return 7;
    }
    return Math.max(1, Integer.parseInt(digits));
  }
}
