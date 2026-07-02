-- ============================================================
-- 旅游网站初始化数据
-- 密码说明：所有账号初始密码均为 123456
--   对应的 BCrypt 哈希（已用 Hutool BCrypt 验证通过）：
--   $2a$10$l71iCsNF7C5XERxB2xkjtOkQYdLZd6aJec8cKEDhghPEAC6qdHo6u
-- ============================================================

-- ------------------------------
-- 管理员（账号 admin / 密码 123456）
-- ------------------------------
INSERT INTO `admin`(`username`, `email`, `password`, `nickname`, `avatar`, `role`, `status`)
VALUES ('admin', 'admin@travel.com', '$2a$10$l71iCsNF7C5XERxB2xkjtOkQYdLZd6aJec8cKEDhghPEAC6qdHo6u', '超级管理员', NULL, 'admin', 1);

-- ------------------------------
-- 示例用户（账号 user1 / 密码 123456）
-- ------------------------------
INSERT INTO `user`(`username`, `phone`, `email`, `password`, `nickname`, `avatar`, `gender`, `status`, `points`, `member_level`)
VALUES
  ('user1', '13800000001', 'user1@travel.com', '$2a$10$l71iCsNF7C5XERxB2xkjtOkQYdLZd6aJec8cKEDhghPEAC6qdHo6u', '旅行家小张', NULL, 1, 1, 200, 2),
  ('user2', '13800000002', 'user2@travel.com', '$2a$10$l71iCsNF7C5XERxB2xkjtOkQYdLZd6aJec8cKEDhghPEAC6qdHo6u', '背包客小李', NULL, 2, 1, 50, 1);

-- ------------------------------
-- 分类（两级）
-- ------------------------------
INSERT INTO `category`(`id`, `parent_id`, `name`, `icon`, `sort`, `status`) VALUES
  (1, 0, '国内游',   NULL, 1, 1),
  (2, 0, '出境游',   NULL, 2, 1),
  (3, 0, '周边游',   NULL, 3, 1),
  (4, 0, '主题游',   NULL, 4, 1),
  (11, 1, '云南',    NULL, 1, 1),
  (12, 1, '海南',    NULL, 2, 1),
  (13, 1, '四川',    NULL, 3, 1),
  (21, 2, '东南亚',  NULL, 1, 1),
  (22, 2, '日韩',    NULL, 2, 1),
  (41, 4, '亲子游',  NULL, 1, 1),
  (42, 4, '蜜月游',  NULL, 2, 1);

-- ------------------------------
-- 产品
-- ------------------------------
INSERT INTO `product`(`category_id`, `title`, `subtitle`, `destination`, `departure`, `price`, `market_price`, `stock`, `cover`, `images`, `detail`, `cost_explain`, `days`, `tags`, `score`, `sales`, `status`, `is_hot`, `is_special`) VALUES
  (11, '丽江大理5日纯玩', '洱海骑行+玉龙雪山，纯玩无购物', '云南·丽江', '北京', 2680.00, 3280.00, 100, 'https://picsum.photos/seed/lijiang/800/500', '["https://picsum.photos/seed/lijiang/800/500","https://picsum.photos/seed/dali/800/500","https://picsum.photos/seed/xueshan/800/500"]', '<h3>行程安排</h3><p>第1天：抵达丽江，入住古城客栈；第2天：玉龙雪山；第3天：大理洱海骑行；第4天：双廊古镇；第5天：返程。</p>', '费用包含：往返机票、住宿、景点门票、导游；不含：个人消费、自费项目。', 5, '纯玩,雪山,洱海', 4.8, 356, 1, 1, 1),
  (12, '三亚蜈支洲岛4日海岛游', '潜水体验+海景酒店', '海南·三亚', '上海', 3299.00, 3999.00, 80, 'https://picsum.photos/seed/sanya/800/500', '["https://picsum.photos/seed/sanya/800/500","https://picsum.photos/seed/wuzhizhou/800/500"]', '<h3>行程安排</h3><p>第1天：抵达三亚；第2天：蜈支洲岛潜水；第3天：亚龙湾沙滩；第4天：返程。</p>', '费用包含：往返机票、海景酒店、潜水体验；不含：个人消费。', 4, '海岛,潜水,亲子', 4.9, 512, 1, 1, 1),
  (13, '成都九寨沟黄龙5日游', '人间仙境+熊猫基地', '四川·成都', '广州', 2980.00, 3580.00, 60, 'https://picsum.photos/seed/jiuzhai/800/500', '["https://picsum.photos/seed/jiuzhai/800/500","https://picsum.photos/seed/huanglong/800/500"]', '<h3>行程安排</h3><p>九寨沟、黄龙、熊猫基地、宽窄巷子。</p>', '费用包含：交通、住宿、门票；不含：缆车费用。', 5, '山水,熊猫,休闲', 4.7, 289, 1, 1, 0),
  (21, '泰国曼谷芭提雅6日游', '人妖秀+水上市场', '泰国·曼谷', '北京', 3680.00, 4580.00, 50, 'https://picsum.photos/seed/bangkok/800/500', '["https://picsum.photos/seed/bangkok/800/500","https://picsum.photos/seed/pattaya/800/500"]', '<h3>行程安排</h3><p>大皇宫、水上市场、芭提雅、珊瑚岛。</p>', '费用包含：机票、酒店、团队签证、用餐；不含：小费、自费。', 6, '出境,海岛,异国', 4.6, 178, 1, 0, 1),
  (22, '日本东京大阪7日自由行', '樱花季限定，温泉美食', '日本·东京', '上海', 6980.00, 8280.00, 40, 'https://picsum.photos/seed/tokyo/800/500', '["https://picsum.photos/seed/tokyo/800/500","https://picsum.photos/seed/osaka/800/500"]', '<h3>行程安排</h3><p>东京、富士山、京都、大阪。</p>', '费用包含：机票、酒店、JR PASS；不含：签证、个人消费。', 7, '出境,樱花,温泉', 4.9, 145, 1, 0, 1),
  (41, '上海迪士尼亲子3日游', '亲子首选，畅玩乐园', '上海', '杭州', 2580.00, 2980.00, 120, 'https://picsum.photos/seed/disney/800/500', '["https://picsum.photos/seed/disney/800/500"]', '<h3>行程安排</h3><p>迪士尼乐园畅玩两天，外滩夜景。</p>', '费用包含：乐园门票、酒店；不含：交通。', 3, '亲子,乐园', 4.8, 678, 1, 1, 0);

-- ------------------------------
-- 产品团期（为前几个产品生成未来出行日期）
-- ------------------------------
INSERT INTO `product_schedule`(`product_id`, `travel_date`, `price`, `stock`) VALUES
  (1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2680.00, 20),
  (1, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 2780.00, 15),
  (1, DATE_ADD(CURDATE(), INTERVAL 14 DAY), 2680.00, 20),
  (2, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 3299.00, 18),
  (2, DATE_ADD(CURDATE(), INTERVAL 10 DAY), 3399.00, 12),
  (3, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 2980.00, 16),
  (3, DATE_ADD(CURDATE(), INTERVAL 12 DAY), 2980.00, 16),
  (4, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 3680.00, 10),
  (5, DATE_ADD(CURDATE(), INTERVAL 20 DAY), 6980.00, 8),
  (6, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 2580.00, 30);

-- ------------------------------
-- 首页轮播图
-- ------------------------------
INSERT INTO `banner`(`image`, `link`, `title`, `sort`, `status`) VALUES
  ('https://picsum.photos/seed/banner1/1200/400', '/product/1', '丽江大理5日纯玩 ¥2680起', 1, 1),
  ('https://picsum.photos/seed/banner2/1200/400', '/product/2', '三亚海岛游 限时特价', 2, 1),
  ('https://picsum.photos/seed/banner3/1200/400', '/product/5', '日本樱花季 火热预订', 3, 1);

-- ------------------------------
-- 优惠券
-- ------------------------------
INSERT INTO `coupon`(`name`, `type`, `face_value`, `min_amount`, `total_count`, `remain_count`, `start_time`, `end_time`, `valid_days`, `status`) VALUES
  ('新人立减50', 3, 50.00, 500.00, 1000, 1000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 30, 1),
  ('满2000减200', 1, 200.00, 2000.00, 500, 480, NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY), 15, 1),
  ('满3000减300', 1, 300.00, 3000.00, 300, 300, NOW(), DATE_ADD(NOW(), INTERVAL 20 DAY), 20, 1);

-- ------------------------------
-- 示例评论
-- ------------------------------
INSERT INTO `comment`(`user_id`, `product_id`, `order_id`, `score`, `content`, `images`, `like_count`) VALUES
  (1, 1, NULL, 5, '行程安排得很合理，玉龙雪山太震撼了，导游也很专业！', NULL, 12),
  (2, 1, NULL, 4, '整体不错，就是旺季人有点多。', NULL, 3),
  (1, 2, NULL, 5, '蜈支洲岛的水真的很清澈，潜水体验超棒！', NULL, 8);

-- ------------------------------
-- 示例游记
-- ------------------------------
INSERT INTO `travel_note`(`user_id`, `title`, `content`, `images`, `video`, `product_id`, `like_count`, `view_count`, `status`) VALUES
  (1, '一个人的丽江，遇见最美的自己', '<p>在丽江古城的青石板路上漫步，每一处都是风景...</p>', '["https://picsum.photos/seed/note1/800/500"]', NULL, 1, 56, 320, 1),
  (2, '三亚亲子游攻略分享', '<p>带孩子去三亚，蜈支洲岛是必去的...</p>', '["https://picsum.photos/seed/note2/800/500"]', NULL, 2, 38, 210, 1);

-- ------------------------------
-- 示例消息
-- ------------------------------
INSERT INTO `message`(`user_id`, `title`, `content`, `type`, `is_read`) VALUES
  (1, '欢迎注册', '欢迎来到旅游网站，开启您的精彩旅程！', 1, 0),
  (2, '欢迎注册', '欢迎来到旅游网站，开启您的精彩旅程！', 1, 1);
