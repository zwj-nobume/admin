-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        11.3.2-MariaDB - mariadb.org binary distribution
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  12.6.0.6765
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- 导出 colonq 的数据库结构
DROP DATABASE IF EXISTS `colonq`;
CREATE DATABASE IF NOT EXISTS `colonq` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `colonq`;

-- 导出  表 colonq.menu_info 结构
CREATE TABLE IF NOT EXISTS `menu_info` (
  `menu_id` uuid NOT NULL DEFAULT uuid() COMMENT '菜单ID',
  `menu_name` varchar(20) NOT NULL COMMENT '菜单名',
  `menu_label` varchar(50) NOT NULL COMMENT '菜单标签',
  `permission` varchar(50) NOT NULL COMMENT '权限标识',
  `parent_id` uuid DEFAULT NULL COMMENT '父标签',
  `create_name` varchar(20) NOT NULL COMMENT '创建人名',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`menu_id`),
  UNIQUE KEY `menu_name` (`menu_name`),
  KEY `parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单信息表';

-- 正在导出表  colonq.menu_info 的数据：~1 rows (大约)
INSERT INTO `menu_info` (`menu_id`, `menu_name`, `menu_label`, `permission`, `parent_id`, `create_name`, `create_time`) VALUES
	('a8c8fc58-fd1d-11ee-9405-f0d41530a047', 'system', '系统管理', 'system', NULL, 'admin', '2024-04-18 08:50:02');

-- 导出  表 colonq.role_info 结构
CREATE TABLE IF NOT EXISTS `role_info` (
  `role_id` uuid NOT NULL DEFAULT uuid() COMMENT '角色ID',
  `role_name` varchar(20) NOT NULL COMMENT '角色名',
  `role_label` varchar(20) NOT NULL COMMENT '角色标签',
  `create_name` varchar(20) NOT NULL COMMENT '创建人名',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';

-- 正在导出表  colonq.role_info 的数据：~4 rows (大约)
INSERT INTO `role_info` (`role_id`, `role_name`, `role_label`, `create_name`, `create_time`) VALUES
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'admin', '管理员', 'admin', '2024-04-17 15:52:04'),
	('bddc962c-fdec-11ee-9405-f0d41530a047', 'test1', '管理员1', 'admin', '2024-04-19 09:32:16'),
	('c0eaa4f0-fdec-11ee-9405-f0d41530a047', 'test2', '管理员2', 'admin', '2024-04-19 09:32:22'),
	('c36b748d-fdec-11ee-9405-f0d41530a047', 'test3', '管理员3', 'admin', '2024-04-19 09:32:26');

-- 导出  表 colonq.role_menu_link 结构
CREATE TABLE IF NOT EXISTS `role_menu_link` (
  `role_id` uuid NOT NULL COMMENT '角色ID',
  `menu_id` uuid NOT NULL COMMENT '菜单ID',
  KEY `id` (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单链接表';

-- 正在导出表  colonq.role_menu_link 的数据：~0 rows (大约)

-- 导出  表 colonq.user_info 结构
CREATE TABLE IF NOT EXISTS `user_info` (
  `user_id` uuid NOT NULL DEFAULT uuid() COMMENT '用户ID',
  `user_name` varchar(20) NOT NULL COMMENT '用户名',
  `password` char(41) NOT NULL DEFAULT password('123456') COMMENT '用户密码',
  `email` varchar(50) NOT NULL COMMENT '用户邮箱',
  `salt` char(64) NOT NULL DEFAULT sha2(md5(rand()),256) COMMENT '盐值',
  `create_name` varchar(20) NOT NULL COMMENT '创建人名',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';

-- 正在导出表  colonq.user_info 的数据：~2 rows (大约)
INSERT INTO `user_info` (`user_id`, `user_name`, `password`, `email`, `salt`, `create_name`, `create_time`) VALUES
	('dca2a4e6-f9f2-11ee-9405-f0d41530a047', 'admin', '*6BB4837EB74329105EE4568DDA7DC67ED2CA2AD9', 'zwj-nobume@outlook.com', '4d7ffba28355cc66aeba8974082de9c87ec479f45b692f0824bde938fb583543', 'admin', '2024-04-14 08:06:35'),
	('d08ecc4d-fb30-11ee-9405-f0d41530a047', 'nobume', '*7F4F8F39C0B8B1348AC226960F5856E6B00DFDCE', 'zwj-nobume@outlook.com', '021aad0b31f2b694b763dfa058718079cfc5e04d62955f9fc0be03858e049165', 'admin', '2024-04-15 22:02:20');

-- 导出  表 colonq.user_role_link 结构
CREATE TABLE IF NOT EXISTS `user_role_link` (
  `user_id` uuid NOT NULL COMMENT '用户ID',
  `role_id` uuid NOT NULL COMMENT '角色ID',
  KEY `id` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色链接表';

-- 正在导出表  colonq.user_role_link 的数据：~4 rows (大约)
INSERT INTO `user_role_link` (`user_id`, `role_id`) VALUES
	('d08ecc4d-fb30-11ee-9405-f0d41530a047', '710d2766-fc8f-11ee-9405-f0d41530a047'),
	('d08ecc4d-fb30-11ee-9405-f0d41530a047', 'bddc962c-fdec-11ee-9405-f0d41530a047'),
	('d08ecc4d-fb30-11ee-9405-f0d41530a047', 'c0eaa4f0-fdec-11ee-9405-f0d41530a047'),
	('d08ecc4d-fb30-11ee-9405-f0d41530a047', 'c36b748d-fdec-11ee-9405-f0d41530a047');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
