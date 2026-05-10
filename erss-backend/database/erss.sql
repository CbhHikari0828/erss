/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80046 (8.0.46)
 Source Host           : 127.0.0.1:3306
 Source Schema         : erss

 Target Server Type    : MySQL
 Target Server Version : 80046 (8.0.46)
 File Encoding         : 65001

 Date: 10/05/2026 09:05:03
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (10, 9, 'http://localhost:9000/erss/images/books/2026/05/09/d961b8d3-a0c3-45c2-a51f-e746610e5d19.png', 0);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (11, 10, 'http://localhost:9000/erss/images/books/2026/05/09/4662fbb1-08b2-4f95-96e6-09607a40edd2.jpg', 0);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (12, 11, 'http://localhost:9000/erss/images/books/2026/05/09/10475850-33eb-4b98-b856-8339d31fbcb8.jpg', 0);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (13, 12, 'http://localhost:9000/erss/images/books/2026/05/09/72a36094-07b4-455a-959a-5217e1a80991.png', 0);
INSERT INTO `book_images` (`id`, `book_id`, `image_url`, `sort_no`) VALUES (14, 13, 'http://localhost:9000/erss/images/books/2026/05/09/fbed566e-90e1-4f0e-a5f1-25562db7596f.jpg', 0);
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of books
-- ----------------------------
BEGIN;
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (1, 1, '高等数学 同济第七版 上册', '教材教辅', '高等数学', 18.00, 49.80, 0, 0.00, 0.00, '九成新', '主校区', 'https://images.pexels.com/photos/3782249/pexels-photo-3782249.jpeg?cs=srgb&dl=pexels-polina-zimmerman-3782249.jpg&fm=jpg', '课后习题页完整，无缺页，封面有轻微折痕。', '大一时使用的教材，重点章节有铅笔标注，已擦除大部分。适合本校理工类专业基础课复习。', '可小刀,自提优先,附笔记', 'ON_SALE', '2026-05-08 15:24:14', '2026-05-09 20:48:50');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (2, 1, '张剑黄皮书 2026 考研英语真题', '考研考公', '考研英语', 26.00, 79.00, 0, 0.00, 0.00, '全新未写', '研究生院', 'https://images.pexels.com/photos/4219041/pexels-photo-4219041.jpeg?cs=srgb&dl=pexels-karola-g-4219041.jpg&fm=jpg', '买重了，塑封还在。', '适合 2027 届考研同学提前备考，书脊无压痕，支持主校区地铁口面交。', '价格稳,考研季,可邮寄', 'ON_SALE', '2026-05-08 15:24:14', '2026-05-08 15:24:14');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (3, 1, '大学英语四级词汇闪过', '语言证书', '四六级', 12.00, 39.80, 0, 0.00, 0.00, '八成新', '南校区', 'https://images.pexels.com/photos/27668991/pexels-photo-27668991.jpeg?cs=srgb&dl=pexels-fiona-murray-537687299-27668991.jpg&fm=jpg', '有重点划线，背词效率很高。', '书内有一部分荧光笔划线，适合临时冲刺备考，不影响继续使用。', '笔记友好,四六级,当天可拿', 'SOLD', '2026-05-08 15:24:14', '2026-05-09 16:02:53');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (4, 1, '传播学教程 第三版', '教材教辅', '大学英语', 22.00, 56.00, 0, 0.00, 0.00, '九成新', '主校区', 'https://images.pexels.com/photos/5009227/pexels-photo-5009227.jpeg?auto=compress&cs=tinysrgb&w=800', '老师指定教材，期末复习非常够用。', '内页很干净，仅目录页有课程安排记录，适合新闻传播类同学接手。', '专业课,本学期热门', 'SOLD', '2026-05-08 15:24:14', '2026-05-09 13:22:32');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (5, 1, 'Linux 系统与网络服务', '教材教辅', '计算机基础', 20.00, 49.00, 1, 4.00, 30.00, '九成新', '主校区', 'https://images.pexels.com/photos/1181671/pexels-photo-1181671.jpeg?auto=compress&cs=tinysrgb&w=800', '作为租借样例保留，便于管理员审核报损流程。', '这本书已用于一次租借流程样例，当前状态会配合管理员工作台展示。', '租借样例,管理员审核', 'RENTED', '2026-05-08 15:24:14', '2026-05-09 20:54:01');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (6, 3, '测试书籍', '教材教辅', '', 1.00, 11.00, 0, 0.00, 0.00, '全新未写', '主校区', 'http://localhost:9000/erss/images/books/2026/05/09/2fc837c4-6652-4f11-abb4-6860981cd9e9.webp', '1', '1', '', 'SOLD', '2026-05-09 11:42:16', '2026-05-09 11:43:30');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (7, 3, 'redBook', '教材教辅', '', 12.00, 111.00, 1, 4.00, 30.00, '全新未写', '主校区', 'http://localhost:9000/erss/images/books/2026/05/09/e0017ce7-7c6b-42cd-a7f9-5be617040b11.png', '', '', '', 'ON_SALE', '2026-05-09 16:21:46', '2026-05-09 23:00:57');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (8, 3, 'Qs', '教材教辅', '', 11.00, 23.00, 0, 0.00, 0.00, '全新未写', '主校区', 'http://localhost:9000/erss/images/books/2026/05/09/a730cd7d-a117-4f05-a0d3-3a1f6b2c4a86.jpg', '', '', '', 'SOLD', '2026-05-09 16:25:48', '2026-05-09 16:27:57');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (9, 3, 'Zulin', '教材教辅', '', 18.00, 49.00, 1, 4.00, 30.00, '全新未写', '主校区', 'http://localhost:9000/erss/images/books/2026/05/09/d961b8d3-a0c3-45c2-a51f-e746610e5d19.png', '11', '11', '', 'RENTED', '2026-05-09 18:00:43', '2026-05-09 20:31:58');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (10, 3, 'Hi', '教材教辅', '', 11.00, 111.00, 1, 4.00, 39.00, '全新未写', '主校区', 'http://localhost:9000/erss/images/books/2026/05/09/4662fbb1-08b2-4f95-96e6-09607a40edd2.jpg', '', '', '', 'SOLD', '2026-05-09 18:07:33', '2026-05-09 19:28:19');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (11, 3, 'Hi', '教材教辅', '', 11.00, 123.00, 1, 1.00, 23.00, '全新未写', '主校区', 'http://localhost:9000/erss/images/books/2026/05/09/10475850-33eb-4b98-b856-8339d31fbcb8.jpg', '', '', '', 'SOLD', '2026-05-09 18:08:17', '2026-05-09 18:28:01');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (12, 1, 'ZuO', '教材教辅', '', 11.00, 1111.00, 1, 1.00, 12.00, '全新未写', '主校区', 'http://localhost:9000/erss/images/books/2026/05/09/72a36094-07b4-455a-959a-5217e1a80991.png', '', '', '', 'ON_SALE', '2026-05-09 21:25:32', '2026-05-09 21:50:57');
INSERT INTO `books` (`id`, `seller_id`, `title`, `category`, `subcategory`, `price`, `original_price`, `rental_enabled`, `rent_price`, `rental_deposit`, `condition_text`, `campus`, `cover_url`, `summary`, `description`, `tags`, `status`, `created_at`, `updated_at`) VALUES (13, 5, 'Er', '教材教辅', '', 18.00, 49.00, 1, 4.00, 30.00, '全新未写', '主校区', 'http://localhost:9000/erss/images/books/2026/05/09/fbed566e-90e1-4f0e-a5f1-25562db7596f.jpg', '', '', '', 'ON_SALE', '2026-05-09 22:38:13', '2026-05-09 22:46:18');
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of damage_reports
-- ----------------------------
BEGIN;
INSERT INTO `damage_reports` (`id`, `rental_order_id`, `reporter_id`, `description`, `images`, `status`, `review_note`, `compensation_amount`, `created_at`, `reviewed_at`) VALUES (1, 1, 2, '封面右下角有轻微磨损，书页边缘有一处水渍，申请管理员判定是否需要赔付。', 'https://images.pexels.com/photos/1181671/pexels-photo-1181671.jpeg?auto=compress&cs=tinysrgb&w=800,https://images.pexels.com/photos/159844/book-book-pages-read-literature-159844.jpeg?auto=compress&cs=tinysrgb&w=800', 'REVIEWED', '11', 11.00, '2026-05-08 21:45:00', '2026-05-09 11:22:34');
INSERT INTO `damage_reports` (`id`, `rental_order_id`, `reporter_id`, `description`, `images`, `status`, `review_note`, `compensation_amount`, `created_at`, `reviewed_at`) VALUES (2, 4, 1, 'ceshi', 'http://localhost:9000/erss/images/damage-reports/2026/05/09/53738ae9-a65e-45e9-801c-7dcdbd554ee3.png', 'REVIEWED', '', 2.00, '2026-05-09 21:49:38', '2026-05-09 21:50:02');
INSERT INTO `damage_reports` (`id`, `rental_order_id`, `reporter_id`, `description`, `images`, `status`, `review_note`, `compensation_amount`, `created_at`, `reviewed_at`) VALUES (3, 5, 5, '111', 'http://localhost:9000/erss/images/damage-reports/2026/05/09/9f726c0a-a4ff-407c-867b-020c7cc9e8f1.jpg', 'REVIEWED', '', 12.00, '2026-05-09 22:44:54', '2026-05-09 22:45:16');
INSERT INTO `damage_reports` (`id`, `rental_order_id`, `reporter_id`, `description`, `images`, `status`, `review_note`, `compensation_amount`, `created_at`, `reviewed_at`) VALUES (4, 6, 3, '11', 'http://localhost:9000/erss/images/damage-reports/2026/05/09/bbabeb8b-07ce-427c-a91e-d228c53a77ca.jpg', 'REVIEWED', 'op', 12.00, '2026-05-09 23:00:04', '2026-05-09 23:00:27');
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
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of notifications
-- ----------------------------
BEGIN;
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (3, 2, 'rental', '报损审核结果', '报损已审核，押金中 11 元赔付给书主，19.00 元退回租客钱包。', '通过并赔付', '查看详情', '/message', 0, '2026-05-09 11:22:34');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (5, 2, 'wallet', '押金已退回', '租赁押金退回 19.00 元，已记入你的钱包余额。', '已退回', '查看钱包', '/wallet', 0, '2026-05-09 11:22:34');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (11, 3, 'trade', '确认收货成功', '你的确认已提交，系统将进入卖家资金发放流程。', '待发放', '查看订单', '/orders', 0, '2026-05-09 15:22:19');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (12, 4, 'system', '新的待发放资金', '订单「ERSS2026050913223161042B98D」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 15:22:19');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (19, 4, 'system', '新的待发放资金', '订单「ERSS202605091143297838AD33A」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 16:07:18');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (22, 4, 'system', '新的待发放资金', '订单「ERSS20260509160253279B026C5」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 16:10:04');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (27, 3, 'trade', '有新的书籍订单', '微信模拟支付成功，金额 11.00 元已进入系统托管，买家选择自提，等待买家确认收货。', '待收货', '查看订单', '/my-shop', 0, '2026-05-09 16:28:00');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (28, 3, 'trade', '待发放资金', '买家已确认收货，订单金额已进入待发放状态，等待管理员处理。', '待发放', '查看资金', '/my-shop', 0, '2026-05-09 16:33:30');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (29, 1, 'trade', '确认收货成功', '你的确认已提交，系统将进入卖家资金发放流程。', '待发放', '查看订单', '/orders', 0, '2026-05-09 16:33:30');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (30, 4, 'system', '新的待发放资金', '订单「ERSS20260509162758239BB59ED」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 16:33:31');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (31, 3, 'trade', '资金已发放', '你在订单「Qs」的待发放金额已进入钱包，当前余额可提现。', '已发放', '查看钱包', '/my-shop', 0, '2026-05-09 16:46:01');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (32, 1, 'trade', '订单已完成', '你已确认收货，平台已完成对卖家的资金发放。', '已完成', '查看订单', '/orders', 0, '2026-05-09 16:46:01');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (33, 1, 'trade', '有新的书籍订单', '支付宝模拟支付成功，金额 21.00 元已进入系统托管，等待你发货。', '待发货', '查看订单', '/my-shop', 0, '2026-05-09 18:09:27');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (34, 3, 'trade', '有新的书籍订单', '系统钱包支付成功，金额 11.00 元已从余额扣除并进入托管，买家选择自提，等待买家确认收货。', '待收货', '查看订单', '/my-shop', 0, '2026-05-09 18:28:01');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (35, 3, 'trade', '卖家已发货', '请在收到书籍后确认收货。', '待确认', '查看订单', '/orders', 0, '2026-05-09 18:28:17');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (36, 3, 'trade', '待发放资金', '买家已确认收货，订单金额已进入待发放状态，等待管理员处理。', '待发放', '查看资金', '/my-shop', 0, '2026-05-09 18:28:29');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (37, 1, 'trade', '确认收货成功', '你的确认已提交，系统将进入卖家资金发放流程。', '待发放', '查看订单', '/orders', 0, '2026-05-09 18:28:29');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (38, 4, 'system', '新的待发放资金', '订单「ERSS20260509182800562B4AEC2」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 18:28:29');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (39, 3, 'trade', '资金已发放', '你在订单「Hi」的待发放金额已进入钱包，当前余额可提现。', '已发放', '查看钱包', '/my-shop', 0, '2026-05-09 19:25:45');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (40, 1, 'trade', '订单已完成', '你已确认收货，平台已完成对卖家的资金发放。', '已完成', '查看订单', '/orders', 0, '2026-05-09 19:25:45');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (41, 1, 'trade', '待发放资金', '买家已确认收货，订单金额已进入待发放状态，等待管理员处理。', '待发放', '查看资金', '/my-shop', 0, '2026-05-09 19:27:33');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (42, 3, 'trade', '确认收货成功', '你的确认已提交，系统将进入卖家资金发放流程。', '待发放', '查看订单', '/orders', 1, '2026-05-09 19:27:33');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (43, 4, 'system', '新的待发放资金', '订单「ERSS20260509180927009F62782」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 19:27:33');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (44, 1, 'trade', '资金已发放', '你在订单「高等数学 同济第七版 上册」的待发放金额已进入钱包，当前余额可提现。', '已发放', '查看钱包', '/my-shop', 0, '2026-05-09 19:27:45');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (45, 3, 'trade', '订单已完成', '你已确认收货，平台已完成对卖家的资金发放。', '已完成', '查看订单', '/orders', 1, '2026-05-09 19:27:45');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (46, 3, 'trade', '有新的书籍订单', '系统钱包支付成功，金额 14.00 元已从余额扣除并进入托管，等待你发货。', '待发货', '查看订单', '/my-shop', 1, '2026-05-09 19:28:19');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (47, 1, 'trade', '卖家已发货', '请在收到书籍后确认收货。', '待确认', '查看订单', '/orders', 0, '2026-05-09 19:31:15');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (48, 3, 'trade', '待发放资金', '买家已确认收货，订单金额已进入待发放状态，等待管理员处理。', '待发放', '查看资金', '/my-shop', 1, '2026-05-09 19:32:00');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (49, 1, 'trade', '确认收货成功', '你的确认已提交，系统将进入卖家资金发放流程。', '待发放', '查看订单', '/orders', 0, '2026-05-09 19:32:00');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (50, 4, 'system', '新的待发放资金', '订单「ERSS20260509192819072C300FC」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 19:32:00');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (51, 3, 'trade', '资金已发放', '你在订单「Hi」的待发放金额已进入钱包，当前余额可提现。', '已发放', '查看钱包', '/my-shop', 1, '2026-05-09 19:47:16');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (52, 1, 'trade', '订单已完成', '你已确认收货，平台已完成对卖家的资金发放。', '已完成', '查看订单', '/orders', 0, '2026-05-09 19:47:16');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (53, 3, 'trade', '有新的书籍订单', '校园卡支付成功，金额 66.00 元已进入系统托管，买家选择自提，等待买家确认收货。', '待收货', '查看订单', '/my-shop', 1, '2026-05-09 20:31:58');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (54, 3, 'trade', '待发放资金', '买家已确认收货，订单金额已进入待发放状态，等待管理员处理。', '待发放', '查看资金', '/my-shop', 1, '2026-05-09 20:37:51');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (55, 1, 'trade', '确认收货成功', '你的确认已提交，系统将进入卖家资金发放流程。', '待发放', '查看订单', '/orders', 0, '2026-05-09 20:37:51');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (56, 4, 'system', '新的待发放资金', '订单「ERSS2026050920315834269FEF4」已进入待发放状态，请前往资金管理处理。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 20:37:51');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (57, 1, 'trade', '待发放资金', '买家已归还，订单金额 27.00 元（已扣除1元报损服务费） 已进入待发放状态，等待管理员处理。', '待发放', '查看资金', '/my-shop', 0, '2026-05-09 20:49:35');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (58, 2, 'trade', '还书确认完成', '卖家已确认还书，系统将进入资金发放流程。', '已完成', '查看订单', '/orders', 0, '2026-05-09 20:49:35');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (59, 4, 'system', '新的待发放资金', '租赁订单「ERSS202605082230000001A1B2C3」还书已确认，进入待发放状态。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 20:49:35');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (60, 1, 'trade', '资金已发放', '你在订单「Linux 系统与网络服务」的待发放金额已进入钱包，当前余额可提现。', '已发放', '查看钱包', '/my-shop', 0, '2026-05-09 20:49:48');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (61, 2, 'trade', '订单已完成', '你已确认收货，平台已完成对卖家的资金发放。', '已完成', '查看订单', '/orders', 0, '2026-05-09 20:49:48');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (62, 3, 'trade', '资金已发放', '你在订单「Zulin」的待发放金额已进入钱包，当前余额可提现。', '已发放', '查看钱包', '/my-shop', 1, '2026-05-09 20:51:42');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (63, 1, 'trade', '订单已完成', '你已确认收货，平台已完成对卖家的资金发放。', '已完成', '查看订单', '/orders', 0, '2026-05-09 20:51:42');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (64, 1, 'trade', '有新的书籍订单', '校园卡支付成功，金额 37.00 元已进入系统托管，等待你发货。', '待发货', '查看订单', '/my-shop', 0, '2026-05-09 20:54:01');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (65, 1, 'trade', '有新的书籍订单', '校园卡支付成功，金额 16.00 元已进入系统托管，等待你发货。', '待发货', '查看订单', '/my-shop', 0, '2026-05-09 21:26:39');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (66, 2, 'trade', '卖家已发货', '请在收到书籍后确认收货。', '待确认', '查看订单', '/orders', 0, '2026-05-09 21:27:24');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (67, 1, 'rental', '买家已发起还书', '林知远已发起还书（快递驿站），请确认还书。', '待确认', '查看租借', '/my-shop', 0, '2026-05-09 21:48:25');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (68, 2, 'rental', '报损审核结果', '报损已审核，押金中 2 元赔付给书主，10.00 元已退回你的钱包。', '通过并赔付', '查看详情', '/message', 0, '2026-05-09 21:50:02');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (69, 1, 'rental', '报损审核结果', '报损已审核，你收到报损赔付 2 元，已记入你的余额。', '通过并赔付', '查看详情', '/message', 0, '2026-05-09 21:50:02');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (70, 2, 'wallet', '押金已退回', '租赁押金退回 10.00 元，已记入你的钱包余额。', '已退回', '查看钱包', '/wallet', 0, '2026-05-09 21:50:02');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (71, 1, 'wallet', '赔付已入账', '你收到报损赔付 2 元，已记入余额。', '已入账', '查看钱包', '/wallet', 0, '2026-05-09 21:50:02');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (72, 1, 'trade', '待发放资金', '买家已归还，订单金额 0.00 元（已扣除1元报损服务费） 已进入待发放状态，等待管理员处理。', '待发放', '查看资金', '/my-shop', 0, '2026-05-09 21:50:57');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (73, 2, 'trade', '还书确认完成', '卖家已确认还书，系统将进入资金发放流程。', '已完成', '查看订单', '/orders', 0, '2026-05-09 21:50:57');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (74, 4, 'system', '新的待发放资金', '租赁订单「ERSS20260509212638692686EE9」还书已确认，进入待发放状态。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 21:50:57');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (75, 5, 'trade', '有新的书籍订单', '校园卡支付成功，金额 58.00 元已进入系统托管，买家选择自提，等待买家确认收货。', '待收货', '查看订单', '/my-shop', 1, '2026-05-09 22:42:26');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (76, 5, 'rental', '买家已发起还书', '陈予安已发起还书（上门自提），请确认还书。', '待确认', '查看租借', '/my-shop', 1, '2026-05-09 22:43:55');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (77, 1, 'rental', '报损审核结果', '报损已审核，押金中 12 元赔付给书主，18.00 元已退回你的钱包。', '通过并赔付', '查看详情', '/message', 0, '2026-05-09 22:45:17');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (78, 5, 'rental', '报损审核结果', '报损已审核，你收到报损赔付 12 元，已记入你的余额。', '通过并赔付', '查看详情', '/message', 1, '2026-05-09 22:45:17');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (79, 1, 'wallet', '押金已退回', '租赁押金退回 18.00 元，已记入你的钱包余额。', '已退回', '查看钱包', '/wallet', 0, '2026-05-09 22:45:17');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (80, 5, 'wallet', '赔付已入账', '你收到报损赔付 12 元，已记入余额。', '已入账', '查看钱包', '/wallet', 1, '2026-05-09 22:45:17');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (81, 5, 'trade', '待发放资金', '买家已归还，订单金额 27.00 元（已扣除1元报损服务费） 已进入待发放状态，等待管理员处理。', '待发放', '查看资金', '/my-shop', 1, '2026-05-09 22:46:18');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (82, 1, 'trade', '还书确认完成', '卖家已确认还书，系统将进入资金发放流程。', '已完成', '查看订单', '/orders', 0, '2026-05-09 22:46:18');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (83, 4, 'system', '新的待发放资金', '租赁订单「ERSS20260509224226116F48845」还书已确认，进入待发放状态。', '待处理', '进入资金管理', '/finance', 1, '2026-05-09 22:46:18');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (84, 3, 'trade', '有新的书籍订单', '系统钱包支付成功，金额 58.00 元已从余额扣除并进入托管，买家选择自提，等待买家确认收货。', '待收货', '查看订单', '/my-shop', 1, '2026-05-09 22:48:57');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (85, 3, 'rental', '买家已发起还书', '陈予安已发起还书（快递驿站），请确认还书。', '待确认', '查看租借', '/my-shop', 1, '2026-05-09 22:59:27');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (86, 1, 'rental', '报损审核结果', '报损已审核，押金中 12 元赔付给书主，18.00 元已退回你的钱包。', '通过并赔付', '查看详情', '/message', 0, '2026-05-09 23:00:27');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (87, 3, 'rental', '报损审核结果', '报损已审核，你收到报损赔付 12 元，已记入你的余额。', '通过并赔付', '查看详情', '/message', 1, '2026-05-09 23:00:27');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (88, 1, 'wallet', '押金已退回', '租赁押金退回 18.00 元，已记入你的钱包余额。', '已退回', '查看钱包', '/wallet', 0, '2026-05-09 23:00:27');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (89, 3, 'wallet', '赔付已入账', '你收到报损赔付 12 元，已记入余额。', '已入账', '查看钱包', '/wallet', 1, '2026-05-09 23:00:27');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (90, 3, 'trade', '还书确认完成', '买家已归还，报损赔付已直接入账，订单完结。', '已完成', '查看订单', '/orders', 0, '2026-05-09 23:00:57');
INSERT INTO `notifications` (`id`, `user_id`, `category`, `title`, `summary`, `status_text`, `action_text`, `route`, `unread`, `created_at`) VALUES (91, 1, 'trade', '还书确认完成', '卖家已确认还书，订单完结。', '已完成', '查看订单', '/orders', 0, '2026-05-09 23:00:57');
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of payout_requests
-- ----------------------------
BEGIN;
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (1, 3, 1, 22.00, 'PAID', '2026-05-09 15:22:19', '2026-05-09 15:22:53', 4, '管理员发放');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (2, 4, 1, 12.00, 'PAID', '2026-05-09 16:10:04', '2026-05-09 16:11:09', 4, '管理员发放');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (3, 2, 3, 1.00, 'PAID', '2026-05-09 16:07:15', '2026-05-09 16:10:49', 4, '管理员发放');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (4, 5, 3, 11.00, 'PAID', '2026-05-09 16:33:29', '2026-05-09 16:46:00', 4, '管理员发放');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (5, 6, 1, 18.00, 'PAID', '2026-05-09 19:27:33', '2026-05-09 19:27:45', 4, '平台已结清，确认发放到卖家钱包');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (6, 7, 3, 11.00, 'PAID', '2026-05-09 18:28:29', '2026-05-09 19:25:44', 4, '平台已结清，确认发放到卖家钱包');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (7, 8, 3, 11.00, 'PAID', '2026-05-09 19:32:00', '2026-05-09 19:47:15', 4, '平台已结清，确认发放到卖家钱包');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (8, 9, 3, 36.00, 'PAID', '2026-05-09 20:37:51', '2026-05-09 20:51:42', 4, '平台已结清，确认发放到卖家钱包');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (9, 1, 1, 27.00, 'PAID', '2026-05-09 20:49:35', '2026-05-09 20:49:47', 4, '平台已结清，确认发放到卖家钱包');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (10, 10, 1, 4.00, 'ESCROW_HOLDING', '2026-05-09 20:54:01', NULL, NULL, '');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (11, 11, 1, 0.00, 'PENDING', '2026-05-09 21:50:57', NULL, NULL, '');
INSERT INTO `payout_requests` (`id`, `order_id`, `seller_id`, `amount`, `status`, `requested_at`, `released_at`, `released_by`, `release_note`) VALUES (12, 12, 5, 27.00, 'PENDING', '2026-05-09 22:46:18', NULL, NULL, '');
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
  `return_delivery_method` varchar(16) DEFAULT '',
  `status` varchar(32) NOT NULL,
  `due_at` datetime NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_rental_orders_owner` (`owner_id`,`created_at`),
  KEY `idx_rental_orders_borrower` (`borrower_id`,`created_at`),
  KEY `idx_rental_orders_book_id` (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of rental_orders
-- ----------------------------
BEGIN;
INSERT INTO `rental_orders` (`id`, `trade_order_id`, `book_id`, `owner_id`, `borrower_id`, `period_text`, `deposit_amount`, `return_delivery_method`, `status`, `due_at`, `created_at`, `updated_at`) VALUES (1, 1, 5, 1, 2, '7天', 30.00, '', 'RETURNED', '2026-05-15 21:30:00', '2026-05-08 21:30:00', '2026-05-09 20:49:35');
INSERT INTO `rental_orders` (`id`, `trade_order_id`, `book_id`, `owner_id`, `borrower_id`, `period_text`, `deposit_amount`, `return_delivery_method`, `status`, `due_at`, `created_at`, `updated_at`) VALUES (2, 9, 9, 3, 1, '9天', 30.00, '', 'ACTIVE', '2026-05-18 20:31:58', '2026-05-09 20:31:58', '2026-05-09 13:24:31');
INSERT INTO `rental_orders` (`id`, `trade_order_id`, `book_id`, `owner_id`, `borrower_id`, `period_text`, `deposit_amount`, `return_delivery_method`, `status`, `due_at`, `created_at`, `updated_at`) VALUES (3, 10, 5, 1, 3, '1天', 30.00, '', 'ACTIVE', '2026-05-10 20:54:01', '2026-05-09 20:54:01', '2026-05-09 13:24:31');
INSERT INTO `rental_orders` (`id`, `trade_order_id`, `book_id`, `owner_id`, `borrower_id`, `period_text`, `deposit_amount`, `return_delivery_method`, `status`, `due_at`, `created_at`, `updated_at`) VALUES (4, 11, 12, 1, 2, '1天', 12.00, 'station', 'RETURNED', '2026-05-10 21:45:23', '2026-05-09 21:26:39', '2026-05-09 21:50:57');
INSERT INTO `rental_orders` (`id`, `trade_order_id`, `book_id`, `owner_id`, `borrower_id`, `period_text`, `deposit_amount`, `return_delivery_method`, `status`, `due_at`, `created_at`, `updated_at`) VALUES (5, 12, 13, 5, 1, '7天', 30.00, 'pickup', 'RETURNED', '2026-05-16 22:42:26', '2026-05-09 22:42:26', '2026-05-09 22:46:18');
INSERT INTO `rental_orders` (`id`, `trade_order_id`, `book_id`, `owner_id`, `borrower_id`, `period_text`, `deposit_amount`, `return_delivery_method`, `status`, `due_at`, `created_at`, `updated_at`) VALUES (6, 13, 7, 3, 1, '7天', 30.00, 'station', 'RETURNED', '2026-05-16 22:48:57', '2026-05-09 22:48:57', '2026-05-09 23:00:57');
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of shops
-- ----------------------------
BEGIN;
INSERT INTO `shops` (`id`, `user_id`, `shop_name`, `shop_campus`, `shop_intro`, `created_at`, `updated_at`) VALUES (1, 1, '主校区二手教材小店', '主校区', '主营教材教辅、考研资料和语言证书用书，支持租借与面交。', '2026-05-08 15:24:14', '2026-05-08 15:24:14');
INSERT INTO `shops` (`id`, `user_id`, `shop_name`, `shop_campus`, `shop_intro`, `created_at`, `updated_at`) VALUES (2, 3, 'ZK', '研究生院', 'ZK1', '2026-05-09 11:28:07', '2026-05-09 11:28:07');
INSERT INTO `shops` (`id`, `user_id`, `shop_name`, `shop_campus`, `shop_intro`, `created_at`, `updated_at`) VALUES (3, 5, '1n', '主校区', '11', '2026-05-09 22:37:40', '2026-05-09 22:37:40');
COMMIT;

-- ----------------------------
-- Table structure for system_transactions
-- ----------------------------
DROP TABLE IF EXISTS `system_transactions`;
CREATE TABLE `system_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(32) NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `description` varchar(255) DEFAULT '',
  `reference_id` varchar(64) DEFAULT '',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sys_tx_category` (`category`),
  KEY `idx_sys_tx_created` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of system_transactions
-- ----------------------------
BEGIN;
INSERT INTO `system_transactions` (`id`, `category`, `amount`, `description`, `reference_id`, `created_at`) VALUES (1, 'DAMAGE_SERVICE_FEE', 1.00, '报损服务费 租借#5', 'rental_5', '2026-05-09 22:46:18');
INSERT INTO `system_transactions` (`id`, `category`, `amount`, `description`, `reference_id`, `created_at`) VALUES (2, 'RETURN_DELIVERY_FEE', 3.00, '还书驿站费 租借#6', 'rental_6', '2026-05-09 22:59:27');
INSERT INTO `system_transactions` (`id`, `category`, `amount`, `description`, `reference_id`, `created_at`) VALUES (3, 'DAMAGE_SERVICE_FEE', 1.00, '报损服务费 租借#6', 'rental_6', '2026-05-09 23:00:57');
INSERT INTO `system_transactions` (`id`, `category`, `amount`, `description`, `reference_id`, `created_at`) VALUES (4, 'DAMAGE_SERVICE_FEE', 28.00, '报损订单租金收入 租借#6', 'rental_6', '2026-05-09 23:00:57');
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
INSERT INTO `system_wallet` (`id`, `escrow_balance`, `created_at`, `updated_at`) VALUES (1, 10116.00, '2026-05-08 16:20:39', '2026-05-09 23:00:57');
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of trade_orders
-- ----------------------------
BEGIN;
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (1, 'ERSS202605082230000001A1B2C3', 'rental', 5, 1, 2, 4.00, 1, '7天', 30.00, 0.00, 58.00, 'pickup', 'offline', 'COMPLETED', 'ESCROW_RELEASED', NULL, NULL, '2026-05-08 21:30:00', '2026-05-09 20:49:48');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (2, 'ERSS202605091143297838AD33A', 'purchase', 6, 3, 1, 1.00, 1, '', 0.00, 3.00, 4.00, 'station', 'wallet', 'COMPLETED', 'ESCROW_RELEASED', '2026-05-09 13:13:48', '2026-05-24 13:13:48', '2026-05-09 11:43:30', '2026-05-09 16:10:50');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (3, 'ERSS2026050913223161042B98D', 'purchase', 4, 1, 3, 22.00, 1, '', 0.00, 0.00, 22.00, 'pickup', 'wechat', 'COMPLETED', 'ESCROW_RELEASED', NULL, '2026-05-24 13:22:32', '2026-05-09 13:22:32', '2026-05-09 15:22:55');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (4, 'ERSS20260509160253279B026C5', 'purchase', 3, 1, 3, 12.00, 1, '', 0.00, 0.00, 12.00, 'pickup', 'alipay', 'COMPLETED', 'ESCROW_RELEASED', NULL, '2026-05-24 16:02:53', '2026-05-09 16:02:53', '2026-05-09 16:11:09');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (5, 'ERSS20260509162758239BB59ED', 'purchase', 8, 3, 1, 11.00, 1, '', 0.00, 0.00, 11.00, 'pickup', 'wechat', 'COMPLETED', 'ESCROW_RELEASED', NULL, '2026-05-24 16:27:58', '2026-05-09 16:27:58', '2026-05-09 16:46:01');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (6, 'ERSS20260509180927009F62782', 'purchase', 1, 1, 3, 18.00, 1, '', 0.00, 3.00, 21.00, 'station', 'alipay', 'COMPLETED', 'ESCROW_RELEASED', '2026-05-09 18:28:17', '2026-05-24 18:28:17', '2026-05-09 18:09:27', '2026-05-09 19:27:45');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (7, 'ERSS20260509182800562B4AEC2', 'purchase', 11, 3, 1, 11.00, 1, '', 0.00, 0.00, 11.00, 'pickup', 'wallet', 'COMPLETED', 'ESCROW_RELEASED', NULL, '2026-05-24 18:28:01', '2026-05-09 18:28:01', '2026-05-09 19:25:45');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (8, 'ERSS20260509192819072C300FC', 'purchase', 10, 3, 1, 11.00, 1, '', 0.00, 3.00, 14.00, 'station', 'wallet', 'COMPLETED', 'ESCROW_RELEASED', '2026-05-09 19:31:15', '2026-05-24 19:31:15', '2026-05-09 19:28:19', '2026-05-09 19:47:16');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (9, 'ERSS2026050920315834269FEF4', 'rental', 9, 3, 1, 4.00, 1, '9天', 30.00, 0.00, 66.00, 'pickup', 'campus', 'COMPLETED', 'ESCROW_RELEASED', NULL, '2026-05-24 20:31:58', '2026-05-09 20:31:58', '2026-05-09 20:51:42');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (10, 'ERSS2026050920540085864C49C', 'rental', 5, 1, 3, 4.00, 1, '1天', 30.00, 3.00, 37.00, 'station', 'campus', 'WAIT_SELLER_SHIP', 'ESCROW_HOLDING', NULL, NULL, '2026-05-09 20:54:01', '2026-05-09 20:54:01');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (11, 'ERSS20260509212638692686EE9', 'rental', 12, 1, 2, 1.00, 1, '1天', 12.00, 3.00, 16.00, 'station', 'campus', 'PAYOUT_PENDING', 'ESCROW_RELEASE_PENDING', '2026-05-09 21:27:24', '2026-05-24 21:27:24', '2026-05-09 21:26:39', '2026-05-09 21:50:57');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (12, 'ERSS20260509224226116F48845', 'rental', 13, 5, 1, 4.00, 1, '7天', 30.00, 0.00, 58.00, 'pickup', 'campus', 'PAYOUT_PENDING', 'ESCROW_RELEASE_PENDING', NULL, '2026-05-24 22:42:26', '2026-05-09 22:42:26', '2026-05-09 22:46:18');
INSERT INTO `trade_orders` (`id`, `order_no`, `order_type`, `book_id`, `seller_id`, `buyer_id`, `unit_price`, `quantity`, `rental_period`, `deposit_amount`, `delivery_fee`, `total_amount`, `delivery_method`, `payment_method`, `status`, `escrow_status`, `seller_shipped_at`, `auto_confirm_at`, `created_at`, `updated_at`) VALUES (13, 'ERSS2026050922485724844EB0A', 'rental', 7, 3, 1, 4.00, 1, '7天', 30.00, 0.00, 58.00, 'pickup', 'wallet', 'COMPLETED', 'ESCROW_RELEASED', NULL, '2026-05-24 22:48:57', '2026-05-09 22:48:57', '2026-05-09 23:00:57');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
INSERT INTO `users` (`id`, `student_id`, `password_hash`, `username`, `avatar`, `campus`, `department`, `bio`, `role`, `shop_registered`, `banned`, `ban_reason`, `banned_at`, `wallet_balance`, `created_at`, `updated_at`) VALUES (1, '20260001', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '陈予安', '', '主校区', '新闻与传播学院', '偏爱纸质书，也愿意把好书继续流转出去。', 'seller', 1, 0, '', NULL, 29.00, '2026-05-08 15:24:14', '2026-05-09 23:00:27');
INSERT INTO `users` (`id`, `student_id`, `password_hash`, `username`, `avatar`, `campus`, `department`, `bio`, `role`, `shop_registered`, `banned`, `ban_reason`, `banned_at`, `wallet_balance`, `created_at`, `updated_at`) VALUES (2, '20260002', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '林知远', '', '主校区', '数学系大三', '', 'student', 0, 0, '', NULL, 26.00, '2026-05-08 15:24:14', '2026-05-09 21:50:02');
INSERT INTO `users` (`id`, `student_id`, `password_hash`, `username`, `avatar`, `campus`, `department`, `bio`, `role`, `shop_registered`, `banned`, `ban_reason`, `banned_at`, `wallet_balance`, `created_at`, `updated_at`) VALUES (3, '20260003', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '周可', '', '研究生院', '新闻传播研一', '', 'seller', 1, 0, '', NULL, 82.00, '2026-05-08 15:24:14', '2026-05-09 23:00:27');
INSERT INTO `users` (`id`, `student_id`, `password_hash`, `username`, `avatar`, `campus`, `department`, `bio`, `role`, `shop_registered`, `banned`, `ban_reason`, `banned_at`, `wallet_balance`, `created_at`, `updated_at`) VALUES (4, '90000000', 'f1f53efb866639dd11c1013678f531ba44775243a24ad943d77721d67278f5de', '系统管理员', '', '主校区', '信息中心', '负责报损鉴定与结果审核。', 'admin', 0, 0, '', NULL, 0.00, '2026-05-08 15:24:14', '2026-05-08 15:24:14');
INSERT INTO `users` (`id`, `student_id`, `password_hash`, `username`, `avatar`, `campus`, `department`, `bio`, `role`, `shop_registered`, `banned`, `ban_reason`, `banned_at`, `wallet_balance`, `created_at`, `updated_at`) VALUES (5, '2243232010227', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Alex', 'http://localhost:9000/erss/images/avatars/2026/05/09/cbf6d521-ad22-41e9-a09a-09b14816a805.jpg', '主校区', '信息工程学院', '', 'seller', 1, 0, '', NULL, 212.00, '2026-05-09 22:08:35', '2026-05-09 22:45:16');
INSERT INTO `users` (`id`, `student_id`, `password_hash`, `username`, `avatar`, `campus`, `department`, `bio`, `role`, `shop_registered`, `banned`, `ban_reason`, `banned_at`, `wallet_balance`, `created_at`, `updated_at`) VALUES (6, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', '学生dmin', '', '主校区', '', '', 'student', 0, 0, '', NULL, 2.00, '2026-05-09 23:39:54', '2026-05-09 23:39:54');
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
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of wallet_transactions
-- ----------------------------
BEGIN;
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (1, 1, 'EXPENSE', 9.00, 20.00, 'WITHDRAW', '提现', 'withdrawal_1', '2026-05-09 15:58:04');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (2, 3, 'INCOME', 1.00, 1.00, 'PAYOUT_RELEASE', '售书收入到账', 'payout_3', '2026-05-09 16:10:48');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (3, 1, 'INCOME', 12.00, 32.00, 'PAYOUT_RELEASE', '售书收入到账', 'payout_2', '2026-05-09 16:11:08');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (4, 3, 'INCOME', 11.00, 12.00, 'PAYOUT_RELEASE', '售书收入到账', 'payout_4', '2026-05-09 16:46:00');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (5, 1, 'EXPENSE', 11.00, 21.00, 'ORDER_PAY', '购买《Hi》', 'order_7', '2026-05-09 18:28:01');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (6, 3, 'INCOME', 11.00, 23.00, 'PAYOUT_RELEASE', '售书收入到账', 'payout_6', '2026-05-09 19:25:44');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (7, 1, 'INCOME', 18.00, 39.00, 'PAYOUT_RELEASE', '售书收入到账', 'payout_5', '2026-05-09 19:27:45');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (8, 1, 'EXPENSE', 14.00, 25.00, 'ORDER_PAY', '购买《Hi》', 'order_8', '2026-05-09 19:28:19');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (9, 3, 'INCOME', 11.00, 34.00, 'PAYOUT_RELEASE', '售书收入到账', 'payout_7', '2026-05-09 19:47:15');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (10, 1, 'INCOME', 27.00, 52.00, 'PAYOUT_RELEASE', '售书收入到账', 'payout_9', '2026-05-09 20:49:47');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (11, 3, 'INCOME', 36.00, 70.00, 'PAYOUT_RELEASE', '售书收入到账', 'payout_8', '2026-05-09 20:51:42');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (12, 2, 'EXPENSE', 3.00, 16.00, 'RETURN_DELIVERY_FEE', '还书驿站服务费', 'rental_4', '2026-05-09 21:48:25');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (13, 2, 'INCOME', 10.00, 26.00, 'RENTAL_DEPOSIT_REFUND', '租赁押金退还', 'rental_4', '2026-05-09 21:50:02');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (14, 1, 'INCOME', 2.00, 54.00, 'RENTAL_COMPENSATION', '租赁损坏赔偿', 'rental_4', '2026-05-09 21:50:02');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (15, 5, 'INCOME', 200.00, 200.00, 'REGISTER_BONUS', '注册赠送余额', '', '2026-05-09 22:08:35');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (16, 1, 'INCOME', 18.00, 72.00, 'RENTAL_DEPOSIT_REFUND', '租赁押金退还', 'rental_5', '2026-05-09 22:45:16');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (17, 5, 'INCOME', 12.00, 212.00, 'RENTAL_COMPENSATION', '租赁损坏赔偿', 'rental_5', '2026-05-09 22:45:16');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (18, 1, 'EXPENSE', 58.00, 14.00, 'ORDER_PAY', '购买《redBook》', 'order_13', '2026-05-09 22:48:57');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (19, 1, 'EXPENSE', 3.00, 11.00, 'RETURN_DELIVERY_FEE', '还书驿站服务费', 'rental_6', '2026-05-09 22:59:27');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (20, 1, 'INCOME', 18.00, 29.00, 'RENTAL_DEPOSIT_REFUND', '租赁押金退还', 'rental_6', '2026-05-09 23:00:27');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (21, 3, 'INCOME', 12.00, 82.00, 'RENTAL_COMPENSATION', '租赁损坏赔偿', 'rental_6', '2026-05-09 23:00:27');
INSERT INTO `wallet_transactions` (`id`, `user_id`, `type`, `amount`, `balance_after`, `category`, `title`, `reference_id`, `created_at`) VALUES (22, 6, 'INCOME', 2.00, 2.00, 'REGISTER_BONUS', '注册赠送余额', '', '2026-05-09 23:39:54');
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
