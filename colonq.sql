-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        11.4.2-MariaDB - Arch Linux
-- 服务器操作系统:                      Linux
-- HeidiSQL 版本:                  12.7.0.6850
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

-- 导出  表 colonq.log_info 结构
CREATE TABLE IF NOT EXISTS `log_info` (
  `log_id` uuid NOT NULL DEFAULT uuid() COMMENT '角色ID',
  `log_type` enum('info','add','edit','delete','download','login','error') NOT NULL DEFAULT 'info' COMMENT '字典KEY:log_type',
  `api_params` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '入参参数' CHECK (json_valid(`api_params`)),
  `log_intro` varchar(512) NOT NULL DEFAULT '' COMMENT '日志简介',
  `log_data` longtext DEFAULT NULL COMMENT '日志信息',
  `create_name` varchar(20) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`log_id`) USING BTREE,
  KEY `search_key` (`log_type`,`log_intro`(255),`create_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='日志信息表';

-- 正在导出表  colonq.log_info 的数据：~0 rows (大约)

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
  KEY `search` (`parent_id`,`menu_label`,`permission`,`create_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单信息表';

-- 正在导出表  colonq.menu_info 的数据：~26 rows (大约)
INSERT INTO `menu_info` (`menu_id`, `menu_name`, `menu_label`, `permission`, `parent_id`, `create_name`, `create_time`) VALUES
	('f1bfc34f-473b-11ef-81f2-00155d2df2bf', 'system_log', '日志管理', 'system:log', 'a8c8fc58-fd1d-11ee-9405-f0d41530a047', 'admin', '2024-07-21 16:33:45'),
	('02876651-473c-11ef-81f2-00155d2df2bf', 'system_log_query', '日志查询', 'system:log:query', 'f1bfc34f-473b-11ef-81f2-00155d2df2bf', 'admin', '2024-07-21 16:34:13'),
	('c9ab68d5-36a5-11ef-8283-00155d3a40eb', 'system_file', '文件管理', 'system:file', 'a8c8fc58-fd1d-11ee-9405-f0d41530a047', 'admin', '2024-06-30 13:58:35'),
	('e9f0b56c-36a5-11ef-8283-00155d3a40eb', 'system_file_query', '文件查询', 'system:file:query', 'c9ab68d5-36a5-11ef-8283-00155d3a40eb', 'admin', '2024-06-30 13:59:29'),
	('fe8a5eef-36a5-11ef-8283-00155d3a40eb', 'system_file_add', '文件新增', 'system:file:add', 'c9ab68d5-36a5-11ef-8283-00155d3a40eb', 'admin', '2024-06-30 14:00:03'),
	('0c42e874-36a6-11ef-8283-00155d3a40eb', 'system_file_edit', '文件修改', 'system:file:edit', 'c9ab68d5-36a5-11ef-8283-00155d3a40eb', 'admin', '2024-06-30 14:00:26'),
	('15db031a-36a6-11ef-8283-00155d3a40eb', 'system_file_delete', '文件删除', 'system:file:delete', 'c9ab68d5-36a5-11ef-8283-00155d3a40eb', 'admin', '2024-06-30 14:00:43'),
	('24e2a561-36a6-11ef-8283-00155d3a40eb', 'system_file_download', '文件下载', 'system:file:download', 'c9ab68d5-36a5-11ef-8283-00155d3a40eb', 'admin', '2024-06-30 14:01:08'),
	('1bff0bc7-3d84-11ef-81fc-00155d791683', 'system_dict', '字典管理', 'system:dict', 'a8c8fc58-fd1d-11ee-9405-f0d41530a047', 'admin', '2024-07-09 07:45:08'),
	('3d69df81-3d84-11ef-81fc-00155d791683', 'system_dict_query', '字典查询', 'system:dict:query', '1bff0bc7-3d84-11ef-81fc-00155d791683', 'admin', '2024-07-09 07:46:04'),
	('4d15fa66-3d84-11ef-81fc-00155d791683', 'system_dict_add', '字典新增', 'system:dict:add', '1bff0bc7-3d84-11ef-81fc-00155d791683', 'admin', '2024-07-09 07:46:31'),
	('611fb075-3d84-11ef-81fc-00155d791683', 'system_dict_edit', '字典修改', 'system:dict:edit', '1bff0bc7-3d84-11ef-81fc-00155d791683', 'admin', '2024-07-09 07:47:04'),
	('6d7cd70b-3d84-11ef-81fc-00155d791683', 'system_dict_delete', '字典删除', 'system:dict:delete', '1bff0bc7-3d84-11ef-81fc-00155d791683', 'admin', '2024-07-09 07:47:25'),
	('0ab4e3e1-0745-11ef-88a5-c6d2cc8741ac', 'system_user_query', '用户查询', 'system:user:query', 'e13f27bd-06da-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 06:57:38'),
	('1ce1e75f-0745-11ef-88a5-c6d2cc8741ac', 'system_user_add', '用户新增', 'system:user:add', 'e13f27bd-06da-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 06:58:09'),
	('aab253bf-0745-11ef-88a5-c6d2cc8741ac', 'system_user_edit', '用户修改', 'system:user:edit', 'e13f27bd-06da-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 07:02:06'),
	('bee5af14-0745-11ef-88a5-c6d2cc8741ac', 'system_user_delete', '用户删除', 'system:user:delete', 'e13f27bd-06da-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 07:02:40'),
	('f55e7164-0745-11ef-88a5-c6d2cc8741ac', 'system_role_query', '角色查询', 'system:role:query', '458e05f5-06e1-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 07:04:12'),
	('fe23035c-0745-11ef-88a5-c6d2cc8741ac', 'system_role_add', '角色新增', 'system:role:add', '458e05f5-06e1-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 07:04:26'),
	('05c17b81-0746-11ef-88a5-c6d2cc8741ac', 'system_role_edit', '角色修改', 'system:role:edit', '458e05f5-06e1-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 07:04:39'),
	('1032e829-0746-11ef-88a5-c6d2cc8741ac', 'system_role_delete', '角色删除', 'system:role:delete', '458e05f5-06e1-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 07:04:57'),
	('54a89b10-0746-11ef-88a5-c6d2cc8741ac', 'system_menu_query', '菜单查询', 'system:menu:query', 'f5fb4553-06e1-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 07:06:52'),
	('4b7f8977-0748-11ef-88a5-c6d2cc8741ac', 'system_menu_add', '菜单新增', 'system:menu:add', 'f5fb4553-06e1-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 07:20:55'),
	('554ae313-0748-11ef-88a5-c6d2cc8741ac', 'system_menu_edit', '菜单修改', 'system:menu:edit', 'f5fb4553-06e1-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 07:21:12'),
	('5f4dbce5-0748-11ef-88a5-c6d2cc8741ac', 'system_menu_delete', '菜单删除', 'system:menu:delete', 'f5fb4553-06e1-11ef-892a-c7d9e8014b74', 'nobume', '2024-05-01 07:21:28'),
	('e13f27bd-06da-11ef-892a-c7d9e8014b74', 'system_user', '用户管理', 'system:user', 'a8c8fc58-fd1d-11ee-9405-f0d41530a047', 'nobume', '2024-04-30 18:17:43'),
	('458e05f5-06e1-11ef-892a-c7d9e8014b74', 'system_role', '角色管理', 'system:role', 'a8c8fc58-fd1d-11ee-9405-f0d41530a047', 'nobume', '2024-04-30 19:03:28'),
	('f5fb4553-06e1-11ef-892a-c7d9e8014b74', 'system_menu', '菜单管理', 'system:menu', 'a8c8fc58-fd1d-11ee-9405-f0d41530a047', 'nobume', '2024-04-30 19:08:24'),
	('a8c8fc58-fd1d-11ee-9405-f0d41530a047', 'system', '系统管理', 'system', NULL, 'admin', '2024-04-18 08:50:02');

-- 导出  表 colonq.role_info 结构
CREATE TABLE IF NOT EXISTS `role_info` (
  `role_id` uuid NOT NULL DEFAULT uuid() COMMENT '角色ID',
  `role_name` varchar(20) NOT NULL COMMENT '角色名',
  `role_label` varchar(20) NOT NULL COMMENT '角色标签',
  `create_name` varchar(20) NOT NULL COMMENT '创建人名',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`),
  KEY `search` (`role_label`,`create_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';

-- 正在导出表  colonq.role_info 的数据：~0 rows (大约)
INSERT INTO `role_info` (`role_id`, `role_name`, `role_label`, `create_name`, `create_time`) VALUES
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'admin', '管理员', 'admin', '2024-04-17 15:52:04');

-- 导出  表 colonq.role_menu_link 结构
CREATE TABLE IF NOT EXISTS `role_menu_link` (
  `role_id` uuid NOT NULL COMMENT '角色ID',
  `menu_id` uuid NOT NULL COMMENT '菜单ID',
  KEY `id` (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单链接表';

-- 正在导出表  colonq.role_menu_link 的数据：~29 rows (大约)
INSERT INTO `role_menu_link` (`role_id`, `menu_id`) VALUES
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'f1bfc34f-473b-11ef-81f2-00155d2df2bf'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '02876651-473c-11ef-81f2-00155d2df2bf'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'c9ab68d5-36a5-11ef-8283-00155d3a40eb'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'e9f0b56c-36a5-11ef-8283-00155d3a40eb'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'fe8a5eef-36a5-11ef-8283-00155d3a40eb'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '0c42e874-36a6-11ef-8283-00155d3a40eb'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '15db031a-36a6-11ef-8283-00155d3a40eb'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '24e2a561-36a6-11ef-8283-00155d3a40eb'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '1bff0bc7-3d84-11ef-81fc-00155d791683'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '3d69df81-3d84-11ef-81fc-00155d791683'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '4d15fa66-3d84-11ef-81fc-00155d791683'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '611fb075-3d84-11ef-81fc-00155d791683'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '6d7cd70b-3d84-11ef-81fc-00155d791683'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '0ab4e3e1-0745-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '1ce1e75f-0745-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'aab253bf-0745-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'bee5af14-0745-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'f55e7164-0745-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'fe23035c-0745-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '05c17b81-0746-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '1032e829-0746-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '54a89b10-0746-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '4b7f8977-0748-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '554ae313-0748-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '5f4dbce5-0748-11ef-88a5-c6d2cc8741ac'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'e13f27bd-06da-11ef-892a-c7d9e8014b74'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', '458e05f5-06e1-11ef-892a-c7d9e8014b74'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'f5fb4553-06e1-11ef-892a-c7d9e8014b74'),
	('710d2766-fc8f-11ee-9405-f0d41530a047', 'a8c8fc58-fd1d-11ee-9405-f0d41530a047');

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
  UNIQUE KEY `user_name` (`user_name`),
  KEY `search` (`email`,`create_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';

-- 正在导出表  colonq.user_info 的数据：~1 rows (大约)
INSERT INTO `user_info` (`user_id`, `user_name`, `password`, `email`, `salt`, `create_name`, `create_time`) VALUES
	('dca2a4e6-f9f2-11ee-9405-f0d41530a047', 'admin', '*6BB4837EB74329105EE4568DDA7DC67ED2CA2AD9', 'zwj-nobume@outlook.com', 'df99d548638413c78ba783269280d69529ee60bd1af72fd6478bef0019496d76', 'admin', '2024-04-14 08:06:35');

-- 导出  表 colonq.user_role_link 结构
CREATE TABLE IF NOT EXISTS `user_role_link` (
  `user_id` uuid NOT NULL COMMENT '用户ID',
  `role_id` uuid NOT NULL COMMENT '角色ID',
  KEY `id` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色链接表';

-- 正在导出表  colonq.user_role_link 的数据：~0 rows (大约)
INSERT INTO `user_role_link` (`user_id`, `role_id`) VALUES
	('dca2a4e6-f9f2-11ee-9405-f0d41530a047', '710d2766-fc8f-11ee-9405-f0d41530a047');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
