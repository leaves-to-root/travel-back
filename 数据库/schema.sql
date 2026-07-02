-- ============================================================
-- 旅游网站数据库建表脚本
-- 数据库：MySQL 8.x
-- 注意：执行本脚本前请先创建数据库 travel
--   CREATE DATABASE travel DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
-- ============================================================

-- ------------------------------
-- 1. 用户表
-- ------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`      VARCHAR(64)  DEFAULT NULL COMMENT '用户名（账号登录）',
  `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `email`         VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `password`      VARCHAR(128) NOT NULL COMMENT '密码（BCrypt）',
  `nickname`      VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
  `avatar`        VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
  `gender`        TINYINT      DEFAULT 0 COMMENT '性别：0未知 1男 2女',
  `status`        TINYINT      DEFAULT 1 COMMENT '状态：0禁用 1正常',
  `points`        INT          DEFAULT 0 COMMENT '积分',
  `member_level`  TINYINT      DEFAULT 1 COMMENT '会员等级',
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`       TINYINT      DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`, `deleted`),
  UNIQUE KEY `uk_phone` (`phone`, `deleted`),
  UNIQUE KEY `uk_email` (`email`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ------------------------------
-- 2. 管理员表
-- ------------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    VARCHAR(64)  NOT NULL COMMENT '账号',
  `password`    VARCHAR(128) NOT NULL COMMENT '密码（BCrypt）',
  `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
  `avatar`      VARCHAR(512) DEFAULT NULL COMMENT '头像',
  `role`        VARCHAR(32)  DEFAULT 'admin' COMMENT '角色',
  `status`      TINYINT      DEFAULT 1 COMMENT '状态：0禁用 1正常',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT      DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_username` (`username`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ------------------------------
-- 3. 分类表（多级）
-- ------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `parent_id`   BIGINT       DEFAULT 0 COMMENT '父分类id，0为顶级',
  `name`        VARCHAR(64)  NOT NULL COMMENT '分类名',
  `icon`        VARCHAR(512) DEFAULT NULL COMMENT '图标URL',
  `sort`        INT          DEFAULT 0 COMMENT '排序，越小越靠前',
  `status`      TINYINT      DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT      DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- ------------------------------
-- 4. 旅游产品表
-- ------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT,
  `category_id`  BIGINT        DEFAULT NULL COMMENT '分类id',
  `title`        VARCHAR(255)  NOT NULL COMMENT '产品标题',
  `subtitle`     VARCHAR(255)  DEFAULT NULL COMMENT '副标题/卖点',
  `destination`  VARCHAR(128)  DEFAULT NULL COMMENT '目的地',
  `departure`    VARCHAR(128)  DEFAULT NULL COMMENT '出发地',
  `price`        DECIMAL(10,2) NOT NULL COMMENT '现价',
  `market_price` DECIMAL(10,2) DEFAULT NULL COMMENT '市场价',
  `stock`        INT           DEFAULT 0 COMMENT '库存',
  `cover`        VARCHAR(512)  DEFAULT NULL COMMENT '封面图',
  `images`       TEXT          DEFAULT NULL COMMENT '图片集（JSON数组）',
  `detail`       LONGTEXT      DEFAULT NULL COMMENT '图文详情（富文本/行程安排）',
  `cost_explain` TEXT          DEFAULT NULL COMMENT '费用说明',
  `days`         INT           DEFAULT 1 COMMENT '行程天数',
  `tags`         VARCHAR(255)  DEFAULT NULL COMMENT '标签（逗号分隔）',
  `score`        DECIMAL(2,1)  DEFAULT 5.0 COMMENT '评分',
  `sales`        INT           DEFAULT 0 COMMENT '销量',
  `status`       TINYINT       DEFAULT 1 COMMENT '状态：0下架 1上架',
  `is_hot`       TINYINT       DEFAULT 0 COMMENT '是否热门：0否 1是',
  `is_special`   TINYINT       DEFAULT 0 COMMENT '是否特价：0否 1是',
  `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`      TINYINT       DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='旅游产品表';

-- ------------------------------
-- 5. 产品团期（出行日期/价格/库存）
-- ------------------------------
DROP TABLE IF EXISTS `product_schedule`;
CREATE TABLE `product_schedule` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT,
  `product_id`   BIGINT        NOT NULL COMMENT '产品id',
  `travel_date`  DATE          NOT NULL COMMENT '出行日期',
  `price`        DECIMAL(10,2) NOT NULL COMMENT '当日价格',
  `stock`        INT           NOT NULL DEFAULT 0 COMMENT '当日库存',
  `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`      TINYINT       DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`, `travel_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品团期表';

-- ------------------------------
-- 6. 首页轮播图
-- ------------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `image`       VARCHAR(512) NOT NULL COMMENT '图片URL',
  `link`        VARCHAR(512) DEFAULT NULL COMMENT '点击跳转链接',
  `title`       VARCHAR(128) DEFAULT NULL COMMENT '标题',
  `sort`        INT          DEFAULT 0 COMMENT '排序',
  `status`      TINYINT      DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT      DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图表';

-- ------------------------------
-- 7. 用户收藏
-- ------------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT   NOT NULL,
  `product_id`  BIGINT   NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT  DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- ------------------------------
-- 8. 购物车
-- ------------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT   NOT NULL,
  `product_id`  BIGINT   NOT NULL,
  `schedule_id` BIGINT   DEFAULT NULL COMMENT '团期id',
  `travel_date` DATE     DEFAULT NULL COMMENT '出行日期',
  `quantity`    INT      DEFAULT 1 COMMENT '数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT  DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ------------------------------
-- 9. 订单主表
-- ------------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT,
  `order_no`       VARCHAR(64)   NOT NULL COMMENT '订单号',
  `user_id`        BIGINT        NOT NULL COMMENT '用户id',
  `total_amount`   DECIMAL(10,2) NOT NULL COMMENT '订单总额',
  `pay_amount`     DECIMAL(10,2) DEFAULT NULL COMMENT '实付金额',
  `status`         TINYINT       DEFAULT 0 COMMENT '0待支付 1已支付/待出行 2已完成 3已取消 4退款中 5已退款',
  `pay_type`       VARCHAR(32)   DEFAULT NULL COMMENT '支付方式：alipay/wechat/mock',
  `pay_time`       DATETIME      DEFAULT NULL COMMENT '支付时间',
  `travel_date`    DATE          DEFAULT NULL COMMENT '出行日期',
  `contact_name`   VARCHAR(64)   DEFAULT NULL COMMENT '联系人',
  `contact_phone`  VARCHAR(20)   DEFAULT NULL COMMENT '联系电话',
  `person_count`   INT           DEFAULT 1 COMMENT '出行人数',
  `remark`         VARCHAR(255)  DEFAULT NULL COMMENT '订单备注',
  `coupon_id`      BIGINT        DEFAULT NULL COMMENT '使用的优惠券id',
  `coupon_amount`  DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
  `refund_reason`  VARCHAR(255)  DEFAULT NULL COMMENT '退款原因',
  `create_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `update_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`        TINYINT       DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ------------------------------
-- 10. 订单明细
-- ------------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT,
  `order_id`     BIGINT        NOT NULL,
  `product_id`   BIGINT        NOT NULL,
  `product_name` VARCHAR(255)  DEFAULT NULL COMMENT '下单时的产品名快照',
  `product_cover` VARCHAR(512) DEFAULT NULL COMMENT '产品封面快照',
  `travel_date`  DATE          DEFAULT NULL,
  `price`        DECIMAL(10,2) NOT NULL COMMENT '单价',
  `quantity`     INT           NOT NULL COMMENT '数量',
  `subtotal`     DECIMAL(10,2) NOT NULL COMMENT '小计',
  `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`      TINYINT       DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ------------------------------
-- 11. 优惠券模板
-- ------------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT,
  `name`         VARCHAR(128)  NOT NULL COMMENT '优惠券名称',
  `type`         TINYINT       DEFAULT 1 COMMENT '类型：1满减 2折扣 3直减',
  `face_value`   DECIMAL(10,2) NOT NULL COMMENT '面额（满减/直减为金额，折扣为折扣率*100）',
  `min_amount`   DECIMAL(10,2) DEFAULT 0 COMMENT '使用门槛（满多少）',
  `total_count`  INT           DEFAULT 0 COMMENT '发放总量，0不限',
  `remain_count` INT           DEFAULT 0 COMMENT '剩余数量',
  `start_time`   DATETIME      DEFAULT NULL COMMENT '领取开始时间',
  `end_time`     DATETIME      DEFAULT NULL COMMENT '领取结束时间',
  `valid_days`   INT           DEFAULT 30 COMMENT '领取后有效天数',
  `status`       TINYINT       DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `update_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`      TINYINT       DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板表';

-- ------------------------------
-- 12. 用户优惠券
-- ------------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT   NOT NULL,
  `coupon_id`   BIGINT   NOT NULL,
  `status`      TINYINT  DEFAULT 0 COMMENT '0未使用 1已使用 2已过期',
  `order_id`    BIGINT   DEFAULT NULL COMMENT '使用的订单id',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT  DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- ------------------------------
-- 13. 游记
-- ------------------------------
DROP TABLE IF EXISTS `travel_note`;
CREATE TABLE `travel_note` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT       NOT NULL COMMENT '作者',
  `title`       VARCHAR(255) NOT NULL COMMENT '标题',
  `content`     LONGTEXT     COMMENT '正文',
  `images`      TEXT         COMMENT '图片集（JSON数组）',
  `video`       VARCHAR(512) DEFAULT NULL COMMENT '视频URL',
  `product_id`  BIGINT       DEFAULT NULL COMMENT '关联产品',
  `like_count`  INT          DEFAULT 0 COMMENT '点赞数',
  `view_count`  INT          DEFAULT 0 COMMENT '浏览量',
  `status`      TINYINT      DEFAULT 1 COMMENT '状态：0待审核 1已发布 2已下架',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT      DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游记表';

-- ------------------------------
-- 14. 评论评分
-- ------------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT   NOT NULL,
  `product_id`  BIGINT   NOT NULL,
  `order_id`    BIGINT   DEFAULT NULL,
  `score`       TINYINT  DEFAULT 5 COMMENT '评分1-5',
  `content`     VARCHAR(1000) DEFAULT NULL,
  `images`      TEXT     DEFAULT NULL COMMENT '评论图片（JSON数组）',
  `like_count`  INT      DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT  DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`),
  KEY `idx_user_order` (`user_id`, `order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论评分表';

-- ------------------------------
-- 15. 站内消息
-- ------------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT   NOT NULL COMMENT '接收用户id',
  `title`       VARCHAR(128) DEFAULT NULL,
  `content`     VARCHAR(1000) DEFAULT NULL,
  `type`        TINYINT  DEFAULT 1 COMMENT '1系统 2订单 3互动',
  `is_read`     TINYINT  DEFAULT 0 COMMENT '0未读 1已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT  DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息表';

-- ------------------------------
-- 16. 浏览历史
-- ------------------------------
DROP TABLE IF EXISTS `browse_history`;
CREATE TABLE `browse_history` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT   NOT NULL,
  `product_id`  BIGINT   NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     TINYINT  DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览历史表';

-- ------------------------------
-- 17. 管理员操作日志
-- ------------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `admin_id`    BIGINT   DEFAULT NULL,
  `admin_name`  VARCHAR(64) DEFAULT NULL,
  `module`      VARCHAR(64) DEFAULT NULL COMMENT '操作模块',
  `action`      VARCHAR(128) DEFAULT NULL COMMENT '操作描述',
  `method`      VARCHAR(255) DEFAULT NULL COMMENT '请求方法',
  `params`      TEXT     DEFAULT NULL COMMENT '请求参数',
  `ip`          VARCHAR(64) DEFAULT NULL,
  `cost_ms`     BIGINT   DEFAULT NULL COMMENT '耗时(ms)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_admin` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员操作日志表';
