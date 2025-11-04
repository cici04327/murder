/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : murder

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 27/10/2025 16:29:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '优惠券名称',
  `type` tinyint NOT NULL COMMENT '类型：1满减，2折扣，3通用',
  `discount_type` tinyint NULL DEFAULT 1 COMMENT '优惠类型',
  `discount_value` decimal(10, 2) NOT NULL COMMENT '优惠值',
  `min_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '最低消费金额',
  `total_count` int NOT NULL COMMENT '发行总量',
  `received_count` int NULL DEFAULT 0 COMMENT '已领取数量',
  `used_count` int NULL DEFAULT 0 COMMENT '已使用数量',
  `valid_start` datetime NULL DEFAULT NULL COMMENT '有效期开始',
  `valid_end` datetime NULL DEFAULT NULL COMMENT '有效期结束',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon
-- ----------------------------
INSERT INTO `coupon` VALUES (1, '新用户专享券', 1, 1, 20.00, 100.00, 1000, 0, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', '首次注册用户专享，满100减20元', 1, 0, '2025-10-24 18:31:07', '2025-10-24 18:31:07');
INSERT INTO `coupon` VALUES (2, '满200减50', 1, 1, 50.00, 200.00, 800, 0, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', '全场通用，满200元立减50元', 1, 0, '2025-10-24 18:31:07', '2025-10-24 18:31:07');
INSERT INTO `coupon` VALUES (3, '全场8折券', 2, 2, 0.80, 0.00, 1000, 0, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', '无门槛8折优惠', 1, 0, '2025-10-24 18:31:07', '2025-10-24 18:31:07');
INSERT INTO `coupon` VALUES (4, '30元代金券', 3, 3, 30.00, 0.00, 2000, 0, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', '无门槛代金券', 1, 0, '2025-10-24 18:31:07', '2025-10-24 18:31:07');
INSERT INTO `coupon` VALUES (5, '50元代金券', 3, 3, 50.00, 0.00, 1000, 0, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', '无门槛代金券50元', 1, 0, '2025-10-24 18:31:07', '2025-10-24 18:31:07');
INSERT INTO `coupon` VALUES (6, '周末特惠券', 1, 1, 40.00, 150.00, 1000, 0, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', '周六周日可用', 1, 0, '2025-10-24 18:31:07', '2025-10-24 18:31:07');
INSERT INTO `coupon` VALUES (7, '会员专享85折', 2, 2, 0.85, 100.00, 600, 0, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', '会员专属85折', 1, 0, '2025-10-24 18:31:07', '2025-10-24 18:31:07');
INSERT INTO `coupon` VALUES (8, '满300减80', 1, 1, 80.00, 300.00, 500, 0, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 'VIP会员专享', 1, 0, '2025-10-24 18:31:07', '2025-10-24 18:31:07');
INSERT INTO `coupon` VALUES (9, '生日专享券', 1, 1, 66.00, 150.00, 500, 0, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', '生日当月可用', 1, 0, '2025-10-24 18:31:07', '2025-10-24 18:31:07');
INSERT INTO `coupon` VALUES (10, '100元代金券', 3, 3, 100.00, 0.00, 300, 0, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', '超值代金券', 1, 0, '2025-10-24 18:31:07', '2025-10-24 18:31:07');

-- ----------------------------
-- Table structure for reservation
-- ----------------------------
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reservation_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预约编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `store_id` bigint NOT NULL COMMENT '门店ID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `script_id` bigint NOT NULL COMMENT '剧本ID',
  `player_count` int NOT NULL COMMENT '玩家人数',
  `reservation_time` datetime NOT NULL COMMENT '预约时间',
  `duration` decimal(3, 1) NULL DEFAULT NULL COMMENT '预计时长（小时）',
  `total_price` decimal(10, 2) NOT NULL COMMENT '总价格',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1待确认，2已确认，3已完成，4已取消',
  `pay_status` tinyint NULL DEFAULT 0 COMMENT '支付状态：0未支付，1已支付',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_reservation_no`(`reservation_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_store_id`(`store_id` ASC) USING BTREE,
  INDEX `idx_reservation_time`(`reservation_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reservation
-- ----------------------------
INSERT INTO `reservation` VALUES (1, 'R202410210001', 1, 1, 1, 1, 6, '2024-10-15 14:00:00', 3.5, 168.00, '张三', '13800138000', 3, 1, '2024-10-14 10:30:00', 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57');
INSERT INTO `reservation` VALUES (2, 'R202410210002', 2, 1, 3, 4, 8, '2024-10-16 18:00:00', 4.0, 198.00, '李四', '13900139000', 3, 1, '2024-10-15 15:20:00', 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57');
INSERT INTO `reservation` VALUES (3, 'R202410210003', 3, 2, 5, 7, 6, '2024-10-17 15:00:00', 3.5, 178.00, '王小明', '13700137000', 3, 1, '2024-10-16 11:00:00', 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57');
INSERT INTO `reservation` VALUES (4, 'R202410210004', 4, 3, 9, 2, 7, '2024-10-22 14:00:00', 4.0, 198.00, '刘芳', '13600136000', 2, 1, '2024-10-20 09:30:00', 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57');
INSERT INTO `reservation` VALUES (5, 'R202410210005', 5, 2, 8, 14, 10, '2024-10-22 19:00:00', 5.0, 228.00, '陈强', '13500135000', 2, 1, '2024-10-20 14:15:00', 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57');
INSERT INTO `reservation` VALUES (6, 'R202410210006', 6, 4, 12, 17, 8, '2024-10-23 15:00:00', 4.5, 208.00, '赵敏', '13400134000', 2, 1, '2024-10-21 10:00:00', 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57');
INSERT INTO `reservation` VALUES (7, 'R202410210007', 7, 5, 15, 20, 5, '2024-10-24 14:00:00', 2.5, 118.00, '孙悦', '13300133000', 1, 1, '2024-10-21 16:30:00', 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57');
INSERT INTO `reservation` VALUES (8, 'R202410210008', 8, 6, 18, 1, 6, '2024-10-25 19:00:00', 3.5, 168.00, '周杰', '13200132000', 1, 1, '2024-10-21 17:45:00', 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57');
INSERT INTO `reservation` VALUES (9, 'R202410210009', 9, 1, 2, 8, 5, '2024-10-26 14:00:00', 3.0, 138.00, '吴倩', '13100131000', 1, 0, NULL, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57');
INSERT INTO `reservation` VALUES (10, 'R202410210010', 10, 2, 6, 21, 6, '2024-10-27 15:30:00', 2.5, 108.00, '郑伟', '13000130000', 1, 0, NULL, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57');

-- ----------------------------
-- Table structure for reservation_order
-- ----------------------------
DROP TABLE IF EXISTS `reservation_order`;
CREATE TABLE `reservation_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `store_id` bigint NULL DEFAULT NULL COMMENT '门店ID',
  `room_id` bigint NULL DEFAULT NULL COMMENT '房间ID',
  `script_id` bigint NULL DEFAULT NULL COMMENT '剧本ID',
  `player_count` int NULL DEFAULT NULL COMMENT '预约人数',
  `reservation_time` datetime NULL DEFAULT NULL COMMENT '预约时间',
  `duration` decimal(6, 2) NULL DEFAULT NULL COMMENT '预约时长（小时）',
  `total_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '总价格',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NULL DEFAULT 1 COMMENT '预约状态：1待确认，2已确认，3已完成，4已取消',
  `pay_status` tinyint NULL DEFAULT 0 COMMENT '支付状态：0未支付，1已支付，2已退款',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：1删除，0未删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `coupon_id` bigint NULL DEFAULT NULL COMMENT '用户优惠券ID',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `actual_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '实际支付金额',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预约订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reservation_order
-- ----------------------------
INSERT INTO `reservation_order` VALUES (1, 'R202501010001', 1, 1, 1, 1, 6, '2025-10-24 10:19:07', 3.50, 168.00, '测试用户A', '13800138000', NULL, 2, 1, NULL, 0, '2025-10-23 10:19:07', '2025-10-23 10:19:07', NULL, 0.00, 0.00);
INSERT INTO `reservation_order` VALUES (2, 'R202510260001', NULL, 5, 17, 1, 6, '2025-10-30 00:00:00', NULL, NULL, '123', '12321321321321', '1', 4, 0, NULL, 0, NULL, NULL, NULL, 0.00, 0.00);
INSERT INTO `reservation_order` VALUES (3, 'R202510270001', 1, 1, 1, 1, 6, '2025-10-28 14:00:00', 3.00, 168.00, '????', '13800138000', NULL, 1, 0, NULL, 0, NULL, NULL, NULL, 0.00, 168.00);

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reservation_id` bigint NOT NULL COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `store_id` bigint NOT NULL COMMENT '门店ID',
  `script_id` bigint NOT NULL COMMENT '剧本ID',
  `overall_rating` tinyint NOT NULL COMMENT '综合评分：1-5星',
  `store_rating` tinyint NULL DEFAULT NULL COMMENT '门店评分',
  `script_rating` tinyint NULL DEFAULT NULL COMMENT '剧本评分',
  `service_rating` tinyint NULL DEFAULT NULL COMMENT '服务评分',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价图片（多张用逗号分隔）',
  `tags` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '璇勪环鏍囩',
  `status` tinyint NULL DEFAULT 1,
  `is_anonymous` tinyint NULL DEFAULT 0,
  `is_featured` tinyint NULL DEFAULT 0,
  `reply_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `reply_time` datetime NULL DEFAULT NULL,
  `reward_points` int NULL DEFAULT 0,
  `reward_coupon_id` bigint NULL DEFAULT NULL,
  `audit_user_id` bigint NULL DEFAULT NULL,
  `audit_time` datetime NULL DEFAULT NULL,
  `audit_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_reservation_id`(`reservation_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_store_id`(`store_id` ASC) USING BTREE,
  INDEX `idx_script_id`(`script_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '综合评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review
-- ----------------------------
INSERT INTO `review` VALUES (1, 1, 1, 1, 1, 5, 5, 5, 5, 'Great experience!', '', 'good,excellent', 2, 0, 1, NULL, NULL, 530, NULL, NULL, NULL, NULL, 0, '2025-10-25 16:54:17', NULL);
INSERT INTO `review` VALUES (2, 1, 2, 1, 1, 4, 4, 5, 4, 'Nice script!', '', 'good', 2, 0, 0, NULL, NULL, 20, NULL, NULL, NULL, NULL, 0, '2025-10-25 16:54:17', '2025-10-25 16:54:17');

-- ----------------------------
-- Table structure for script
-- ----------------------------
DROP TABLE IF EXISTS `script`;
CREATE TABLE `script`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '剧本名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '剧本简介',
  `type` tinyint NOT NULL COMMENT '类型：1恐怖，2推理，3情感，4欢乐，5机制，6还原',
  `difficulty` tinyint NULL DEFAULT 2 COMMENT '难度：1简单，2普通，3困难，4硬核',
  `player_count` int NOT NULL COMMENT '玩家人数',
  `duration` decimal(3, 1) NULL DEFAULT NULL COMMENT '游戏时长（小时）',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `tags` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签（逗号分隔）',
  `rating` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '评分',
  `is_exclusive` tinyint NULL DEFAULT 0 COMMENT '是否独家：1是，0否',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1上架，0下架',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cover` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'script detail images',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '剧本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of script
-- ----------------------------
INSERT INTO `script` VALUES (1, '午夜凶铃', 1, '经典日式恐怖剧本，充满悬念与惊悚', 1, 2, 6, 3.5, 168.00, '恐怖,日式,悬疑', 4.70, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (2, '寂静岭', 1, '改编自经典游戏，浓雾笼罩的小镇隐藏着黑暗的秘密', 1, 3, 7, 4.0, 198.00, '恐怖,重口,烧脑', 4.80, 1, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (3, '午夜怪谈', 1, '都市传说背后的真相，胆小慎入', 1, 1, 5, 3.0, 148.00, '恐怖,都市,新手', 4.50, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (4, '东方快车谋杀案', 2, '阿加莎经典推理作品改编，每个人都有秘密', 2, 3, 8, 4.0, 198.00, '推理,经典,烧脑', 4.90, 1, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (5, '无人生还', 2, '孤岛杀人事件，凶手就在你们之中', 2, 3, 10, 4.5, 218.00, '推理,经典,硬核', 4.90, 1, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (6, '唐人街探案', 2, '中国风推理剧本，幽默与悬疑并存', 2, 2, 6, 3.5, 178.00, '推理,中式,有趣', 4.70, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (7, '白夜追凶', 2, '双生兄弟的复仇之路', 2, 3, 7, 4.0, 188.00, '推理,悬疑,刑侦', 4.80, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (8, '时光倒流', 3, '感人至深的情感治愈系剧本，关于遗憾与救赎', 3, 1, 5, 3.0, 138.00, '情感,治愈,泪点', 4.80, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (9, '云边有个小卖部', 3, '温馨的乡村故事，治愈系满分', 3, 1, 6, 3.5, 158.00, '情感,温馨,治愈', 4.90, 1, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (10, '再见，我的童年', 3, '关于成长的故事，每个人的青春都有遗憾', 3, 2, 6, 3.0, 148.00, '情感,青春,回忆', 4.70, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (11, '爆笑课堂', 4, '轻松欢乐的校园剧本，笑到肚子疼', 4, 1, 6, 2.5, 118.00, '欢乐,校园,新手友好', 4.50, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (12, '综艺大冒险', 4, '模拟综艺节目，欢乐互动多', 4, 1, 8, 3.0, 128.00, '欢乐,综艺,互动', 4.60, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (13, '欢乐喜剧人', 4, '各种搞笑桥段，适合聚会', 4, 1, 7, 2.5, 108.00, '欢乐,搞笑,聚会', 4.40, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (14, '时空迷城', 5, '高难度机制本，烧脑推理', 5, 4, 9, 5.0, 228.00, '机制,烧脑,硬核', 4.90, 1, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (15, '盗梦空间', 5, '多层梦境，复杂机制', 5, 4, 8, 5.5, 248.00, '机制,烧脑,科幻', 4.80, 1, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (16, '罗生门', 5, '多视角叙事', 5, 3, 7, 4.5, 198.00, '机制,叙诡,推理', 4.70, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (17, '大唐盛世', 6, '唐朝背景的高还原度剧本', 6, 3, 7, 4.5, 208.00, '还原,古风,沉浸', 4.80, 1, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (18, '民国风云', 6, '民国时期的上海滩', 6, 3, 8, 4.0, 198.00, '还原,民国,黑帮', 4.70, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (19, '清明上河图', 6, '北宋汴京的市井生活', 6, 2, 6, 3.5, 178.00, '还原,古风,生活', 4.60, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (20, '第一次', 7, '专为新手设计', 4, 1, 5, 2.0, 98.00, '新手,简单,入门', 4.50, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (21, '周末派对', 7, '轻松愉快', 4, 1, 6, 2.5, 108.00, '新手,聚会,欢乐', 4.40, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (22, '校园青春', 7, '校园题材', 3, 1, 5, 2.5, 118.00, '新手,校园,情感', 4.60, 0, 1, 0, '2025-10-21 11:12:57', '2025-10-21 11:12:57', NULL, NULL);
INSERT INTO `script` VALUES (23, '雾都迷案', 2, '英伦推理', 2, 4, 7, 4.0, 198.00, '推理,悬疑', 4.80, 0, 1, 0, '2025-10-23 10:19:07', '2025-10-23 10:19:07', NULL, NULL);
INSERT INTO `script` VALUES (24, '岁月回声', 3, '情感治愈', 3, 2, 6, 3.0, 158.00, '情感,治愈', 4.60, 0, 1, 0, '2025-10-23 10:19:07', '2025-10-23 10:19:07', NULL, NULL);

-- ----------------------------
-- Table structure for script_category
-- ----------------------------
DROP TABLE IF EXISTS `script_category`;
CREATE TABLE `script_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_category_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '剧本分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of script_category
-- ----------------------------
INSERT INTO `script_category` VALUES (1, '恐怖惊悚', '充满恐怖氛围和惊悚元素的剧本，适合喜欢刺激的玩家', 1, 1, 0, '2025-10-21 11:12:56', '2025-10-23 11:08:21');
INSERT INTO `script_category` VALUES (2, '推理悬疑', '注重逻辑推理和线索分析的剧本，适合烧脑玩家', 2, 1, 0, '2025-10-21 11:12:56', '2025-10-23 11:08:23');
INSERT INTO `script_category` VALUES (3, '情感治愈', '注重角色情感和故事体验的剧本，适合感性玩家', 3, 1, 0, '2025-10-21 11:12:56', '2025-10-23 11:08:23');
INSERT INTO `script_category` VALUES (4, '欢乐搞笑', '轻松愉快，充满欢笑的剧本，适合休闲娱乐', 4, 1, 0, '2025-10-21 11:12:56', '2025-10-23 11:08:21');
INSERT INTO `script_category` VALUES (5, '机制硬核', '机制复杂，玩法硬核的剧本，适合资深玩家', 5, 1, 0, '2025-10-21 11:12:56', '2025-10-23 11:08:22');
INSERT INTO `script_category` VALUES (6, '还原沉浸', '高度还原故事背景，沉浸式体验的剧本', 6, 1, 0, '2025-10-21 11:12:56', '2025-10-23 11:08:22');
INSERT INTO `script_category` VALUES (7, '新手友好', '简单易懂，适合新手玩家的入门剧本', 7, 1, 0, '2025-10-21 11:12:56', '2025-10-23 11:08:22');
INSERT INTO `script_category` VALUES (8, '独家首发', '独家首发的精品剧本，仅在本平台提供', 8, 1, 0, '2025-10-21 11:12:56', '2025-10-23 11:08:23');

-- ----------------------------
-- Table structure for script_review
-- ----------------------------
DROP TABLE IF EXISTS `script_review`;
CREATE TABLE `script_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `script_id` bigint NOT NULL COMMENT '剧本ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `reservation_id` bigint NULL DEFAULT NULL COMMENT '预约ID',
  `rating` tinyint NOT NULL COMMENT '评分：1-5星',
  `story_rating` tinyint NULL DEFAULT NULL COMMENT '剧情评分',
  `difficulty_rating` tinyint NULL DEFAULT NULL COMMENT '难度评分',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_script_id`(`script_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '剧本评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of script_review
-- ----------------------------
INSERT INTO `script_review` VALUES (1, 1, 1, 1, 5, 5, 4, 'Amazing script! Very immersive.', 0, '2025-10-25 17:07:00', '2025-10-25 17:07:00');
INSERT INTO `script_review` VALUES (2, 1, 2, 1, 4, 4, 3, 'Good story, moderate difficulty.', 0, '2025-10-25 17:07:00', '2025-10-25 17:07:00');
INSERT INTO `script_review` VALUES (3, 2, 3, 1, 3, 3, 5, 'Too difficult for beginners.', 0, '2025-10-25 17:07:00', '2025-10-25 17:07:00');

-- ----------------------------
-- Table structure for script_role
-- ----------------------------
DROP TABLE IF EXISTS `script_role`;
CREATE TABLE `script_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `script_id` bigint NOT NULL COMMENT '剧本ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色头像',
  `character_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色立绘',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：1男，2女，3不限',
  `age_range` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年龄范围',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '角色描述',
  `difficulty` tinyint NULL DEFAULT 1 COMMENT '难度：1简单，2普通，3困难',
  `is_murderer` tinyint NULL DEFAULT 0 COMMENT '是否凶手：1是，0否',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_script_id`(`script_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 162 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '剧本角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of script_role
-- ----------------------------
INSERT INTO `script_role` VALUES (1, 1, '灵媒师', NULL, NULL, 2, '25-35', '能够感知灵异现象的神秘女子，似乎知道一些不为人知的秘密', 2, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (2, 1, '记者', NULL, NULL, 1, '28-35', '调查诅咒真相的新闻记者，理性且执着', 2, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (3, 1, '大学生', NULL, NULL, 2, '20-25', '无意中触发诅咒的年轻女孩，陷入恐慌', 1, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (4, 1, '警察', NULL, NULL, 1, '30-40', '负责调查离奇死亡案件的刑警，不相信超自然现象', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (5, 1, '心理医生', NULL, NULL, 1, '35-45', '试图用科学解释诅咒现象的心理学专家', 2, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (6, 1, '神秘人', NULL, NULL, 3, '不限', '了解诅咒起源的关键人物，身份成谜', 3, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (7, 2, '母亲', NULL, NULL, 2, '30-40', '为了寻找失踪的女儿来到寂静岭', 2, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (8, 2, '警官', NULL, NULL, 1, '35-45', '追踪母亲来到小镇的警察，逐渐发现真相', 2, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (9, 2, '护士', NULL, NULL, 2, '25-35', '医院的幸存者，知道一些小镇的秘密', 2, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (10, 2, '教徒', NULL, NULL, 1, '40-50', '狂热的宗教信徒，认为这是神的审判', 3, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (11, 2, '幸存者', NULL, NULL, 2, '20-30', '困在小镇多年的女孩，精神濒临崩溃', 2, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (12, 2, '调查员', NULL, NULL, 1, '30-40', '研究小镇历史的超自然现象调查员', 3, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (13, 2, '孩子', NULL, NULL, 3, '8-12', '失踪女儿的关键线索，真相的核心', 2, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (14, 3, '主播', NULL, NULL, 2, '22-28', '探险类主播，为了流量来探索灵异地点', 1, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (15, 3, '摄影师', NULL, NULL, 1, '25-30', '主播的搭档，负责拍摄记录', 1, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (16, 3, '看门人', NULL, NULL, 1, '50-60', '废弃建筑的守夜人，知道很多往事', 2, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (17, 3, '灵异爱好者', NULL, NULL, 2, '20-28', '相信鬼神的年轻人，主动加入探险', 1, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (18, 3, '失踪者家属', NULL, NULL, 1, '30-40', '为了寻找真相而来，背负着沉重的过去', 2, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (19, 4, '大侦探波洛', NULL, NULL, 1, '40-50', '世界知名的比利时侦探，智慧超群', 3, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (20, 4, '美国富豪', NULL, NULL, 1, '45-55', '神秘的美国商人，被害者', 2, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (21, 4, '俄国公爵夫人', NULL, NULL, 2, '50-60', '高贵优雅的贵族，隐藏着秘密', 2, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (22, 4, '英国军官', NULL, NULL, 1, '35-45', '退役军人，行事果断', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (23, 4, '瑞典女教师', NULL, NULL, 2, '30-40', '温柔善良的教师，似乎与案件有关', 2, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (24, 4, '意大利商人', NULL, NULL, 1, '40-50', '热情健谈的生意人', 2, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (25, 4, '德国女仆', NULL, NULL, 2, '25-35', '谨慎的贴身女仆，观察力敏锐', 2, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (26, 4, '匈牙利伯爵', NULL, NULL, 1, '45-55', '彬彬有礼的伯爵，过去不为人知', 3, 0, 8, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (27, 5, '法官', NULL, NULL, 1, '55-65', '退休法官，道貌岸然', 3, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (28, 5, '医生', NULL, NULL, 1, '40-50', '军医出身，沉着冷静', 2, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (29, 5, '将军', NULL, NULL, 1, '60-70', '退役将军，威严但内疚', 2, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (30, 5, '女秘书', NULL, NULL, 2, '25-30', '年轻的秘书，似乎很无辜', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (31, 5, '雇佣兵', NULL, NULL, 1, '30-40', '冷酷的职业杀手', 3, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (32, 5, '女演员', NULL, NULL, 2, '28-35', '过气女明星，虚荣心强', 2, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (33, 5, '警探', NULL, NULL, 1, '35-45', '前警探，现在是私家侦探', 3, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (34, 5, '老处女', NULL, NULL, 2, '50-60', '虔诚的宗教徒，严肃古板', 2, 0, 8, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (35, 5, '花花公子', NULL, NULL, 1, '28-35', '年轻的纨绔子弟，玩世不恭', 2, 0, 9, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (36, 5, '管家夫妇', NULL, NULL, 3, '45-55', '岛上的管家，行为诡异', 2, 0, 10, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (37, 6, '秦风', NULL, NULL, 1, '20-25', '天才少年侦探，推理能力超群', 3, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (38, 6, '唐仁', NULL, NULL, 1, '35-45', '搞笑的冒牌侦探，实则有独特智慧', 2, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (39, 6, '警察局长', NULL, NULL, 1, '45-55', '泰国华裔警察，正直严肃', 2, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (40, 6, '黑帮老大', NULL, NULL, 1, '50-60', '唐人街的地下势力头目', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (41, 6, '女医生', NULL, NULL, 2, '28-35', '法医，协助破案的关键人物', 2, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (42, 6, '富商之女', NULL, NULL, 2, '22-28', '案件受害者的女儿，寻找真相', 1, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (43, 7, '关宏峰', NULL, NULL, 1, '35-45', '刑警队长，冷静理智，为兄弟洗冤', 3, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (44, 7, '关宏宇', NULL, NULL, 1, '35-45', '双胞胎弟弟，被指控杀人', 3, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (45, 7, '周巡', NULL, NULL, 1, '28-35', '年轻刑警，关宏峰的搭档', 2, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (46, 7, '女法医', NULL, NULL, 2, '30-38', '技术精湛的法医，暗恋关宏峰', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (47, 7, '局长', NULL, NULL, 1, '50-60', '公安局长，案件的关键人物', 2, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (48, 7, '神秘女人', NULL, NULL, 2, '28-35', '与案件有关的神秘女子', 3, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (49, 7, '刑警老吴', NULL, NULL, 1, '45-55', '资深刑警，经验丰富', 2, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (50, 8, '男主角', NULL, NULL, 1, '25-35', '获得时光倒流能力的普通人，想要改变过去', 2, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (51, 8, '女主角', NULL, NULL, 2, '23-30', '男主角深爱的人，命运多舛', 2, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (52, 8, '最好的朋友', NULL, NULL, 1, '25-35', '陪伴主角的挚友，见证所有改变', 1, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (53, 8, '父亲', NULL, NULL, 1, '50-60', '父子关系紧张，是遗憾的源头之一', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (54, 8, '神秘老人', NULL, NULL, 3, '70-80', '给予时光倒流能力的人，暗示代价', 1, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (55, 9, '刘十三', NULL, NULL, 1, '25-30', '离家多年的年轻人，回乡陪伴外婆', 2, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (56, 9, '外婆', NULL, NULL, 2, '70-80', '善良坚强的老人，经营小卖部', 1, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (57, 9, '程霜', NULL, NULL, 2, '23-28', '青梅竹马的女孩，纯真美好', 2, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (58, 9, '村长', NULL, NULL, 1, '55-65', '和蔼的村长，见证了村庄的变迁', 1, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (59, 9, '小学老师', NULL, NULL, 2, '30-40', '坚守在乡村的女教师', 1, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (60, 9, '发小', NULL, NULL, 1, '25-30', '一起长大的伙伴，各自有不同人生', 1, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (61, 10, '成年的我', NULL, NULL, 1, '30-35', '回忆童年往事的主人公', 2, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (62, 10, '童年的我', NULL, NULL, 1, '10-12', '天真烂漫的孩子', 1, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (63, 10, '母亲', NULL, NULL, 2, '35-45', '辛苦工作养家的母亲', 2, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (64, 10, '初恋', NULL, NULL, 2, '30-35', '青春期的美好回忆', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (65, 10, '老师', NULL, NULL, 2, '40-50', '影响一生的恩师', 1, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (66, 10, '童年好友', NULL, NULL, 1, '30-35', '一起长大后分道扬镳的朋友', 2, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (67, 11, '学霸', NULL, NULL, 2, '18-22', '成绩优异但社交障碍的学霸', 1, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (68, 11, '学渣', NULL, NULL, 1, '18-22', '成绩垫底但搞笑天赋满分', 1, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (69, 11, '班长', NULL, NULL, 2, '18-22', '负责任的班长，常常被整蛊', 1, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (70, 11, '体育委员', NULL, NULL, 1, '18-22', '头脑简单四肢发达的运动健将', 1, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (71, 11, '新来的转学生', NULL, NULL, 2, '18-22', '神秘的转学生，引发各种搞笑事件', 1, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (72, 11, '数学老师', NULL, NULL, 1, '40-50', '严厉但内心温柔的老师', 1, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (73, 12, '主持人', NULL, NULL, 1, '30-40', '幽默风趣的综艺主持', 1, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (74, 12, '歌手', NULL, NULL, 2, '25-30', '人气歌手，综艺感十足', 1, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (75, 12, '演员', NULL, NULL, 1, '28-35', '实力派演员，不太适应综艺', 1, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (76, 12, '网红', NULL, NULL, 2, '22-28', '流量网红，擅长炒作', 1, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (77, 12, '运动员', NULL, NULL, 1, '25-30', '退役运动员，体能担当', 1, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (78, 12, '喜剧人', NULL, NULL, 1, '35-45', '专业喜剧演员，负责搞笑', 1, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (79, 12, '女主持', NULL, NULL, 2, '28-35', '美貌与智慧并存的女主持', 1, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (80, 12, '神秘嘉宾', NULL, NULL, 3, '不限', '突然出现的特别嘉宾', 1, 0, 8, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (81, 13, '老牌喜剧人', NULL, NULL, 1, '45-55', '经验丰富的喜剧演员', 1, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (82, 13, '新人喜剧演员', NULL, NULL, 1, '22-28', '年轻有活力的新人', 1, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (83, 13, '搭档A', NULL, NULL, 2, '30-40', '配合默契的女搭档', 1, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (84, 13, '搭档B', NULL, NULL, 1, '30-40', '耍宝担当', 1, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (85, 13, '导演', NULL, NULL, 1, '40-50', '严格的喜剧导演', 1, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (86, 13, '编剧', NULL, NULL, 2, '28-35', '脑洞大开的编剧', 1, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (87, 13, '观众代表', NULL, NULL, 3, '20-50', '参与互动的观众', 1, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (88, 14, '时间管理者', NULL, NULL, 1, '35-45', '掌控时间流动的关键人物', 4, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (89, 14, '物理学家', NULL, NULL, 1, '40-50', '研究时空理论的科学家', 3, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (90, 14, '穿越者A', NULL, NULL, 2, '25-35', '来自未来的女性', 3, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (91, 14, '穿越者B', NULL, NULL, 1, '28-38', '来自过去的男性', 3, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (92, 14, '守护者', NULL, NULL, 2, '30-40', '保护时间线的守护者', 4, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (93, 14, '破坏者', NULL, NULL, 1, '30-40', '试图改变历史的人', 4, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (94, 14, '观察者', NULL, NULL, 3, '不限', '记录时间线变化的存在', 3, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (95, 14, '平行世界的我', NULL, NULL, 2, '25-35', '来自平行时空的自己', 3, 0, 8, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (96, 14, '时间之子', NULL, NULL, 3, '18-25', '天生拥有特殊能力的年轻人', 4, 0, 9, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (97, 15, '造梦师', NULL, NULL, 1, '35-45', '构建梦境世界的大师', 4, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (98, 15, '盗梦者', NULL, NULL, 1, '30-40', '窃取潜意识秘密的专家', 4, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (99, 15, '筑梦师', NULL, NULL, 2, '28-35', '设计梦境结构的建筑师', 3, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (100, 15, '药剂师', NULL, NULL, 2, '30-40', '调配梦境药剂的化学家', 3, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (101, 15, '伪装者', NULL, NULL, 1, '28-38', '在梦中扮演各种角色', 3, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (102, 15, '守卫者', NULL, NULL, 1, '30-40', '保护梦境不被入侵', 3, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (103, 15, '目标', NULL, NULL, 2, '40-50', '需要植入想法的目标人物', 3, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (104, 15, '投影', NULL, NULL, 3, '不限', '潜意识的具象化投影', 4, 0, 8, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (105, 16, '武士', NULL, NULL, 1, '30-40', '被杀的武士，讲述自己版本的故事', 3, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (106, 16, '武士之妻', NULL, NULL, 2, '25-30', '美丽的妻子，各方都想要', 3, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (107, 16, '强盗', NULL, NULL, 1, '28-35', '被指控的凶手', 3, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (108, 16, '樵夫', NULL, NULL, 1, '40-50', '发现尸体的目击者', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (109, 16, '僧人', NULL, NULL, 1, '50-60', '听取各方证词的僧人', 2, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (110, 16, '差役', NULL, NULL, 1, '35-45', '负责审理案件的官差', 2, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (111, 16, '灵媒', NULL, NULL, 2, '45-55', '通灵召唤死者的女巫', 3, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (112, 17, '李白', NULL, NULL, 1, '30-40', '诗仙李白，豪放不羁', 3, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (113, 17, '杨贵妃', NULL, NULL, 2, '25-30', '绝世美人，深受宠爱', 2, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (114, 17, '安禄山', NULL, NULL, 1, '45-55', '手握重兵的节度使', 3, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (115, 17, '高力士', NULL, NULL, 1, '40-50', '唐玄宗身边的大太监', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (116, 17, '王维', NULL, NULL, 1, '35-45', '诗佛王维，淡泊名利', 2, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (117, 17, '公主', NULL, NULL, 2, '20-25', '唐玄宗之女，才华横溢', 2, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (118, 17, '将军', NULL, NULL, 1, '35-45', '忠心耿耿的禁军将领', 2, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (119, 18, '黑帮老大', NULL, NULL, 1, '40-50', '上海滩的风云人物', 3, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (120, 18, '交际花', NULL, NULL, 2, '25-30', '游走于各方势力的美女', 3, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (121, 18, '革命党', NULL, NULL, 1, '28-35', '秘密进行革命活动', 3, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (122, 18, '军阀', NULL, NULL, 1, '45-55', '手握实权的地方军阀', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (123, 18, '商人', NULL, NULL, 1, '40-50', '富甲一方的实业家', 2, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (124, 18, '留学生', NULL, NULL, 2, '22-28', '从海外归来的新女性', 2, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (125, 18, '警察局长', NULL, NULL, 1, '40-50', '租界警察局长', 2, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (126, 18, '戏子', NULL, NULL, 2, '20-28', '红遍上海滩的名角', 2, 0, 8, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (127, 19, '画师', NULL, NULL, 1, '30-40', '描绘汴京繁华的画师', 2, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (128, 19, '商人', NULL, NULL, 1, '35-45', '经营茶楼的富商', 2, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (129, 19, '官员', NULL, NULL, 1, '40-50', '朝廷命官，清正廉洁', 2, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (130, 19, '才女', NULL, NULL, 2, '22-28', '琴棋书画样样精通', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (131, 19, '捕快', NULL, NULL, 1, '28-35', '开封府捕快，破案能手', 2, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (132, 19, '歌伎', NULL, NULL, 2, '20-25', '色艺双绝的歌伎', 1, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (133, 20, '小白', NULL, NULL, 1, '20-25', '第一次玩剧本杀的新手', 1, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (134, 20, '向导', NULL, NULL, 2, '22-28', '热心的老玩家，负责带新人', 1, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (135, 20, '搞笑担当', NULL, NULL, 1, '25-30', '活跃气氛的开心果', 1, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (136, 20, '解谜高手', NULL, NULL, 2, '23-28', '擅长解谜的玩家', 1, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (137, 20, '划水党', NULL, NULL, 1, '22-28', '就是来玩的佛系玩家', 1, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (138, 21, '派对主持', NULL, NULL, 2, '25-30', '组织派对的热心人', 1, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (139, 21, '调酒师', NULL, NULL, 1, '28-35', '负责调酒的专业人士', 1, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (140, 21, '音乐DJ', NULL, NULL, 1, '22-28', '掌控派对节奏的DJ', 1, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (141, 21, '社交达人', NULL, NULL, 2, '25-30', '认识所有人的社交能手', 1, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (142, 21, '宅男', NULL, NULL, 1, '23-28', '被朋友拉来的宅男', 1, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (143, 21, '闺蜜团', NULL, NULL, 2, '22-28', '结伴而来的好闺蜜', 1, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (144, 22, '班草', NULL, NULL, 1, '18-20', '阳光帅气的校园男神', 1, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (145, 22, '校花', NULL, NULL, 2, '18-20', '美丽善良的校花', 1, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (146, 22, '学生会主席', NULL, NULL, 2, '19-21', '能力出众的学生会主席', 1, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (147, 22, '篮球队长', NULL, NULL, 1, '19-21', '运动万能的篮球队长', 1, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (148, 22, '文艺委员', NULL, NULL, 2, '18-20', '多才多艺的文艺少女', 1, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (149, 23, '大侦探', NULL, NULL, 1, '35-45', '著名的私家侦探，推理能力超凡', 3, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (150, 23, '贵族', NULL, NULL, 1, '40-50', '神秘的贵族，案件受害者', 2, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (151, 23, '管家', NULL, NULL, 1, '50-60', '服务多年的老管家，知晓许多秘密', 2, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (152, 23, '千金小姐', NULL, NULL, 2, '22-28', '贵族之女，与受害者关系复杂', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (153, 23, '律师', NULL, NULL, 1, '38-48', '精明的律师，掌握重要情报', 3, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (154, 23, '女仆长', NULL, NULL, 2, '35-45', '庄园的女仆长，观察入微', 2, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (155, 23, '医生', NULL, NULL, 1, '40-50', '家庭医生，最早发现案件', 2, 0, 7, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (156, 24, '老人', NULL, NULL, 1, '70-80', '即将离世的老人，回顾一生', 2, 0, 1, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (157, 24, '中年的自己', NULL, NULL, 1, '45-55', '事业有成但内心空虚', 2, 0, 2, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (158, 24, '青年的自己', NULL, NULL, 1, '25-35', '意气风发的青年时期', 2, 0, 3, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (159, 24, '初恋情人', NULL, NULL, 2, '70-80', '相伴一生的伴侣', 2, 0, 4, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (160, 24, '儿子', NULL, NULL, 1, '40-50', '事业繁忙的儿子，陪伴父亲最后时光', 1, 0, 5, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');
INSERT INTO `script_role` VALUES (161, 24, '孙女', NULL, NULL, 2, '18-22', '懂事的孙女，聆听爷爷的故事', 1, 0, 6, 0, '2025-10-27 11:07:37', '2025-10-27 11:07:37');

-- ----------------------------
-- Table structure for store
-- ----------------------------
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '门店名称',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '门店地址',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '门店描述',
  `open_time` time NULL DEFAULT NULL COMMENT '营业开始时间',
  `close_time` time NULL DEFAULT NULL COMMENT '营业结束时间',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `rating` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '评分',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1营业，0停业',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '门店表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of store
-- ----------------------------
INSERT INTO `store` VALUES (1, '探案馆·人民广场店', '上海市黄浦区南京东路100号新世界大厦3楼', '021-12345678', '上海首家沉浸式剧本杀体验店，拥有专业DM团队和精美布景', '10:00:00', '23:00:00', 121.473701, 31.231706, 4.80, 1, 0, '2025-10-21 11:12:56', '2025-10-24 17:02:45');
INSERT INTO `store` VALUES (2, '悬疑社·静安寺店', '上海市静安区南京西路200号嘉里中心4楼', '021-87654321', '高端剧本杀娱乐空间，提供下午茶和晚餐服务', '11:00:00', '23:30:00', 121.445933, 31.227080, 4.90, 1, 0, '2025-10-21 11:12:56', '2025-10-24 17:02:45');
INSERT INTO `store` VALUES (3, '密室逃脱·徐家汇店', '上海市徐汇区虹桥路288号港汇广场5楼', '021-34567890', '集剧本杀和密室逃脱于一体的综合娱乐场所', '10:00:00', '22:00:00', 121.434420, 31.197646, 4.70, 1, 0, '2025-10-21 11:12:56', '2025-10-24 17:02:45');
INSERT INTO `store` VALUES (4, '剧本空间·陆家嘴店', '上海市浦东新区世纪大道100号环球金融中心6楼', '021-56789012', '面朝东方明珠的高端剧本杀会所，适合商务娱乐', '12:00:00', '23:59:59', 121.499860, 31.239880, 4.90, 1, 0, '2025-10-21 11:12:56', '2025-10-22 14:44:22');
INSERT INTO `store` VALUES (5, '推理馆·五角场店', '上海市杨浦区淞沪路8号五角场万达广场7楼', '021-67890123', '大学城附近的人气剧本杀店，学生优惠多多', '10:00:00', '22:30:00', 121.506720, 31.297570, 4.60, 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store` VALUES (6, '恐怖屋·中山公园店', '上海市长宁区长宁路777号龙之梦购物中心8楼', '021-78901234', '专注恐怖剧本的主题店，胆小勿入', '13:00:00', '23:00:00', 121.422590, 31.225870, 4.80, 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store` VALUES (7, '测试门店（已更新）', '上海市测试区测试路888号', '021-88888888', '这是一个更新后的测试门店', '10:00:00', '23:00:00', 121.500000, 31.200000, 4.50, 0, 1, '2025-10-22 15:15:36', '2025-10-22 15:15:36');
INSERT INTO `store` VALUES (8, '测试门店', '上海市测试区测试路888号', '021-99999999', '这是一个测试门店', '09:00:00', '22:00:00', 121.500000, 31.200000, 4.50, 1, 1, '2025-10-23 08:32:28', '2025-10-23 08:32:29');
INSERT INTO `store` VALUES (9, '测试门店', '上海市测试区测试路888号', '021-99999999', '这是一个测试门店', '09:00:00', '22:00:00', 121.500000, 31.200000, 4.50, 1, 1, '2025-10-24 17:02:45', '2025-10-24 17:02:45');

-- ----------------------------
-- Table structure for store_employee
-- ----------------------------
DROP TABLE IF EXISTS `store_employee`;
CREATE TABLE `store_employee`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `store_id` bigint NOT NULL COMMENT '门店ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '员工姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `position` tinyint NOT NULL COMMENT '职位：1店长，2副店长，3主持人，4服务员',
  `join_date` date NULL DEFAULT NULL COMMENT '入职日期',
  `salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '月薪',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1在职，0离职',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_store_id`(`store_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '门店员工表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of store_employee
-- ----------------------------
INSERT INTO `store_employee` VALUES (1, 1, '张经理（已更新）', '13800001001', 1, '2023-01-15', 8500.00, 0, 0, '2025-10-22 15:43:36', '2025-10-24 17:02:46');
INSERT INTO `store_employee` VALUES (2, 1, 'Li Deputy', '13800001002', 2, '2023-03-20', 6500.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (3, 1, 'Wang Host', '13800001003', 3, '2023-05-10', 5000.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (4, 1, 'Zhao Host', '13800001004', 3, '2023-06-15', 5000.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (5, 1, 'Qian Waiter', '13800001005', 4, '2023-07-01', 4000.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (6, 1, 'Sun Waiter', '13800001006', 4, '2023-08-01', 4000.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (7, 2, 'Zhou Manager', '13800002001', 1, '2022-12-01', 9000.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (8, 2, 'Wu Deputy', '13800002002', 2, '2023-02-15', 7000.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (9, 2, 'Zheng Host', '13800002003', 3, '2023-04-01', 5500.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (10, 2, 'Wang Host', '13800002004', 3, '2023-05-20', 5500.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (11, 2, 'Feng Host', '13800002005', 3, '2023-06-10', 5500.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (12, 2, 'Chen Waiter', '13800002006', 4, '2023-07-15', 4200.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (13, 2, 'Chu Waiter', '13800002007', 4, '2023-08-20', 4200.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (14, 3, 'Wei Manager', '13800003001', 1, '2023-02-01', 8500.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (15, 3, 'Jiang Deputy', '13800003002', 2, '2023-03-15', 6800.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (16, 3, 'Shen Host', '13800003003', 3, '2023-05-01', 5200.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (17, 3, 'Han Host', '13800003004', 3, '2023-06-01', 5200.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (18, 3, 'Yang Waiter', '13800003005', 4, '2023-07-10', 4100.00, 1, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_employee` VALUES (19, 1, '测试员工', '13800138888', 4, '2024-10-22', 4500.00, 1, 0, '2025-10-23 08:32:29', '2025-10-23 08:32:29');
INSERT INTO `store_employee` VALUES (20, 1, '测试员工', '13800138888', 4, '2024-10-22', 4500.00, 1, 0, '2025-10-24 17:02:46', '2025-10-24 17:02:46');

-- ----------------------------
-- Table structure for store_review
-- ----------------------------
DROP TABLE IF EXISTS `store_review`;
CREATE TABLE `store_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `store_id` bigint NOT NULL COMMENT '门店ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `reservation_id` bigint NULL DEFAULT NULL COMMENT '预约ID',
  `rating` tinyint NOT NULL COMMENT '总评分：1-5星',
  `environment_rating` tinyint NULL DEFAULT NULL COMMENT '环境评分',
  `service_rating` tinyint NULL DEFAULT NULL COMMENT '服务评分',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商家回复',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_store_id`(`store_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '门店评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of store_review
-- ----------------------------
INSERT INTO `store_review` VALUES (1, 1, 1, NULL, 5, 5, 5, 'Great environment and professional host!', '非常感谢您的好评，期待您的再次光临！', 0, '2025-10-22 15:43:36', '2025-10-24 17:02:46');
INSERT INTO `store_review` VALUES (2, 1, 2, NULL, 4, 4, 5, 'Good service, nice decoration.', 'oooooooooo', 0, '2025-10-22 15:43:36', '2025-10-22 15:47:12');
INSERT INTO `store_review` VALUES (3, 2, 3, NULL, 5, 5, 5, 'High-end atmosphere, delicious snacks!', 'Thank you for your support!', 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (4, 2, 4, NULL, 4, 4, 4, 'Overall good, but too crowded on weekends.', NULL, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (5, 3, 5, NULL, 4, 4, 4, 'Challenging escape room, fun script!', 'Thank you!', 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (6, 4, 6, NULL, 5, 5, 4, 'Great river view! Perfect for team building.', 'Welcome back!', 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (7, 5, 7, NULL, 4, 4, 4, 'Student discount is great, good value!', 'Thank you for your support!', 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (8, 6, 8, NULL, 5, 5, 5, 'Really scary! Atmosphere is amazing!', 'Haha, thank you brave one!', 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (9, 1, 999, NULL, 5, 5, 5, 'First time playing, great experience!', NULL, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (10, 2, 999, NULL, 4, 5, 4, 'Beautiful vintage decoration, great photos!', NULL, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (11, 3, 999, NULL, 5, 4, 5, 'Escape room is so exciting!', NULL, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (12, 4, 999, NULL, 4, 5, 4, 'Great view but expensive.', NULL, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (13, 5, 999, NULL, 5, 4, 5, 'Close to school, affordable!', NULL, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (14, 6, 999, NULL, 3, 3, 4, 'Good horror effects.', NULL, 0, '2025-10-22 15:43:36', '2025-10-22 15:43:36');
INSERT INTO `store_review` VALUES (15, 1, 1, NULL, 5, 5, 5, '非常好的体验，环境和服务都很棒！', NULL, 0, '2025-10-23 08:32:30', '2025-10-23 08:32:30');
INSERT INTO `store_review` VALUES (16, 1, 1, NULL, 5, 5, 5, '非常好的体验，环境和服务都很棒！', NULL, 0, '2025-10-24 17:02:46', '2025-10-24 17:02:46');

-- ----------------------------
-- Table structure for store_room
-- ----------------------------
DROP TABLE IF EXISTS `store_room`;
CREATE TABLE `store_room`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `store_id` bigint NOT NULL COMMENT '门店ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房间名称',
  `type` tinyint NOT NULL COMMENT '类型：1小房，2中房，3大房',
  `capacity` int NOT NULL COMMENT '容纳人数',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '房间描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1可用，0不可用',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_store_id`(`store_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of store_room
-- ----------------------------
INSERT INTO `store_room` VALUES (1, 1, '推理小房A（已更新）', 1, 5, '温馨舒适的小型房间，适合4-6人的推理剧本', 1, 0, '2025-10-21 11:12:56', '2025-10-24 17:02:47');
INSERT INTO `store_room` VALUES (2, 1, '推理小房B', 1, 4, '配备高清投影设备，适合新手玩家', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (3, 1, '恐怖中房', 2, 6, '氛围营造到位，适合6-8人的恐怖剧本', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (4, 1, '机制大房', 3, 10, '超大空间，适合8-12人的机制本', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (5, 2, '欢乐小房', 1, 4, '明亮宽敞，适合新手的轻松剧本', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (6, 2, '还原中房A', 2, 6, '古风装修，适合硬核玩家的还原本', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (7, 2, '还原中房B', 2, 6, '现代风格，配备专业音响系统', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (8, 2, 'VIP大房', 3, 10, '豪华包间，提供茶点和按摩椅', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (9, 3, '密室A', 1, 5, '机关密室，融合剧本杀元素', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (10, 3, '密室B', 2, 7, '高科技密室，沉浸式体验', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (11, 3, '剧本房', 2, 6, '传统剧本杀房间', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (12, 4, '商务厅A', 2, 8, '适合团建和商务活动', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (13, 4, '商务厅B', 2, 8, '配备会议设备', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (14, 4, '景观房', 3, 12, '270度江景，顶级体验', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (15, 5, '学生房A', 1, 5, '学生专享，价格优惠', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (16, 5, '学生房B', 1, 5, '适合小团体', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (17, 5, '普通房', 2, 6, '标准配置房间', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (18, 6, '恐怖小房', 1, 4, '适合4人恐怖本', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (19, 6, '惊悚中房', 2, 7, '重度恐怖剧本专用', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (20, 6, '鬼屋大房', 3, 10, '超大型恐怖场景', 1, 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `store_room` VALUES (21, 1, '测试房间', 2, 6, '测试房间描述', 1, 0, '2025-10-22 15:15:37', '2025-10-22 15:15:37');
INSERT INTO `store_room` VALUES (22, 1, '测试房间', 2, 6, '测试房间描述', 1, 0, '2025-10-23 08:32:30', '2025-10-23 08:32:30');
INSERT INTO `store_room` VALUES (23, 1, '测试房间', 2, 6, '测试房间描述', 1, 0, '2025-10-24 17:02:47', '2025-10-24 17:02:47');

-- ----------------------------
-- Table structure for system_notification
-- ----------------------------
DROP TABLE IF EXISTS `system_notification`;
CREATE TABLE `system_notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `type` tinyint NOT NULL COMMENT '通知类型：1预约成功，2预约提醒，3优惠券到期，4系统公告',
  `biz_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型：reservation, coupon, system',
  `biz_id` bigint NULL DEFAULT NULL COMMENT '业务ID',
  `target_type` tinyint NOT NULL COMMENT '目标类型：1全体用户，2指定用户',
  `target_users` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '目标用户ID列表（逗号分隔）',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1待发送，2已发送，3已取消',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：1删除，0未删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_biz`(`biz_type` ASC, `biz_id` ASC) USING BTREE,
  INDEX `idx_send_time`(`send_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_notification
-- ----------------------------
INSERT INTO `system_notification` VALUES (1, '预约成功通知', '您已成功预约《午夜凶铃》，预约时间：2025-10-26 14:00，请准时到场！', 1, 'reservation', 1, 2, '1', '2025-10-25 17:56:05', 2, 0, '2025-10-25 17:56:05', '2025-10-25 17:56:05');
INSERT INTO `system_notification` VALUES (2, '预约提醒', '温馨提醒：您预约的《寂静岭》将在2小时后开始，请准时到场！', 2, 'reservation', 2, 2, '2', '2025-10-25 17:56:05', 2, 0, '2025-10-25 17:56:05', '2025-10-25 17:56:05');
INSERT INTO `system_notification` VALUES (3, '优惠券即将到期', '您的优惠券\"新用户专享券\"将在3天后到期，赶快使用吧！', 3, 'coupon', 1, 2, '1,2', '2025-10-25 17:56:05', 2, 0, '2025-10-25 17:56:05', '2025-10-25 17:56:05');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（MD5加密）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：1男，2女',
  `member_level` tinyint NULL DEFAULT 1 COMMENT '会员等级：1普通，2银卡，3金卡，4钻石',
  `points` int NULL DEFAULT 0 COMMENT '积分',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'USER' COMMENT 'User role',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：1已删除，0未删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_member_level`(`member_level` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1001 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'test_user', '5f4dcc3b5aa765d61d8327deb882cf99', '张三', '13800138000', NULL, 1, 1, 150, 1, 'ADMIN', 0, '2025-10-21 11:12:56', '2025-10-27 14:12:57');
INSERT INTO `user` VALUES (2, 'vip_user', '5f4dcc3b5aa765d61d8327deb882cf99', '李四', '13900139000', NULL, 2, 3, 800, 1, 'USER', 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `user` VALUES (3, 'user001', '5f4dcc3b5aa765d61d8327deb882cf99', '王小明', '13700137000', NULL, 1, 1, 50, 1, 'USER', 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `user` VALUES (4, 'user002', '5f4dcc3b5aa765d61d8327deb882cf99', '刘芳', '13600136000', NULL, 2, 2, 300, 1, 'USER', 1, '2025-10-21 11:12:56', '2025-10-21 11:32:58');
INSERT INTO `user` VALUES (5, 'user003', '5f4dcc3b5aa765d61d8327deb882cf99', '陈强', '13500135000', NULL, 1, 2, 450, 1, 'USER', 0, '2025-10-21 11:12:56', '2025-10-21 11:32:43');
INSERT INTO `user` VALUES (6, 'user004', '5f4dcc3b5aa765d61d8327deb882cf99', '赵敏', '13400134000', NULL, 2, 3, 1200, 1, 'USER', 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `user` VALUES (7, 'user005', '5f4dcc3b5aa765d61d8327deb882cf99', '孙悦', '13300133000', NULL, 2, 1, 80, 1, 'USER', 0, '2025-10-21 11:12:56', '2025-10-21 11:32:52');
INSERT INTO `user` VALUES (8, 'user006', '5f4dcc3b5aa765d61d8327deb882cf99', '周杰', '13200132000', NULL, 1, 4, 2000, 1, 'USER', 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `user` VALUES (9, 'user007', '5f4dcc3b5aa765d61d8327deb882cf99', '吴倩', '13100131000', NULL, 2, 2, 520, 1, 'USER', 0, '2025-10-21 11:12:56', '2025-10-21 11:12:56');
INSERT INTO `user` VALUES (10, 'user008', '5f4dcc3b5aa765d61d8327deb882cf99', '郑伟', '13000130000', NULL, 1, 1, 100, 1, 'USER', 0, '2025-10-21 11:12:56', '2025-10-21 11:29:21');
INSERT INTO `user` VALUES (11, '123123', '123123', '123', '13316294860', NULL, 1, 1, 1, 1, 'USER', 0, '2025-10-22 14:41:35', '2025-10-22 14:41:35');
INSERT INTO `user` VALUES (999, 'anonymous', 'anonymous', '????', '00000000000', NULL, NULL, 1, 0, 1, 'USER', 0, '2025-10-22 15:33:47', '2025-10-22 15:33:47');
INSERT INTO `user` VALUES (1000, 'cici', 'e10adc3949ba59abbe56e057f20f883e', NULL, '18630829601', NULL, NULL, 1, 0, 1, 'USER', 0, '2025-10-27 10:04:10', '2025-10-27 10:04:10');

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区/县',
  `detail_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认地址：1是，0否',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (1, 999, '陈', '18630829601', '上海市', '上海市', '长宁区', '额外全额瓦其', 0, 0, '2025-10-23 20:59:09', '2025-10-23 20:59:09');

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `coupon_id` bigint NOT NULL COMMENT '优惠券ID',
  `receive_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `use_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `order_id` bigint NULL DEFAULT NULL COMMENT '订单ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1未使用，2已使用，3已过期',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_coupon_id`(`coupon_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------
INSERT INTO `user_coupon` VALUES (1, 1, 1, '2025-10-24 18:44:39', NULL, '2025-12-31 23:59:59', NULL, 1, 0, '2025-10-24 18:44:39');
INSERT INTO `user_coupon` VALUES (2, 1, 4, '2025-10-24 18:44:39', NULL, '2025-12-31 23:59:59', NULL, 1, 0, '2025-10-24 18:44:39');
INSERT INTO `user_coupon` VALUES (3, 2, 2, '2025-10-24 18:44:39', NULL, '2025-12-31 23:59:59', NULL, 1, 0, '2025-10-24 18:44:39');
INSERT INTO `user_coupon` VALUES (4, 2, 4, '2025-10-24 18:44:39', NULL, '2025-12-31 23:59:59', NULL, 1, 0, '2025-10-24 18:44:39');
INSERT INTO `user_coupon` VALUES (5, 2, 3, '2025-10-24 18:44:39', NULL, '2025-12-31 23:59:59', NULL, 1, 0, '2025-10-24 18:44:39');
INSERT INTO `user_coupon` VALUES (6, 3, 5, '2025-10-24 18:44:39', NULL, '2025-12-31 23:59:59', NULL, 1, 0, '2025-10-24 18:44:39');
INSERT INTO `user_coupon` VALUES (7, 3, 4, '2025-10-24 18:44:39', NULL, '2025-12-31 23:59:59', NULL, 1, 0, '2025-10-24 18:44:39');
INSERT INTO `user_coupon` VALUES (8, 10, 6, '2025-10-24 18:44:39', NULL, '2025-12-31 23:59:59', NULL, 1, 0, '2025-10-24 18:44:39');

-- ----------------------------
-- Table structure for user_notification
-- ----------------------------
DROP TABLE IF EXISTS `user_notification`;
CREATE TABLE `user_notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `notification_id` bigint NOT NULL COMMENT '通知ID',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读：1已读，0未读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：1删除，0未删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_notification`(`user_id` ASC, `notification_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户通知记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_notification
-- ----------------------------
INSERT INTO `user_notification` VALUES (1, 1, 1, 1, '2025-10-25 17:56:05', 0, '2025-10-25 17:56:05', '2025-10-25 17:56:05');
INSERT INTO `user_notification` VALUES (2, 2, 2, 0, NULL, 0, '2025-10-25 17:56:05', '2025-10-25 17:56:05');
INSERT INTO `user_notification` VALUES (3, 1, 3, 0, NULL, 0, '2025-10-25 17:56:05', '2025-10-25 17:56:05');
INSERT INTO `user_notification` VALUES (4, 2, 3, 0, NULL, 0, '2025-10-25 17:56:05', '2025-10-25 17:56:05');

-- ----------------------------
-- Table structure for user_points_record
-- ----------------------------
DROP TABLE IF EXISTS `user_points_record`;
CREATE TABLE `user_points_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` tinyint NOT NULL COMMENT '类型：1增加，2减少',
  `points` int NOT NULL COMMENT '积分数量',
  `source` tinyint NOT NULL COMMENT '来源：1注册，2消费，3签到，4活动',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户积分记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_points_record
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
