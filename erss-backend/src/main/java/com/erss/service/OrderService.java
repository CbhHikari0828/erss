package com.erss.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.erss.common.BizException;
import com.erss.domain.entity.Book;
import com.erss.domain.entity.Notification;
import com.erss.domain.entity.PayoutRequest;
import com.erss.domain.entity.RentalOrder;
import com.erss.domain.entity.SystemTransaction;
import com.erss.domain.entity.SystemWallet;
import com.erss.domain.entity.TradeOrder;
import com.erss.domain.entity.UserAccount;
import com.erss.domain.entity.WalletTransaction;
import com.erss.dto.CreateOrderRequest;
import com.erss.mapper.BookMapper;
import com.erss.mapper.NotificationMapper;
import com.erss.mapper.PayoutRequestMapper;
import com.erss.mapper.RentalOrderMapper;
import com.erss.mapper.SystemTransactionMapper;
import com.erss.mapper.SystemWalletMapper;
import com.erss.mapper.TradeOrderMapper;
import com.erss.mapper.UserAccountMapper;
import com.erss.mapper.WalletTransactionMapper;
import com.erss.vo.OrderVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
  private static final DateTimeFormatter ORDER_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

  private final TradeOrderMapper tradeOrderMapper;
  private final RentalOrderMapper rentalOrderMapper;
  private final BookMapper bookMapper;
  private final UserAccountMapper userAccountMapper;
  private final NotificationMapper notificationMapper;
  private final PayoutRequestMapper payoutRequestMapper;
  private final SystemWalletMapper systemWalletMapper;
  private final WalletTransactionMapper walletTransactionMapper;
  private final SystemTransactionMapper systemTransactionMapper;

  public OrderService(
    TradeOrderMapper tradeOrderMapper,
    RentalOrderMapper rentalOrderMapper,
    BookMapper bookMapper,
    UserAccountMapper userAccountMapper,
    NotificationMapper notificationMapper,
    PayoutRequestMapper payoutRequestMapper,
    SystemWalletMapper systemWalletMapper,
    WalletTransactionMapper walletTransactionMapper,
    SystemTransactionMapper systemTransactionMapper
  ) {
    this.tradeOrderMapper = tradeOrderMapper;
    this.rentalOrderMapper = rentalOrderMapper;
    this.bookMapper = bookMapper;
    this.userAccountMapper = userAccountMapper;
    this.notificationMapper = notificationMapper;
    this.payoutRequestMapper = payoutRequestMapper;
    this.systemWalletMapper = systemWalletMapper;
    this.walletTransactionMapper = walletTransactionMapper;
    this.systemTransactionMapper = systemTransactionMapper;
  }

  @Transactional
  public Long createOrder(CreateOrderRequest request) {
    Long buyerId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    Book book = bookMapper.selectById(request.bookId());
    if (book == null) {
      throw new BizException("书籍不存在");
    }
    if (book.getSellerId().equals(buyerId)) {
      throw new BizException("不能购买自己发布的书籍");
    }

    String orderType = normalizeOrderType(request.orderType());
    boolean rental = "rental".equals(orderType);
    if (rental && !Boolean.TRUE.equals(book.getRentalEnabled())) {
      throw new BizException("该书籍未开启租赁");
    }
    String nextBookStatus = rental ? "RENTED" : "SOLD";
    int changed = bookMapper.update(null, Wrappers.<Book>lambdaUpdate()
      .eq(Book::getId, request.bookId())
      .eq(Book::getStatus, "ON_SALE")
      .set(Book::getStatus, nextBookStatus)
      .set(Book::getUpdatedAt, LocalDateTime.now()));
    if (changed == 0) {
      throw new BizException("该书籍已售或已租出");
    }

    LocalDateTime now = LocalDateTime.now();
    String deliveryMethod = normalizeDeliveryMethod(request.deliveryMethod());
    boolean pickup = "pickup".equals(deliveryMethod);
    String rentalPeriod = rental ? normalizeRentalPeriod(request.rentalPeriod()) : "";
    BigDecimal deliveryFee = "station".equals(deliveryMethod) ? new BigDecimal("3.00") : BigDecimal.ZERO;
    BigDecimal unitPrice = rental ? normalizeBalance(book.getRentPrice()) : normalizeBalance(book.getPrice());
    BigDecimal deposit = rental ? normalizeBalance(book.getRentalDeposit()) : BigDecimal.ZERO;
    if (rental && (unitPrice.compareTo(BigDecimal.ZERO) <= 0 || deposit.compareTo(BigDecimal.ZERO) <= 0)) {
      throw new BizException("该书籍租赁价格配置不完整");
    }
    int rentalDays = rental ? rentalDays(rentalPeriod) : 0;
    BigDecimal total = rental
      ? unitPrice.multiply(BigDecimal.valueOf(rentalDays)).add(deposit).add(deliveryFee)
      : unitPrice.add(deliveryFee);

    String normalizedPaymentMethod = normalizePaymentMethod(request.paymentMethod());
    BigDecimal walletBalanceAfter = null;
    if ("wallet".equals(normalizedPaymentMethod)) {
      UserAccount buyer = userAccountMapper.selectById(buyerId);
      if (buyer == null) {
        throw new BizException("购买人不存在");
      }
      BigDecimal balance = buyer.getWalletBalance() == null ? BigDecimal.ZERO : buyer.getWalletBalance();
      if (balance.compareTo(total) < 0) {
        throw new BizException("系统钱包余额不足，请先充值");
      }
      buyer.setWalletBalance(balance.subtract(total));
      buyer.setUpdatedAt(now);
      userAccountMapper.updateById(buyer);
      walletBalanceAfter = balance.subtract(total);
    }

    TradeOrder order = new TradeOrder();
    order.setOrderNo(createOrderNo());
    order.setOrderType(orderType);
    order.setBookId(book.getId());
    order.setSellerId(book.getSellerId());
    order.setBuyerId(buyerId);
    order.setUnitPrice(unitPrice);
    order.setQuantity(1);
    order.setRentalPeriod(rentalPeriod);
    order.setDepositAmount(deposit);
    order.setDeliveryFee(deliveryFee);
    order.setTotalAmount(total);
    order.setDeliveryMethod(deliveryMethod);
    order.setPaymentMethod(normalizedPaymentMethod);
    order.setStatus(pickup ? "WAIT_RECEIVE" : "WAIT_SELLER_SHIP");
    order.setEscrowStatus("ESCROW_HOLDING");
    order.setAutoConfirmAt(pickup ? now.plusDays(15) : null);
    order.setCreatedAt(now);
    order.setUpdatedAt(now);
    tradeOrderMapper.insert(order);

    if (walletBalanceAfter != null) {
      WalletTransaction tx = new WalletTransaction();
      tx.setUserId(buyerId);
      tx.setType("EXPENSE");
      tx.setAmount(total);
      tx.setBalanceAfter(walletBalanceAfter);
      tx.setCategory("ORDER_PAY");
      tx.setTitle("购买《" + (book.getTitle() != null ? book.getTitle() : "书籍") + "》");
      tx.setReferenceId("order_" + order.getId());
      tx.setCreatedAt(now);
      walletTransactionMapper.insert(tx);
    }

    SystemWallet wallet = getOrCreateWallet();
    wallet.setEscrowBalance(normalizeBalance(wallet.getEscrowBalance()).add(total));
    wallet.setUpdatedAt(now);
    systemWalletMapper.updateById(wallet);

    if (deliveryFee.compareTo(BigDecimal.ZERO) > 0) {
      SystemTransaction stx = new SystemTransaction();
      stx.setCategory("STATION_DELIVERY_FEE");
      stx.setAmount(deliveryFee);
      stx.setDescription("驿站配送费 订单#" + order.getId());
      stx.setReferenceId("order_" + order.getId());
      stx.setCreatedAt(now);
      systemTransactionMapper.insert(stx);
    }

    createOrUpdatePayoutRequest(order, "ESCROW_HOLDING", now);

    if (rental) {
      RentalOrder rentalOrder = new RentalOrder();
      rentalOrder.setTradeOrderId(order.getId());
      rentalOrder.setBookId(book.getId());
      rentalOrder.setOwnerId(book.getSellerId());
      rentalOrder.setBorrowerId(buyerId);
      rentalOrder.setPeriodText(rentalPeriod);
      rentalOrder.setDepositAmount(deposit);
      rentalOrder.setStatus(pickup ? "ACTIVE" : "WAIT_SHIP");
      rentalOrder.setDueAt(now.plusDays(rentalDays));
      rentalOrder.setCreatedAt(now);
      rentalOrder.setUpdatedAt(now);
      rentalOrderMapper.insert(rentalOrder);
    }

    String sellerSummary = pickup
      ? paymentNotice(order.getPaymentMethod(), total) + "，买家选择自提，等待买家确认收货。"
      : paymentNotice(order.getPaymentMethod(), total) + "，等待你发货。";
    createNotification(book.getSellerId(), "trade", "有新的书籍订单", sellerSummary, pickup ? "待收货" : "待发货", "查看订单", "/my-shop");
    return order.getId();
  }

  public List<OrderVO> listBuyerOrders() {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    return tradeOrderMapper.selectList(Wrappers.<TradeOrder>lambdaQuery()
        .eq(TradeOrder::getBuyerId, userId)
        .orderByDesc(TradeOrder::getCreatedAt))
      .stream()
      .map(this::toOrderVO)
      .toList();
  }

  public List<OrderVO> listSellerOrders() {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    return tradeOrderMapper.selectList(Wrappers.<TradeOrder>lambdaQuery()
        .eq(TradeOrder::getSellerId, userId)
        .orderByDesc(TradeOrder::getCreatedAt))
      .stream()
      .map(this::toOrderVO)
      .toList();
  }

  @Transactional
  public void shipOrder(Long orderId) {
    Long sellerId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    TradeOrder order = tradeOrderMapper.selectById(orderId);
    if (order == null || !order.getSellerId().equals(sellerId)) {
      throw new BizException("订单不存在");
    }
    if (!"WAIT_SELLER_SHIP".equals(order.getStatus())) {
      throw new BizException("当前订单状态不能发货");
    }
    LocalDateTime now = LocalDateTime.now();
    order.setStatus("WAIT_RECEIVE");
    order.setEscrowStatus("ESCROW_HOLDING");
    order.setSellerShippedAt(now);
    order.setAutoConfirmAt(now.plusDays(15));
    order.setUpdatedAt(now);
    tradeOrderMapper.updateById(order);

    RentalOrder rentalOrder = rentalOrderMapper.selectOne(
      Wrappers.<RentalOrder>lambdaQuery().eq(RentalOrder::getTradeOrderId, orderId).last("limit 1"));
    if (rentalOrder != null && "WAIT_SHIP".equals(rentalOrder.getStatus())) {
      rentalOrder.setStatus("WAIT_RECEIVE");
      rentalOrder.setUpdatedAt(now);
      rentalOrderMapper.updateById(rentalOrder);
    }

    createNotification(order.getBuyerId(), "trade", "卖家已发货", "请在收到书籍后确认收货。", "待确认", "查看订单", "/orders");
  }

  @Transactional
  public void confirmReceipt(Long orderId) {
    Long buyerId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    TradeOrder order = tradeOrderMapper.selectById(orderId);
    if (order == null || !order.getBuyerId().equals(buyerId)) {
      throw new BizException("订单不存在");
    }
    if (!"WAIT_RECEIVE".equals(order.getStatus())) {
      throw new BizException("当前订单状态不能确认收货");
    }
    if ("rental".equals(order.getOrderType())) {
      throw new BizException("租赁订单请在我的租借中确认收货");
    }
    order.setStatus("PAYOUT_PENDING");
    order.setEscrowStatus("ESCROW_RELEASE_PENDING");
    order.setUpdatedAt(LocalDateTime.now());
    tradeOrderMapper.updateById(order);

    createOrUpdatePayoutRequest(order, "PENDING", LocalDateTime.now());

    createNotification(order.getSellerId(), "trade", "待发放资金", "买家已确认收货，订单金额已进入待发放状态，等待管理员处理。", "待发放", "查看资金", "/my-shop");
    createNotification(order.getBuyerId(), "trade", "确认收货成功", "你的确认已提交，系统将进入卖家资金发放流程。", "待发放", "查看订单", "/orders");
    notifyAdmins("新的待发放资金", "订单「" + order.getOrderNo() + "」已进入待发放状态，请前往资金管理处理。");
  }

  private OrderVO toOrderVO(TradeOrder order) {
    Book book = bookMapper.selectById(order.getBookId());
    UserAccount seller = userAccountMapper.selectById(order.getSellerId());
    UserAccount buyer = userAccountMapper.selectById(order.getBuyerId());
    return new OrderVO(
      order.getId(),
      order.getOrderNo(),
      order.getOrderType(),
      order.getBookId(),
      book == null ? "" : book.getTitle(),
      seller == null ? "" : seller.getUsername(),
      buyer == null ? "" : buyer.getUsername(),
      order.getUnitPrice(),
      order.getQuantity(),
      order.getRentalPeriod(),
      order.getDepositAmount(),
      order.getDeliveryFee(),
      order.getTotalAmount(),
      order.getDeliveryMethod(),
      order.getPaymentMethod(),
      statusText(order.getStatus()),
      escrowText(order.getEscrowStatus()),
      order.getAutoConfirmAt(),
      order.getCreatedAt()
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

  private String createOrderNo() {
    return "ERSS" + LocalDateTime.now().format(ORDER_NO_TIME) + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
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

  private String statusText(String status) {
    return switch (status) {
      case "WAIT_SELLER_SHIP" -> "待卖家发货";
      case "WAIT_PAYMENT" -> "待支付";
      case "WAIT_RECEIVE" -> "待确认收货";
      case "PAYOUT_PENDING" -> "待发放";
      case "COMPLETED" -> "已完成";
      default -> status;
    };
  }

  private String escrowText(String status) {
    return switch (status) {
      case "ESCROW_PENDING" -> "系统暂存中";
      case "ESCROW_HOLDING" -> "系统托管中";
      case "ESCROW_RELEASE_PENDING" -> "待管理员发放";
      case "ESCROW_RELEASED" -> "买家确认收货后打款给卖家";
      default -> status;
    };
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

  private void notifyAdmins(String title, String summary) {
    List<UserAccount> admins = userAccountMapper.selectList(Wrappers.<UserAccount>lambdaQuery()
      .eq(UserAccount::getRole, "admin"));
    for (UserAccount admin : admins) {
      createNotification(admin.getId(), "system", title, summary, "待处理", "进入资金管理", "/finance");
    }
  }

  private PayoutRequest createOrUpdatePayoutRequest(TradeOrder order, String status, LocalDateTime now) {
    PayoutRequest payoutRequest = payoutRequestMapper.selectOne(Wrappers.<PayoutRequest>lambdaQuery()
      .eq(PayoutRequest::getOrderId, order.getId())
      .last("limit 1"));

    if (payoutRequest == null) {
      payoutRequest = new PayoutRequest();
      payoutRequest.setOrderId(order.getId());
      payoutRequest.setSellerId(order.getSellerId());
      payoutRequest.setAmount(resolvePayoutAmount(order));
      payoutRequest.setStatus(status);
      payoutRequest.setRequestedAt(now);
      payoutRequestMapper.insert(payoutRequest);
      return payoutRequest;
    }

    payoutRequest.setSellerId(order.getSellerId());
    payoutRequest.setAmount(resolvePayoutAmount(order));
    payoutRequest.setStatus(status);
    if ("PENDING".equals(status)) {
      payoutRequest.setRequestedAt(now);
      payoutRequest.setReleasedAt(null);
      payoutRequest.setReleasedBy(null);
      payoutRequest.setReleaseNote("");
    }
    payoutRequestMapper.updateById(payoutRequest);
    return payoutRequest;
  }

  private String normalizePaymentMethod(String paymentMethod) {
    if (paymentMethod == null) {
      return "";
    }
    return paymentMethod.toLowerCase();
  }

  private String paymentNotice(String paymentMethod, BigDecimal total) {
    return switch (paymentMethod) {
      case "wechat" -> "微信模拟支付成功，金额 " + total.toPlainString() + " 元已进入系统托管";
      case "alipay" -> "支付宝模拟支付成功，金额 " + total.toPlainString() + " 元已进入系统托管";
      case "campus" -> "校园卡支付成功，金额 " + total.toPlainString() + " 元已进入系统托管";
      case "wallet" -> "系统钱包支付成功，金额 " + total.toPlainString() + " 元已从余额扣除并进入托管";
      default -> "支付成功，金额 " + total.toPlainString() + " 元已进入系统托管";
    };
  }

  private BigDecimal resolvePayoutAmount(TradeOrder order) {
    if (!"rental".equals(order.getOrderType())) {
      return normalizeBalance(order.getUnitPrice()).multiply(BigDecimal.valueOf(order.getQuantity() == null ? 1 : order.getQuantity()));
    }

    return normalizeBalance(order.getUnitPrice()).multiply(BigDecimal.valueOf(rentalDays(order.getRentalPeriod())));
  }

  private BigDecimal normalizeBalance(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  private String normalizeOrderType(String orderType) {
    if ("rental".equalsIgnoreCase(orderType)) {
      return "rental";
    }
    if ("purchase".equalsIgnoreCase(orderType)) {
      return "purchase";
    }
    throw new BizException("请选择正确的订单类型");
  }

  private String normalizeDeliveryMethod(String deliveryMethod) {
    if ("station".equalsIgnoreCase(deliveryMethod)) {
      return "station";
    }
    if ("pickup".equalsIgnoreCase(deliveryMethod)) {
      return "pickup";
    }
    throw new BizException("请选择正确的收货方式");
  }

  private String normalizeRentalPeriod(String rentalPeriod) {
    if (rentalPeriod == null || rentalPeriod.isBlank()) {
      return "7天";
    }
    return rentalPeriod.trim();
  }
}
