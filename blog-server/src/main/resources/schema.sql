-- 个人博客系统 MySQL 8 初始化脚本（面向全新 personal_blog 数据库）
CREATE DATABASE IF NOT EXISTS `personal_blog`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `personal_blog`;

-- 用户表：管理员与前台注册用户
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户主键',
  `username` VARCHAR(50) NOT NULL COMMENT '登录用户名',
  `password` VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密密码',
  `nickname` VARCHAR(50) NOT NULL COMMENT '展示昵称',
  `email` VARCHAR(100) NOT NULL COMMENT '用户邮箱',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像地址',
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN 管理员，USER 普通用户',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE 正常，DISABLED 禁用',
  `token_version` INT NOT NULL DEFAULT 0 COMMENT '令牌版本，修改密码后递增以使旧令牌失效',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 否，1 是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  UNIQUE KEY `uk_user_email` (`email`),
  CONSTRAINT `ck_user_role` CHECK (`role` IN ('ADMIN', 'USER')),
  CONSTRAINT `ck_user_status` CHECK (`status` IN ('ACTIVE', 'DISABLED')),
  CONSTRAINT `ck_user_deleted` CHECK (`deleted` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 邮箱验证码表：用于保存一次性密码重置验证码
CREATE TABLE IF NOT EXISTS `email_verification_code` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '验证码主键',
  `email` VARCHAR(100) NOT NULL COMMENT '接收验证码的邮箱',
  `code` CHAR(6) NOT NULL COMMENT '6位数字验证码',
  `type` VARCHAR(30) NOT NULL COMMENT '验证码类型：RESET_PASSWORD',
  `expire_time` DATETIME NOT NULL COMMENT '验证码失效时间',
  `used` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已使用或失效：0 否，1 是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_email_code_lookup` (`email`, `type`, `code`, `used`),
  KEY `idx_email_send_limit` (`email`, `type`, `create_time`),
  KEY `idx_email_expire_time` (`expire_time`),
  CONSTRAINT `ck_email_code_type` CHECK (`type` IN ('RESET_PASSWORD')),
  CONSTRAINT `ck_email_code_used` CHECK (`used` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮箱验证码表';

-- 分类表：一篇文章只能属于一个分类
CREATE TABLE IF NOT EXISTS `blog_category` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类主键',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，数值越小越靠前',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE 启用，INACTIVE 停用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 否，1 是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`name`),
  KEY `idx_category_status_sort` (`status`, `sort_order`),
  CONSTRAINT `ck_category_status` CHECK (`status` IN ('ACTIVE', 'INACTIVE')),
  CONSTRAINT `ck_category_deleted` CHECK (`deleted` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- 标签表：通过文章标签关联表与文章形成多对多关系
CREATE TABLE IF NOT EXISTS `blog_tag` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签主键',
  `name` VARCHAR(50) NOT NULL COMMENT '标签显示名称',
  `slug` VARCHAR(80) NOT NULL COMMENT '标签 URL 别名',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '标签描述',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE 启用，INACTIVE 停用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 否，1 是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`name`),
  UNIQUE KEY `uk_tag_slug` (`slug`),
  KEY `idx_tag_status` (`status`),
  CONSTRAINT `ck_tag_status` CHECK (`status` IN ('ACTIVE', 'INACTIVE')),
  CONSTRAINT `ck_tag_deleted` CHECK (`deleted` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签表';

-- 文章表：分类外键限制删除，防止删除仍被文章引用的分类
CREATE TABLE IF NOT EXISTS `blog_article` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文章主键',
  `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
  `slug` VARCHAR(220) NOT NULL COMMENT '文章 URL 别名',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
  `content` LONGTEXT NOT NULL COMMENT '文章正文，存储 Markdown 或 HTML',
  `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图片地址',
  `category_id` BIGINT UNSIGNED NOT NULL COMMENT '所属分类主键',
  `author_id` BIGINT UNSIGNED NOT NULL COMMENT '作者用户主键',
  `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT 草稿，PUBLISHED 已发布',
  `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0 否，1 是',
  `view_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '阅读次数',
  `like_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞次数冗余统计',
  `comment_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '已通过评论数冗余统计',
  `published_at` DATETIME DEFAULT NULL COMMENT '发布时间，草稿为空',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 否，1 是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_slug` (`slug`),
  KEY `idx_article_category` (`category_id`),
  KEY `idx_article_author` (`author_id`),
  KEY `idx_article_status_published` (`status`, `published_at`),
  KEY `idx_article_top` (`is_top`),
  CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `blog_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_article_author` FOREIGN KEY (`author_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ck_article_status` CHECK (`status` IN ('DRAFT', 'PUBLISHED')),
  CONSTRAINT `ck_article_top` CHECK (`is_top` IN (0, 1)),
  CONSTRAINT `ck_article_deleted` CHECK (`deleted` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客文章表';

-- 文章标签关联表：联合主键确保文章与标签的对应关系不重复
CREATE TABLE IF NOT EXISTS `blog_article_tag` (
  `article_id` BIGINT UNSIGNED NOT NULL COMMENT '文章主键',
  `tag_id` BIGINT UNSIGNED NOT NULL COMMENT '标签主键',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联创建时间',
  PRIMARY KEY (`article_id`, `tag_id`),
  KEY `idx_article_tag_tag` (`tag_id`),
  CONSTRAINT `fk_article_tag_article` FOREIGN KEY (`article_id`) REFERENCES `blog_article` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_article_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `blog_tag` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- 评论表：仅支持直接评论文章，不设置父级评论字段
CREATE TABLE IF NOT EXISTS `blog_comment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论主键',
  `article_id` BIGINT UNSIGNED NOT NULL COMMENT '被评论文章主键',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '评论用户主键',
  `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING 待审核，APPROVED 已通过，REJECTED 已拒绝',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 否，1 是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_article_user_content` (`article_id`, `user_id`, `content`),
  KEY `idx_comment_article_status_time` (`article_id`, `status`, `created_at`),
  KEY `idx_comment_user_time` (`user_id`, `created_at`),
  CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `blog_article` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ck_comment_status` CHECK (`status` IN ('PENDING', 'APPROVED', 'REJECTED')),
  CONSTRAINT `ck_comment_deleted` CHECK (`deleted` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章评论表';

-- 留言表：登录用户可在留言板发布内容
CREATE TABLE IF NOT EXISTS `blog_message` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '留言主键',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '留言用户主键',
  `content` VARCHAR(500) NOT NULL COMMENT '留言内容',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING 待审核，APPROVED 已通过，REJECTED 已拒绝',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 否，1 是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_user_content` (`user_id`, `content`),
  KEY `idx_message_status_time` (`status`, `created_at`),
  CONSTRAINT `fk_message_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ck_message_status` CHECK (`status` IN ('PENDING', 'APPROVED', 'REJECTED')),
  CONSTRAINT `ck_message_deleted` CHECK (`deleted` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客留言板表';

-- 点赞表：联合主键保证一个用户只能给一篇文章点赞一次
CREATE TABLE IF NOT EXISTS `blog_article_like` (
  `article_id` BIGINT UNSIGNED NOT NULL COMMENT '被点赞文章主键',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '点赞用户主键',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`article_id`, `user_id`),
  KEY `idx_article_like_user` (`user_id`),
  CONSTRAINT `fk_article_like_article` FOREIGN KEY (`article_id`) REFERENCES `blog_article` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_article_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章点赞记录表';

-- 站点配置表：保存前台展示的博客资料
CREATE TABLE IF NOT EXISTS `blog_site_config` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '配置主键',
  `config_key` VARCHAR(80) NOT NULL COMMENT '配置键',
  `config_value` TEXT DEFAULT NULL COMMENT '配置值',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '配置说明',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点配置表';

-- 以下数据可重复执行；$2a$10$... 对应密码 password，仅用于本地开发。
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `role`, `status`) VALUES
  ('admin', '$2a$10$ZaB810qETQjV2fbAdd.rYu0rVHqgWrg0vA7eSLw1OWJXQoicGcefC', '博客管理员', 'admin@example.com', 'ADMIN', 'ACTIVE'),
  ('xiaoming', '$2a$10$ZaB810qETQjV2fbAdd.rYu0rVHqgWrg0vA7eSLw1OWJXQoicGcefC', '小明', 'xiaoming@example.com', 'USER', 'ACTIVE'),
  ('xiaohong', '$2a$10$ZaB810qETQjV2fbAdd.rYu0rVHqgWrg0vA7eSLw1OWJXQoicGcefC', '小红', 'xiaohong@example.com', 'USER', 'ACTIVE')
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`), `nickname` = VALUES(`nickname`), `role` = VALUES(`role`), `status` = VALUES(`status`), `deleted` = 0;

INSERT INTO `blog_category` (`name`, `description`, `sort_order`, `status`) VALUES
  ('技术分享', '技术学习与开发记录', 1, 'ACTIVE'),
  ('生活随笔', '日常思考与记录', 2, 'ACTIVE'),
  ('课程设计', '课程项目与实践总结', 3, 'ACTIVE')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`), `sort_order` = VALUES(`sort_order`), `status` = VALUES(`status`), `deleted` = 0;

INSERT INTO `blog_tag` (`name`, `slug`, `description`, `status`) VALUES
  ('Vue3', 'vue3', 'Vue 3 前端开发', 'ACTIVE'),
  ('Spring Boot', 'spring-boot', 'Spring Boot 后端开发', 'ACTIVE'),
  ('MySQL', 'mysql', 'MySQL 数据库', 'ACTIVE'),
  ('课程设计', 'course-design', '大学课程设计', 'ACTIVE'),
  ('学习笔记', 'study-notes', '学习过程记录', 'ACTIVE')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`), `status` = VALUES(`status`), `deleted` = 0;

INSERT INTO `blog_article` (`title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `status`, `is_top`, `view_count`, `like_count`, `comment_count`, `published_at`) VALUES
  ('Vue3 与 Vite 搭建个人博客前台', 'vue3-vite-blog-frontend', '记录 Vue3、Vite 与 Element Plus 的博客前台搭建过程。', '# Vue3 与 Vite\n\n本文介绍个人博客前台的基础架构和页面组织方式。', (SELECT id FROM blog_category WHERE name = '技术分享'), (SELECT id FROM sys_user WHERE username = 'admin'), 'PUBLISHED', 1, 128, 2, 2, NOW() - INTERVAL 5 DAY),
  ('Spring Boot 3 实现 JWT 登录认证', 'spring-boot-jwt-authentication', '使用 Spring Security 和 JWT 完成后台登录保护。', '# JWT 登录认证\n\n本文记录认证过滤器、Token 签发和接口鉴权的实现思路。', (SELECT id FROM blog_category WHERE name = '技术分享'), (SELECT id FROM sys_user WHERE username = 'admin'), 'PUBLISHED', 0, 86, 1, 1, NOW() - INTERVAL 2 DAY),
  ('个人博客课程设计开发计划', 'personal-blog-course-plan', '尚未发布的课程设计开发草稿。', '# 开发计划\n\n梳理数据库、接口、前端页面和测试计划。', (SELECT id FROM blog_category WHERE name = '课程设计'), (SELECT id FROM sys_user WHERE username = 'admin'), 'DRAFT', 0, 0, 0, 0, NULL)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`), `summary` = VALUES(`summary`), `content` = VALUES(`content`), `status` = VALUES(`status`), `is_top` = VALUES(`is_top`), `deleted` = 0;

INSERT IGNORE INTO `blog_article_tag` (`article_id`, `tag_id`)
SELECT a.id, t.id FROM `blog_article` a JOIN `blog_tag` t
  ON (a.title = 'Vue3 与 Vite 搭建个人博客前台' AND t.slug IN ('vue3', 'course-design', 'study-notes'))
    OR (a.title = 'Spring Boot 3 实现 JWT 登录认证' AND t.slug IN ('spring-boot', 'mysql', 'study-notes'))
    OR (a.title = '个人博客课程设计开发计划' AND t.slug IN ('course-design', 'mysql'));

INSERT IGNORE INTO `blog_comment` (`article_id`, `user_id`, `content`, `status`)
SELECT a.id, u.id, '文章结构很清晰，期待后续的完整实现。', 'APPROVED' FROM blog_article a JOIN sys_user u WHERE a.title = 'Vue3 与 Vite 搭建个人博客前台' AND u.username = 'xiaoming'
UNION ALL SELECT a.id, u.id, '这篇 JWT 的说明对我很有帮助。', 'APPROVED' FROM blog_article a JOIN sys_user u WHERE a.title = 'Vue3 与 Vite 搭建个人博客前台' AND u.username = 'xiaohong'
UNION ALL SELECT a.id, u.id, '请问 Token 过期后会如何处理？', 'PENDING' FROM blog_article a JOIN sys_user u WHERE a.title = 'Spring Boot 3 实现 JWT 登录认证' AND u.username = 'xiaoming';

INSERT IGNORE INTO `blog_article_like` (`article_id`, `user_id`)
SELECT a.id, u.id FROM blog_article a JOIN sys_user u
  ON (a.title = 'Vue3 与 Vite 搭建个人博客前台' AND u.username IN ('xiaoming', 'xiaohong'))
    OR (a.title = 'Spring Boot 3 实现 JWT 登录认证' AND u.username = 'xiaohong');

INSERT IGNORE INTO `blog_message` (`user_id`, `content`, `status`)
SELECT id, '博客的页面很清爽，期待更多技术文章。', 'APPROVED' FROM sys_user WHERE username = 'xiaoming'
UNION ALL SELECT id, '课程设计加油，功能规划得很完整！', 'APPROVED' FROM sys_user WHERE username = 'xiaohong';

INSERT INTO `blog_site_config` (`config_key`, `config_value`, `description`) VALUES
  ('siteName', '我的个人博客', '站点名称'),
  ('siteSubtitle', '记录、思考与分享', '站点副标题'),
  ('homeIntro', '你好，我是一名热爱技术与生活的创作者。', '首页介绍'),
  ('aboutContent', '在这里记录学习过程、项目实践与生活感悟。', '关于我'),
  ('email', 'hello@example.com', '联系邮箱')
ON DUPLICATE KEY UPDATE `config_value` = VALUES(`config_value`), `description` = VALUES(`description`);
