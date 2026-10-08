-- 忘记密码功能数据库升级脚本，可重复执行。
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
