-- MySQL dump 10.13  Distrib 8.0.18, for Win64 (x86_64)
--
-- Host: localhost    Database: volleyball
-- ------------------------------------------------------
-- Server version	8.0.18

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- create database and use
--

DROP DATABASE IF EXISTS `volleyball`;
CREATE DATABASE `volleyball`;
USE `volleyball`;
--
-- create user
--
DROP USER IF EXISTS 'volleyball'@'%';
CREATE USER 'volleyball'@'%' identified BY 'volleyball';
GRANT ALL PRIVILEGES ON `volleyball`.* TO 'volleyball'@'%' WITH GRANT OPTION;
FLUSH privileges;

SET FOREIGN_KEY_CHECKS=0;


-- ----------------------------
-- Table structure for `application`
-- ----------------------------
DROP TABLE IF EXISTS `application`;
CREATE TABLE `application` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ap_type_id` int NOT NULL,
  `ap_user_id` int NOT NULL,
  `ap_user_name` varchar(255) DEFAULT NULL,
  `time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '未处理',
  `deal_user_id` int DEFAULT '1',
  `deal_user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_application_ap_type_id` (`ap_type_id`),
  KEY `fk_application_ap_user_id` (`ap_user_id`),
  KEY `fk_application_deal_user_id` (`deal_user_id`),
  CONSTRAINT `fk_application_ap_type_id` FOREIGN KEY (`ap_type_id`) REFERENCES `ap_type` (`id`),
  CONSTRAINT `fk_application_ap_user_id` FOREIGN KEY (`ap_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_application_deal_user_id` FOREIGN KEY (`deal_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ----------------------------
-- Table structure for `ap_type`
-- ----------------------------
DROP TABLE IF EXISTS `ap_type`;
CREATE TABLE `ap_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of ap_type
-- ----------------------------
INSERT INTO `ap_type` VALUES ('1', '默认申请');
INSERT INTO `ap_type` VALUES ('2', '用户申请记分员');
INSERT INTO `ap_type` VALUES ('3', '用户申请管理员');

-- ----------------------------
-- Table structure for `history`
-- ----------------------------
DROP TABLE IF EXISTS `history`;
CREATE TABLE `history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matchs_id` int NOT NULL,
  `user_id` int NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_history_matchs_id` (`matchs_id`),
  KEY `fk_history_user_id` (`user_id`),
  CONSTRAINT `fk_history_matchs_id` FOREIGN KEY (`matchs_id`) REFERENCES `matchs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_history_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of history
-- ----------------------------
INSERT INTO `history` VALUES ('1', '1', '1', null, '暂无记录', '2022-07-01 10:25:08');


-- ----------------------------
-- Table structure for `matchs`
-- ----------------------------
DROP TABLE IF EXISTS `matchs`;
CREATE TABLE `matchs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `rules` varchar(255) DEFAULT NULL,
  `start_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `end_time` timestamp NULL DEFAULT NULL,
  `sponsor` varchar(255) DEFAULT NULL,
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '准备',
  `scorer_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=252 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of matchs
-- ----------------------------
INSERT INTO `matchs` VALUES ('1', '暂无比赛', null, null, null, null, null, null);


-- ----------------------------
-- Table structure for `member`
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
  `id` int NOT NULL AUTO_INCREMENT,
  `team_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `fk_team_id` (`team_id`),
  CONSTRAINT `fk_member_team_id` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of member
-- ----------------------------
INSERT INTO `member` VALUES ('1', '1', '暂无队员', '');
INSERT INTO `member` VALUES ('2', '5', '王缓缓', '副攻');
INSERT INTO `member` VALUES ('3', '5', '袁心玥', '副攻');
INSERT INTO `member` VALUES ('4', '5', '丁霞', '二传');
INSERT INTO `member` VALUES ('5', '5', '龚翔宇', '接应');
INSERT INTO `member` VALUES ('6', '5', '王梦洁', '自由人');
INSERT INTO `member` VALUES ('7', '2', '普拉默', '主攻');
INSERT INTO `member` VALUES ('8', '2', '威尔海特', '主攻');
INSERT INTO `member` VALUES ('9', '2', '迪克森', '副攻');
INSERT INTO `member` VALUES ('10', '2', '汉考克', '二传');
INSERT INTO `member` VALUES ('11', '2', '德鲁斯', '接应');
INSERT INTO `member` VALUES ('12', '2', '怀特', '自由人');
INSERT INTO `member` VALUES ('13', '3', '李软经', '主攻');
INSERT INTO `member` VALUES ('14', '3', '梁孝贞', '副攻');
INSERT INTO `member` VALUES ('15', '3', '金溪镇', '接应');
INSERT INTO `member` VALUES ('16', '3', '安慧珍', '二传');
INSERT INTO `member` VALUES ('17', '3', '联惠善', '二传');
INSERT INTO `member` VALUES ('18', '3', '吴智英', '自由人');
INSERT INTO `member` VALUES ('19', '4', '沃隆科瓦', '主攻');
INSERT INTO `member` VALUES ('20', '4', '斯米尔诺娃', '主攻');
INSERT INTO `member` VALUES ('21', '4', '罗列娃', '副攻');
INSERT INTO `member` VALUES ('22', '4', '拉扎列娃', '接应');
INSERT INTO `member` VALUES ('23', '4', '马特维耶娃', '二传');
INSERT INTO `member` VALUES ('24', '4', '玛洛瓦', '自由人');
INSERT INTO `member` VALUES ('25', '5', '王婷', '主攻');


-- ----------------------------
-- Table structure for `record`
-- ----------------------------
DROP TABLE IF EXISTS `record`;
CREATE TABLE `record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `session` int NOT NULL DEFAULT '0',
  `inning` int NOT NULL DEFAULT '0',
  `matchs_id` int NOT NULL,
  `member_id` int NOT NULL,
  `team_id` int NOT NULL,
  `score_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`session`,`inning`),
  KEY `fk_record_matchs_id` (`matchs_id`),
  KEY `fk_record_member_id` (`member_id`),
  KEY `record_session` (`session`) USING BTREE,
  KEY `record_inning` (`inning`) USING BTREE,
  KEY `fk_record_team_id` (`team_id`),
  CONSTRAINT `fk_record_matchs_id` FOREIGN KEY (`matchs_id`) REFERENCES `matchs` (`id`) ON UPDATE RESTRICT,
  CONSTRAINT `fk_record_member_id` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_record_team_id` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3319 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ----------------------------
-- Table structure for `score`
-- ----------------------------
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matchs_id` int NOT NULL,
  `team_id` int NOT NULL,
  `d_score` int DEFAULT '0',
  `x_score` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_score_matchs_id` (`matchs_id`),
  KEY `fk_score_team_id` (`team_id`),
  CONSTRAINT `fk_score_matchs_id` FOREIGN KEY (`matchs_id`) REFERENCES `matchs` (`id`),
  CONSTRAINT `fk_score_team_id` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=258 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ----------------------------
-- Table structure for `team`
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matchs_id` int DEFAULT '1',
  `name` varchar(255) NOT NULL,
  `coach` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_team_matchs_id` (`matchs_id`),
  CONSTRAINT `fk_team_matchs_id` FOREIGN KEY (`matchs_id`) REFERENCES `matchs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of team
-- ----------------------------
INSERT INTO `team` VALUES ('1', '1', '暂无队伍', '');
INSERT INTO `team` VALUES ('2', '1', '美国女子排球队', '汤姆罗根');
INSERT INTO `team` VALUES ('3', '1', '韩国女子排球队', '塞萨尔');
INSERT INTO `team` VALUES ('4', '1', '俄罗斯女子排球队', '奥维贫尼科夫');
INSERT INTO `team` VALUES ('5', '1', '中国女子排球队', '蔡斌');


-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '暂无用户', '无', null, null);
INSERT INTO `user` VALUES ('2', 'admin', 'admin66', 'admin@a.com', '管理员');


-- ----------------------------
-- Table structure for `vp`
-- ----------------------------
DROP TABLE IF EXISTS `vp`;
CREATE TABLE `vp` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matchs_id` int NOT NULL,
  `member_id` int NOT NULL,
  `gain_scores` int DEFAULT NULL,
  `continue_score` int DEFAULT NULL,
  `continue_num` int DEFAULT NULL,
  `evaluate` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT 'mvp',
  PRIMARY KEY (`id`),
  KEY `fk_vp_matchs_id` (`matchs_id`),
  KEY `fk_vp_member_id` (`member_id`),
  CONSTRAINT `fk_vp_matchs_id` FOREIGN KEY (`matchs_id`) REFERENCES `matchs` (`id`),
  CONSTRAINT `fk_vp_member_id` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of vp
-- ----------------------------


-- ----------------------------
-- drop trigger
-- ----------------------------
DROP TRIGGER IF EXISTS `update_application_ap_type_id`;
DELIMITER ;;
CREATE TRIGGER `update_application_ap_type_id` BEFORE DELETE ON `ap_type` FOR EACH ROW if (exists(select * from application where ap_type_id=old.id))
then
 update application set ap_type_id=1 where ap_type_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `delete_record_matchs_id`;
DELIMITER ;;
CREATE TRIGGER `delete_record_matchs_id` BEFORE DELETE ON `matchs` FOR EACH ROW if (exists(select * from record where matchs_id=old.id))
then
 delete from record where matchs_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `delete_score_matchs_id`;
DELIMITER ;;
CREATE TRIGGER `delete_score_matchs_id` BEFORE DELETE ON `matchs` FOR EACH ROW if (exists(select * from score where matchs_id=old.id))
then
 delete from score where matchs_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `update_team_matchs_id`;
DELIMITER ;;
CREATE TRIGGER `update_team_matchs_id` BEFORE DELETE ON `matchs` FOR EACH ROW if (exists(select * from team where matchs_id=old.id))
then
 update team set matchs_id=1 where matchs_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `delete_vp_matchs_id`;
DELIMITER ;;

-- ----------------------------
-- create trigger
-- ----------------------------
CREATE TRIGGER `delete_vp_matchs_id` BEFORE DELETE ON `matchs` FOR EACH ROW if (exists(select * from vp where matchs_id=old.id))
then
 delete from vp where matchs_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `delete_history_matchs_id`;
DELIMITER ;;
CREATE TRIGGER `delete_history_matchs_id` BEFORE DELETE ON `matchs` FOR EACH ROW if (exists(select * from history where matchs_id=old.id))
then
 delete from history where matchs_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `update_vp_member_id`;
DELIMITER ;;
CREATE TRIGGER `update_vp_member_id` BEFORE DELETE ON `member` FOR EACH ROW if (exists(select * from vp where member_id=old.id))
then
 update vp set matchs_id=1 where member_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `update_record_member_id`;
DELIMITER ;;
CREATE TRIGGER `update_record_member_id` BEFORE DELETE ON `member` FOR EACH ROW if (exists(select * from record where member_id=old.id))
then
 update record set  member_id=1 where member_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `update_record_team_id`;
DELIMITER ;;
CREATE TRIGGER `update_record_team_id` BEFORE DELETE ON `team` FOR EACH ROW if (exists(select * from record where team_id=old.id))
then
 update record set  team_id=1 where team_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `update_score_team_id`;
DELIMITER ;;
CREATE TRIGGER `update_score_team_id` BEFORE DELETE ON `team` FOR EACH ROW if (exists(select * from record where team_id=old.id))
then
 update score set  team_id=1 where team_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `update_member_team_id`;
DELIMITER ;;
CREATE TRIGGER `update_member_team_id` BEFORE DELETE ON `team` FOR EACH ROW if (exists(select * from `member` where team_id=old.id))
then
 update `member` set team_id=1 where team_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `delete_application_ap_user_id`;
DELIMITER ;;
CREATE TRIGGER `delete_application_ap_user_id` BEFORE DELETE ON `user` FOR EACH ROW if (exists(select * from application where ap_user_id=old.id))
then
 delete from application where ap_user_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `delete_application_deal_user_id`;
DELIMITER ;;
CREATE TRIGGER `delete_application_deal_user_id` BEFORE DELETE ON `user` FOR EACH ROW if (exists(select * from application where deal_user_id=old.id))
then
 delete from application where deal_user_id=old.id;
end if
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `update_history_user_id`;
DELIMITER ;;
CREATE TRIGGER `update_history_user_id` BEFORE DELETE ON `user` FOR EACH ROW if (exists(select * from history where user_id=old.id))
then
 update history set user_id=1 where user_id=old.id;
end if
;;
DELIMITER ;
