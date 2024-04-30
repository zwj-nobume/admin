-- MariaDB dump 10.19-11.3.2-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: colonq
-- ------------------------------------------------------
-- Server version	11.3.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `menu_info`
--

-- 导出 colonq 的数据库结构
DROP DATABASE IF EXISTS `colonq`;
CREATE DATABASE IF NOT EXISTS `colonq` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `colonq`;

DROP TABLE IF EXISTS `menu_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menu_info` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_info`
--

LOCK TABLES `menu_info` WRITE;
/*!40000 ALTER TABLE `menu_info` DISABLE KEYS */;
INSERT INTO `menu_info` VALUES
('0ab4e3e1-0745-11ef-88a5-c6d2cc8741ac','system_user_page','用户分页','system:user:page','e13f27bd-06da-11ef-892a-c7d9e8014b74','nobume','2024-05-01 06:57:38'),
('1ce1e75f-0745-11ef-88a5-c6d2cc8741ac','system_user_add','用户新增','system:user:add','e13f27bd-06da-11ef-892a-c7d9e8014b74','nobume','2024-05-01 06:58:09'),
('aab253bf-0745-11ef-88a5-c6d2cc8741ac','system_user_edit','用户修改','system:user:edit','e13f27bd-06da-11ef-892a-c7d9e8014b74','nobume','2024-05-01 07:02:06'),
('bee5af14-0745-11ef-88a5-c6d2cc8741ac','system_user_delete','用户删除','system:user:delete','e13f27bd-06da-11ef-892a-c7d9e8014b74','nobume','2024-05-01 07:02:40'),
('f55e7164-0745-11ef-88a5-c6d2cc8741ac','system_role_page','角色分页','system:role:page','458e05f5-06e1-11ef-892a-c7d9e8014b74','nobume','2024-05-01 07:04:12'),
('fe23035c-0745-11ef-88a5-c6d2cc8741ac','system_role_add','角色新增','system:role:add','458e05f5-06e1-11ef-892a-c7d9e8014b74','nobume','2024-05-01 07:04:26'),
('05c17b81-0746-11ef-88a5-c6d2cc8741ac','system_role_edit','角色修改','system:role:edit','458e05f5-06e1-11ef-892a-c7d9e8014b74','nobume','2024-05-01 07:04:39'),
('1032e829-0746-11ef-88a5-c6d2cc8741ac','system_role_delete','角色删除','system:role:delete','458e05f5-06e1-11ef-892a-c7d9e8014b74','nobume','2024-05-01 07:04:57'),
('54a89b10-0746-11ef-88a5-c6d2cc8741ac','system_menu_page','菜单分页','system:menu:page','f5fb4553-06e1-11ef-892a-c7d9e8014b74','nobume','2024-05-01 07:06:52'),
('4b7f8977-0748-11ef-88a5-c6d2cc8741ac','system_menu_add','菜单新增','system:menu:add','f5fb4553-06e1-11ef-892a-c7d9e8014b74','nobume','2024-05-01 07:20:55'),
('554ae313-0748-11ef-88a5-c6d2cc8741ac','system_menu_edit','菜单修改','system:menu:edit','f5fb4553-06e1-11ef-892a-c7d9e8014b74','nobume','2024-05-01 07:21:12'),
('5f4dbce5-0748-11ef-88a5-c6d2cc8741ac','system_menu_delete','菜单删除','system:menu:delete','f5fb4553-06e1-11ef-892a-c7d9e8014b74','nobume','2024-05-01 07:21:28'),
('e13f27bd-06da-11ef-892a-c7d9e8014b74','system_user','用户管理','system:user','a8c8fc58-fd1d-11ee-9405-f0d41530a047','nobume','2024-04-30 18:17:43'),
('458e05f5-06e1-11ef-892a-c7d9e8014b74','system_role','角色管理','system:role','a8c8fc58-fd1d-11ee-9405-f0d41530a047','nobume','2024-04-30 19:03:28'),
('f5fb4553-06e1-11ef-892a-c7d9e8014b74','system_menu','菜单管理','system:menu','a8c8fc58-fd1d-11ee-9405-f0d41530a047','nobume','2024-04-30 19:08:24'),
('a8c8fc58-fd1d-11ee-9405-f0d41530a047','system','系统管理','system',NULL,'admin','2024-04-18 08:50:02');
/*!40000 ALTER TABLE `menu_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_info`
--

DROP TABLE IF EXISTS `role_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_info` (
  `role_id` uuid NOT NULL DEFAULT uuid() COMMENT '角色ID',
  `role_name` varchar(20) NOT NULL COMMENT '角色名',
  `role_label` varchar(20) NOT NULL COMMENT '角色标签',
  `create_name` varchar(20) NOT NULL COMMENT '创建人名',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_info`
--

LOCK TABLES `role_info` WRITE;
/*!40000 ALTER TABLE `role_info` DISABLE KEYS */;
INSERT INTO `role_info` VALUES
('710d2766-fc8f-11ee-9405-f0d41530a047','admin','管理员','admin','2024-04-17 15:52:04');
/*!40000 ALTER TABLE `role_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_menu_link`
--

DROP TABLE IF EXISTS `role_menu_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_menu_link` (
  `role_id` uuid NOT NULL COMMENT '角色ID',
  `menu_id` uuid NOT NULL COMMENT '菜单ID',
  KEY `id` (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单链接表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_menu_link`
--

LOCK TABLES `role_menu_link` WRITE;
/*!40000 ALTER TABLE `role_menu_link` DISABLE KEYS */;
INSERT INTO `role_menu_link` VALUES
('710d2766-fc8f-11ee-9405-f0d41530a047','0ab4e3e1-0745-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','1ce1e75f-0745-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','aab253bf-0745-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','bee5af14-0745-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','f55e7164-0745-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','fe23035c-0745-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','05c17b81-0746-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','1032e829-0746-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','54a89b10-0746-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','4b7f8977-0748-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','554ae313-0748-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','5f4dbce5-0748-11ef-88a5-c6d2cc8741ac'),
('710d2766-fc8f-11ee-9405-f0d41530a047','e13f27bd-06da-11ef-892a-c7d9e8014b74'),
('710d2766-fc8f-11ee-9405-f0d41530a047','458e05f5-06e1-11ef-892a-c7d9e8014b74'),
('710d2766-fc8f-11ee-9405-f0d41530a047','f5fb4553-06e1-11ef-892a-c7d9e8014b74'),
('710d2766-fc8f-11ee-9405-f0d41530a047','a8c8fc58-fd1d-11ee-9405-f0d41530a047');
/*!40000 ALTER TABLE `role_menu_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_info` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES
('dca2a4e6-f9f2-11ee-9405-f0d41530a047','admin','*6BB4837EB74329105EE4568DDA7DC67ED2CA2AD9','zwj-nobume@outlook.com','4d7ffba28355cc66aeba8974082de9c87ec479f45b692f0824bde938fb583543','admin','2024-04-14 08:06:35'),
('d08ecc4d-fb30-11ee-9405-f0d41530a047','nobume','*7F4F8F39C0B8B1348AC226960F5856E6B00DFDCE','zwj-nobume@outlook.com','021aad0b31f2b694b763dfa058718079cfc5e04d62955f9fc0be03858e049165','admin','2024-04-15 22:02:20');
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role_link`
--

DROP TABLE IF EXISTS `user_role_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role_link` (
  `user_id` uuid NOT NULL COMMENT '用户ID',
  `role_id` uuid NOT NULL COMMENT '角色ID',
  KEY `id` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色链接表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role_link`
--

LOCK TABLES `user_role_link` WRITE;
/*!40000 ALTER TABLE `user_role_link` DISABLE KEYS */;
INSERT INTO `user_role_link` VALUES
('dca2a4e6-f9f2-11ee-9405-f0d41530a047','710d2766-fc8f-11ee-9405-f0d41530a047'),
('d08ecc4d-fb30-11ee-9405-f0d41530a047','710d2766-fc8f-11ee-9405-f0d41530a047');
/*!40000 ALTER TABLE `user_role_link` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-01  7:47:39
