/*
 Navicat Premium Dump SQL

 Source Server         : 103.212.49.129
 Source Server Type    : MySQL
 Source Server Version : 80027 (8.0.27)
 Source Host           : 103.212.49.129:3306
 Source Schema         : erss

 Target Server Type    : MySQL
 Target Server Version : 80027 (8.0.27)
 File Encoding         : 65001

 Date: 09/05/2026 16:35:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for book_images
-- ----------------------------
DROP TABLE IF EXISTS `book_images`;
CREATE TABLE `book_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint NOT NULL,
  `image_url` varchar(512) NOT NULL,
  `sort_no` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_book_images_book_id` (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of book_images
-- ----------------------------
BEGIN;
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (1, 1, 'https://images.pexels.com/photos/3782249/pexels-photo-3782249.jpeg?cs=srgb&dl=pexels-polina-zimmerman-3782249.jpg&fm=jpg', 0);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (2, 1, 'https://images.pexels.com/photos/4219041/pexels-photo-4219041.jpeg?cs=srgb&dl=pexels-karola-g-4219041.jpg&fm=jpg', 1);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (3, 2, 'https://images.pexels.com/photos/4219041/pexels-photo-4219041.jpeg?cs=srgb&dl=pexels-karola-g-4219041.jpg&fm=jpg', 0);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (4, 3, 'https://images.pexels.com/photos/27668991/pexels-photo-27668991.jpeg?cs=srgb&dl=pexels-fiona-murray-537687299-27668991.jpg&fm=jpg', 0);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (5, 4, 'https://images.pexels.com/photos/5009227/pexels-photo-5009227.jpeg?auto=compress&cs=tinysrgb&w=800', 0);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (6, 5, 'https://images.pexels.com/photos/1181671/pexels-photo-1181671.jpeg?auto=compress&cs=tinysrgb&w=800', 0);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (7, 6, 'http://localhost:9000/erss/images/books/2026/05/09/2fc837c4-6652-4f11-abb4-6860981cd9e9.webp', 0);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (8, 7, 'http://localhost:9000/erss/images/books/2026/05/09/e0017ce7-7c6b-42cd-a7f9-5be617040b11.png', 0);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (9, 8, 'http://localhost:9000/erss/images/books/2026/05/09/a730cd7d-a117-4f05-a0d3-3a1f6b2c4a86.jpg', 0);
COMMIT;

-- ----------------------------
-- Table structure for books
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seller_id` bigint NOT NULL,
  `title` varchar(160) NOT NULL,
  `category` varchar(64) NOT NULL,
  `subcategory` varchar(64) DEFAULT '',
  `price` decimal(10,2) NOT NULL,
  `original_price` decimal(10,2) DEFAULT NULL,
  `rental_enabled` tinyint(1) NOT NULL DEFAULT '0',
  `rent_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `rental_deposit` decimal(10,2) NOT NULL DEFAULT '0.00',
  `condition_text` varchar(64) NOT NULL,
  `campus` varchar(64) NOT NULL,
  `cover_url` varchar(512) DEFAULT '',
  `summary` varchar(512) DEFAULT '',
  `description` text,
  `tags` varchar(512) DEFAULT '',
  `status` varchar(32) NOT NULL DEFAULT 'ON_SALE',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_books_status_created` (`status`,`created_at`),
  KEY `idx_books_category` (`category`,`subcategory`),
  KEY `idx_books_seller_id` (`seller_id`),
  FULLTEXT KEY `ft_books_search` (`title`,`category`,`subcategory`,`summary`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of books
-- ----------------------------
BEGIN;
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (1, 1, '高等数学 同济第七版 上册', '教材教辅', '高等数学', 18.00, 49.80, 0, 0.00, 0.00, '九成新', '主校区', 'https://images.pexels.com/photos/3782249/pexels-photo-3782249.jpeg?cs=srgb&dl=pexels-polina-zimmerman-3782249.jpg&fm=jpg', '课后习题页完整，无缺页，封面有轻微折痕。', '大一时使用的教材，重点章节有铅笔标注，已擦除大部分。适合本校理工类专业基础课复习。', '可小刀,自提优先,附笔记', 'ON_SALE', '2026-05-08 15:24:14', '2026-05-08 15:24:14');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (2, 1, '张剑黄皮书 2026 考研英语真题', '考研考公', '考研英语', 26.00, 79.00, 0, 0.00, 0.00, '全新未写', '研究生院', 'https://images.pexels.com/photos/4219041/pexels-photo-4219041.jpeg?cs=srgb&dl=pexels-karola-g-4219041.jpg&fm=jpg', '买重了，塑封还在。', '适合 2027 届考研同学提前备考，书脊无压痕，支持主校区地铁口面交。', '价格稳,考研季,可邮寄', 'ON_SALE', '2026-05-08 15:24:14', '2026-05-08 15:24:14');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (3, 1, '大学英语四级词汇闪过', '语言证书', '四六级', 12.00, 39.80, 0, 0.00, 0.00, '八成新', '南校区', 'https://images.pexels.com/photos/27668991/pexels-photo-27668991.jpeg?cs=srgb&dl=pexels-fiona-murray-537687299-27668991.jpg&fm=jpg', '有重点划线，背词效率很高。', '书内有一部分荧光笔划线，适合临时冲刺备考，不影响继续使用。', '笔记友好,四六级,当天可拿', 'SOLD', '2026-05-08 15:24:14', '2026-05-09 16:02:53');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (4, 1, '传播学教程 第三版', '教材教辅', '大学英语', 22.00, 56.00, 0, 0.00, 0.00, '九成新', '主校区', 'https://images.pexels.com/photos/5009227/pexels-photo-5009227.jpeg?auto=compress&cs=tinysrgb&w=800', '老师指定教材，期末复习非常够用。', '内页很干净，仅目录页有课程安排记录，适合新闻传播类同学接手。', '专业课,本学期热门', 'SOLD', '2026-05-08 15:24:14', '2026-05-09 13:22:32');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (5, 1, 'Linux 系统与网络服务', '教材教辅', '计算机基础', 20.00, 49.00, 1, 4.00, 30.00, '九成新', '主校区', 'https://images.pexels.com/photos/1181671/pexels-photo-1181671.jpeg?auto=compress&cs=tinysrgb&w=800', '作为租借样例保留，便于管理员审核报损流程。', '这本书已用于一次租借流程样例，当前状态会配合管理员工作台展示。', '租借样例,管理员审核', 'RENTED', '2026-05-08 15:24:14', '2026-05-09 03:06:08');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (6, 3, '测试书籍', '教材教辅', '', 1.00, 11.00, 0, 0.00, 0.00, '全新未写', '主校区', 'http://localhost:9000/erss/images/books/2026/05/09/2fc837c4-6652-4f11-abb4-6860981cd9e9.webp', '1', '1', '', 'SOLD', '2026-05-09 11:42:16', '2026-05-09 11:43:30');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (7, 3, 'redBook', '教材教辅', '', 12.00, 111.00, 1, 4.00, 30.00, '全新未写', '主校区', 'http://localhost:9000/erss/images/books/2026/05/09/e0017ce7-7c6b-42cd-a7f9-5be617040b11.png', '', '', '', 'ON_SALE', '2026-05-09 16:21:46', '2026-05-09 16:21:46');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (8, 3, 'Qs', '教材教辅', '', 11.00, 23.00, 0, 0.00, 0.00, '全新未写', '主校区', 'http://localhost:9000/erss/images/books/2026/05/09/a730cd7d-a117-4f05-a0d3-3a1f6b2c4a86.jpg', '', '', '', 'SOLD', '2026-05-09 16:25:48', '2026-05-09 16:27:57');
COMMIT;

-- ----------------------------
-- Table structure for damage_reports
-- ----------------------------
DROP TABLE IF EXISTS `damage_reports`;
CREATE TABLE `damage_reports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rental_order_id` bigint NOT NULL,
  `reporter_id` bigint NOT NULL,
  `description` varchar(1024) NOT NULL,
  `images` text,
  `status` varchar(32) NOT NULL DEFAULT 'PENDING',
  `review_note` varchar(1024) DEFAULT '',
  `compensation_amount` decimal(10,2) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reviewed_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_damage_reports_rental_order` (`rental_order_id`),
  KEY `idx_damage_reports_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of damage_reports
-- ----------------------------
BEGIN;
INSERT INTO `damage_reports` (`id`, `rental_order_id`, `reporter_id`, `description`, `images`, `status`, `review_note`, `compensation_amount`, `created_at`, `reviewed_at`) VALUES (1, 1, 2, '封面右下角有轻微磨损，书页边缘有一处水渍，申请管理员判定是否需要赔付。', 'https://images.pexels.com/photos/1181671/pexels-photo-1181671.jpeg?auto=compress&cs=tinysrgb&w=800,https://images.pexels.com/photos/159844/book-book-pages-read-literature-159844.jpeg?auto=compress&cs=tinysrgb&w=800', 'REVIEWED', '11', 11.00, '2026-05-08 21:45:00', '2026-05-09 11:22:34');
COMMIT;

-- ----------------------------
-- Table structure for notifications
-- ----------------------------
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `category` varchar(32) NOT NULL,
  `title` varchar(160) NOT NULL,
  `summary` varchar(512) NOT NULL,
  `status_text` varchar(64) DEFAULT '',
  `action_text` varchar(64) DEFAULT '',
  `route` varchar(128) DEFAULT '',
  `unread` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_notifications_user` (`user_id`,`created_at`),
  KEY `idx_notifications_category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of notifications
-- ----------------------------
BEGIN;
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (3, 2, 'rental', '报损审核结果', '报损已审核，押金中 11 元赔付给书主，19.00 元退回租客钱包。', '通过并赔付', '查看详情', '/message', 1, '2026-05-09 11:22:34');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (5, 2, 'wallet', '押金已退回', '租赁押金退回 19.00 元，已记入你的钱包余额。', '已退回', '查看钱包', '/wallet', 1, '2026-05-09 11:22:34');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (11, 3, 'trade', '确认收货成功', '你的确认已提交，系统将进入卖家资金发放流程。', '待发放', '查看订单', '/orders', 0, '2026-05-09 15:22:19');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (12, 4, 'system', '新的待发放资金', '订单「ERSS2026050913223161042B98D」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 15:22:19');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (19, 4, 'system', '新的待发放资金', '订单「ERSS202605091143297838AD33A」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 16:07:18');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (22, 4, 'system', '新的待发放资金', '订单「ERSS20260509160253279B026C5」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 16:10:04');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (27, 3, 'trade', '有新的书籍订单', '微信模拟支付成功，金额 11.00 元已进入系统托管，买家选择自提，等待买家确认收货。', '待收货', '查看订单', '/my-shop', 0, '2026-05-09 16:28:00');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (28, 3, 'trade', '待发放资金', '买家已确认收货，订单金额已进入待发放状态，等待管理员处理。', '待发放', '查看资金', '/my-shop', 1, '2026-05-09 16:33:30');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (29, 1, 'trade', '确认收货成功', '你的确认已提交，系统将进入卖家资金发放流程。', '待发放', '查看订单', '/orders', 1, '2026-05-09 16:33:30');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (30, 4, 'system', '新的待发放资金', '订单「ERSS20260509162758239BB59ED」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 16:33:31');
COMMIT;

-- ----------------------------
-- Table structure for payout_requests
-- ----------------------------
DROP TABLE IF EXISTS `payout_requests`;
CREATE TABLE `payout_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'PENDING',
  `requested_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `released_at` datetime DEFAULT NULL,
  `released_by` bigint DEFAULT NULL,
  `release_note` varchar(255) DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payout_requests_order_id` (`order_id`),
  KEY `idx_payout_requests_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of payout_requests
-- ----------------------------
BEGIN;
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (1, 3, 1, 22.00, 'PAID', '2026-05-09 15:22:19', '2026-05-09 15:22:53', 4, '管理员发放');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (2, 4, 1, 12.00, 'PAID', '2026-05-09 16:10:04', '2026-05-09 16:11:09', 4, '管理员发放');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (3, 2, 3, 1.00, 'PAID', '2026-05-09 16:07:15', '2026-05-09 16:10:49', 4, '管理员发放');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (4, 5, 3, 11.00, 'PENDING', '2026-05-09 16:33:29', NULL, NULL, '');
COMMIT;

-- ----------------------------
-- Table structure for rental_orders
-- ----------------------------
DROP TABLE IF EXISTS `rental_orders`;
CREATE TABLE `rental_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `trade_order_id` bigint NOT NULL,
  `book_id` bigint NOT NULL,
  `owner_id` bigint NOT NULL,
  `borrower_id` bigint NOT NULL,
  `period_text` varchar(32) NOT NULL,
  `deposit_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `status` varchar(32) NOT NULL,
  `due_at` datetime NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_rental_orders_owner` (`owner_id`,`created_at`),
  KEY `idx_rental_orders_borrower` (`borrower_id`,`created_at`),
  KEY `idx_rental_orders_book_id` (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of rental_orders
-- ----------------------------
BEGIN;
INSERT INTO `rental_orders` (`id`, `trade_order_id`, `book_id`, `owner_id`, `borrower_id`, `period_text`, `deposit_amount`, `status`, `due_at`, `created_at`, `updated_at`) VALUES (1, 1, 5, 1, 2, '7天', 30.00, 'COMPENSATION_REQUIRED', '2026-05-15 21:30:00', '2026-05-08 21:30:00', '2026-05-09 11:22:34');
COMMIT;

-- ----------------------------
-- Table structure for shops
-- ----------------------------
DROP TABLE IF EXISTS `shops`;
CREATE TABLE `shops` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `shop_name` varchar(128) NOT NULL,
  `shop_campus` varchar(64) NOT NULL,
  `shop_intro` varchar(512) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shops_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of shops
-- ----------------------------
BEGIN;
INSERT INTO `shops` (`id`, `user_id`, `shop_name`, `shop_campus`, `shop_intro`, `created_at`, `updated_at`) VALUES (1, 1, '主校区二手教材小店', '主校区', '主营教材教辅、考研资料和语言证书用书，支持租借与面交。', '2026-05-08 15:24:14', '2026-05-08 15:24:14');
INSERT INTO `shops` (`id`, `user_id`, `shop_name`, `shop_campus`, `shop_intro`, `created_at`, `updated_at`) VALUES (2, 3, 'ZK', '研究生院', 'ZK1', '2026-05-09 11:28:07', '2026-05-09 11:28:07');
COMMIT;

-- ----------------------------
-- Table structure for system_wallet
-- ----------------------------
DROP TABLE IF EXISTS `system_wallet`;
CREATE TABLE `system_wallet` (
  `id` bigint NOT NULL,
  `escrow_balance` decimal(12,2) NOT NULL DEFAULT '0.00',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of system_wallet
-- ----------------------------
BEGIN;
INSERT INTO `system_wallet` (`id`, `escrow_balance`, `created_at`, `updated_at`) VALUES (1, 9983.00, '2026-05-08 16:20:39', '2026-05-09 16:27:58');
COMMIT;

-- ----------------------------
-- Table structure for trade_orders
-- ----------------------------
DROP TABLE IF EXISTS `trade_orders`;
CREATE TABLE `trade_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) NOT NULL,
  `order_type` varchar(32) NOT NULL,
  `book_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  `buyer_id` bigint NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `rental_period` varchar(32) DEFAULT '',
  `deposit_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `delivery_fee` decimal(10,2) NOT NULL DEFAULT '0.00',
  `total_amount` decimal(10,2) NOT NULL,
  `delivery_method` varchar(32) NOT NULL,
  `payment_method` varchar(32) NOT NULL,
  `status` varchar(32) NOT NULL,
  `escrow_status` varchar(32) NOT NULL,
  `seller_shipped_at` datetime DEFAULT NULL,
  `auto_confirm_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_orders_order_no` (`order_no`),
  KEY `idx_trade_orders_buyer` (`buyer_id`,`created_at`),
  KEY `idx_trade_orders_seller` (`seller_id`,`created_at`),
  KEY `idx_trade_orders_book_id` (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of trade_orders
-- ----------------------------
BEGIN;
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (1, 'ERSS202605082230000001A1B2C3', 'rental', 5, 1, 2, 4.00, 1, '7天', 30.00, 0.00, 58.00, 'pickup', 'offline', 'WAIT_RECEIVE', 'ESCROW_HOLDING', NULL, NULL, '2026-05-08 21:30:00', '2026-05-08 21:30:00');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (2, 'ERSS202605091143297838AD33A', 'purchase', 6, 3, 1, 1.00, 1, '', 0.00, 3.00, 4.00, 'station', 'wallet', 'COMPLETED', 'ESCROW_RELEASED', '2026-05-09 13:13:48', '2026-05-24 13:13:48', '2026-05-09 11:43:30', '2026-05-09 16:10:50');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (3, 'ERSS2026050913223161042B98D', 'purchase', 4, 1, 3, 22.00, 1, '', 0.00, 0.00, 22.00, 'pickup', 'wechat', 'COMPLETED', 'ESCROW_RELEASED', NULL, '2026-05-24 13:22:32', '2026-05-09 13:22:32', '2026-05-09 15:22:55');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (4, 'ERSS20260509160253279B026C5', 'purchase', 3, 1, 3, 12.00, 1, '', 0.00, 0.00, 12.00, 'pickup', 'alipay', 'COMPLETED', 'ESCROW_RELEASED', NULL, '2026-05-24 16:02:53', '2026-05-09 16:02:53', '2026-05-09 16:11:09');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (5, 'ERSS20260509162758239BB59ED', 'purchase', 8, 3, 1, 11.00, 1, '', 0.00, 0.00, 11.00, 'pickup', 'wechat', 'PAYOUT_PENDING', 'ESCROW_RELEASE_PENDING', NULL, '2026-05-24 16:27:58', '2026-05-09 16:27:58', '2026-05-09 16:33:29');
COMMIT;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` varchar(32) NOT NULL,
  `password_hash` char(64) NOT NULL,
  `username` varchar(64) NOT NULL,
  `avatar` varchar(512) DEFAULT '',
  `campus` varchar(64) DEFAULT '',
  `department` varchar(128) DEFAULT '',
  `bio` varchar(512) DEFAULT '',
  `role` varchar(32) NOT NULL DEFAULT 'student',
  `shop_registered` tinyint(1) NOT NULL DEFAULT '0',
  `banned` tinyint(1) NOT NULL DEFAULT '0',
  `ban_reason` varchar(255) DEFAULT '',
  `banned_at` datetime DEFAULT NULL,
  `wallet_balance` decimal(12,2) NOT NULL DEFAULT '0.00',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_student_id` (`student_id`),
  KEY `idx_users_role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
INSERT INTO `users` (`id`, `student_id`, `password_hash`, `username`, `avatar`, `campus`, `department`, `bio`, `role`, `shop_registered`, `banned`, `ban_reason`, `banned_at`, `wallet_balance`, `created_at`, `updated_at`) VALUES (1, '20260001', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '陈予安', '', '主校区', '新闻与传播学院', '偏爱纸质书，也愿意把好书继续流转出去。', 'seller', 1, 0, '', NULL, 32.00, '2026-05-08 15:24:14', '2026-05-09 16:11:08');
INSERT INTO `users` (`id`, `student_id`, `password_hash`, `username`, `avatar`, `campus`, `department`, `bio`, `role`, `shop_registered`, `banned`, `ban_reason`, `banned_at`, `wallet_balance`, `created_at`, `updated_at`) VALUES (2, '20260002', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '林知远', '', '主校区', '数学系大三', '', 'student', 0, 0, '', NULL, 19.00, '2026-05-08 15:24:14', '2026-05-09 11:22:34');
INSERT INTO `users` (`id`, `student_id`, `password_hash`, `username`, `avatar`, `campus`, `department`, `bio`, `role`, `shop_registered`, `banned`, `ban_reason`, `banned_at`, `wallet_balance`, `created_at`, `updated_at`) VALUES (3, '20260003', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '周可', '', '研究生院', '新闻传播研一', '', 'seller', 1, 0, '', NULL, 1.00, '2026-05-08 15:24:14', '2026-05-09 16:10:48');
INSERT INTO `users` (`id`, `student_id`, `password_hash`, `username`, `avatar`, `campus`, `department`, `bio`, `role`, `shop_registered`, `banned`, `ban_reason`, `banned_at`, `wallet_balance`, `created_at`, `updated_at`) VALUES (4, '90000000', 'f1f53efb866639dd11c1013678f531ba44775243a24ad943d77721d67278f5de', '系统管理员', '', '主校区', '信息中心', '负责报损鉴定与结果审核。', 'admin', 0, 0, '', NULL, 0.00, '2026-05-08 15:24:14', '2026-05-08 15:24:14');
COMMIT;

-- ----------------------------
-- Table structure for wallet_transactions
-- ----------------------------
DROP TABLE IF EXISTS `wallet_transactions`;
CREATE TABLE `wallet_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` varchar(16) NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `balance_after` decimal(12,2) NOT NULL,
  `category` varchar(32) NOT NULL,
  `title` varchar(128) NOT NULL,
  `reference_id` varchar(64) DEFAULT '',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_wallet_tx_user` (`user_id`,`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of wallet_transactions
-- ----------------------------
BEGIN;
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (1, 1, 'EXPENSE', 9.00, 20.00, 'WITHDRAW', '提现', 'withdrawal_1', '2026-05-09 15:58:04');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (2, 3, 'INCOME', 1.00, 1.00, 'PAYOUT_RELEASE', '售书收入到账', 'payout_3', '2026-05-09 16:10:48');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (3, 1, 'INCOME', 12.00, 32.00, 'PAYOUT_RELEASE', '售书收入到账', 'payout_2', '2026-05-09 16:11:08');
COMMIT;

-- ----------------------------
-- Table structure for wallet_withdrawals
-- ----------------------------
DROP TABLE IF EXISTS `wallet_withdrawals`;
CREATE TABLE `wallet_withdrawals` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `channel` varchar(32) NOT NULL,
  `destination_account` varchar(128) DEFAULT '',
  `note` varchar(255) DEFAULT '',
  `status` varchar(32) NOT NULL DEFAULT 'SUCCESS',
  `requested_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `completed_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_wallet_withdrawals_user` (`user_id`,`requested_at`),
  KEY `idx_wallet_withdrawals_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of wallet_withdrawals
-- ----------------------------
BEGIN;
INSERT INTO `wallet_withdrawals` (`id`, `user_id`, `amount`, `channel`, `destination_account`, `note`, `status`, `requested_at`, `completed_at`) VALUES (1, 1, 9.00, 'alipay', '', '', 'SUCCESS', '2026-05-09 15:58:04', '2026-05-09 15:58:04');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
