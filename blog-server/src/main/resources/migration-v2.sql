-- 从早期版本升级：执行前可先检查 information_schema，避免重复添加字段。
ALTER TABLE `sys_user`
  ADD COLUMN `token_version` INT NOT NULL DEFAULT 0
  COMMENT '令牌版本，修改密码后递增以使旧令牌失效'
  AFTER `status`;

ALTER TABLE `blog_message`
  ALTER COLUMN `status` SET DEFAULT 'PENDING';
