DROP DATABASE IF EXISTS `ai_trip`;
CREATE DATABASE `ai_trip`;
USE `ai_trip`;

-- 用户表
CREATE TABLE `user` (
                        `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                        `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
                        `password` VARCHAR(255) NOT NULL COMMENT '加密密码',
                        `avatar_url` VARCHAR(255) COMMENT '头像URL',
                        `nickname` VARCHAR(50) COMMENT '昵称',
                        `email` VARCHAR(100) UNIQUE COMMENT '电子邮箱',
                        `status` int DEFAULT 1 COMMENT '账户状态，1代表正常，0代表冻结',
                        `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        INDEX `idx_username` (`username`),
                        INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 会话表
CREATE TABLE `session` (
                           `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
                           `title` VARCHAR(100) COMMENT '会话标题',
                           `user_id` BIGINT NOT NULL COMMENT '用户ID',
                           `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           INDEX  `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 消息表
CREATE TABLE `chat_message` (
                                `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
                                `text` text NOT NULL COMMENT '消息内容',
                                `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                `session_id` BIGINT NOT NULL COMMENT '会话ID',
                                `message_type` ENUM('user', 'assistant') NOT NULL DEFAULT 'user' COMMENT '发送者类型',
                                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                Unique INDEX `idx_user_session_created` (`session_id`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



-- 用户偏好表
CREATE TABLE `preference` (
                              `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '偏好ID',
                              `user_id` BIGINT NOT NULL COMMENT '用户ID',
                              `preference_type` VARCHAR(50) NOT NULL COMMENT '偏好类型',
                              `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
#                               FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
                              INDEX `idx_user_preferences` (`user_id`, `preference_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 初始管理员账户
-- admin 123456
INSERT INTO `user` (`username`, `password`, `nickname`, `email` )
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'admin@aitrip.com');

insert into `session`(`user_id`)
values (1),(1),(1),(1),(1);