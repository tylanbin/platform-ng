/*
SQLyog Ultimate v8.32 
MySQL - 5.5.24 : Database - platform_ng
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`platform_ng` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `platform_ng`;

/*Table structure for table `ng_demo_foo` */

DROP TABLE IF EXISTS `ng_demo_foo`;

CREATE TABLE `ng_demo_foo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `col1` int(11) DEFAULT NULL,
  `col2` varchar(255) DEFAULT NULL,
  `col3` double DEFAULT NULL,
  `col4` date DEFAULT NULL,
  `col5` datetime DEFAULT NULL,
  `col6` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ng_sys_emp` */

DROP TABLE IF EXISTS `ng_sys_emp`;

CREATE TABLE `ng_sys_emp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orgId` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `job` varchar(255) DEFAULT NULL,
  `education` varchar(255) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `contact` varchar(255) DEFAULT NULL,
  `idCard` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `isOnJob` int(11) DEFAULT NULL,
  `dateOfEntry` date DEFAULT NULL,
  `dateOfConfirm` date DEFAULT NULL,
  `dateOfLeave` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKC40F33D0E2CBBD8B` (`orgId`),
  CONSTRAINT `FKC40F33D0E2CBBD8B` FOREIGN KEY (`orgId`) REFERENCES `ng_sys_org` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Table structure for table `ng_sys_org` */

DROP TABLE IF EXISTS `ng_sys_org`;

CREATE TABLE `ng_sys_org` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parentId` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `serialNum` varchar(255) DEFAULT NULL,
  `workPlace` varchar(255) DEFAULT NULL,
  `contact` varchar(255) DEFAULT NULL,
  `leader` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKC40F59EC2285BDD1` (`parentId`),
  CONSTRAINT `FKC40F59EC2285BDD1` FOREIGN KEY (`parentId`) REFERENCES `ng_sys_org` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Table structure for table `ng_sys_perm` */

DROP TABLE IF EXISTS `ng_sys_perm`;

CREATE TABLE `ng_sys_perm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parentId` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKBDDC28E8F8D6FF8D` (`parentId`),
  CONSTRAINT `FKBDDC28E8F8D6FF8D` FOREIGN KEY (`parentId`) REFERENCES `ng_sys_perm` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ng_sys_role` */

DROP TABLE IF EXISTS `ng_sys_role`;

CREATE TABLE `ng_sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orgId` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKBDDD366EE2CBBD8B` (`orgId`),
  CONSTRAINT `FKBDDD366EE2CBBD8B` FOREIGN KEY (`orgId`) REFERENCES `ng_sys_org` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Table structure for table `ng_sys_role_perm` */

DROP TABLE IF EXISTS `ng_sys_role_perm`;

CREATE TABLE `ng_sys_role_perm` (
  `roleId` int(11) NOT NULL,
  `permId` int(11) NOT NULL,
  PRIMARY KEY (`roleId`,`permId`),
  KEY `FK4B18CA2177AEF053` (`permId`),
  KEY `FK4B18CA217BA3C1DF` (`roleId`),
  CONSTRAINT `FK4B18CA2177AEF053` FOREIGN KEY (`permId`) REFERENCES `ng_sys_perm` (`id`),
  CONSTRAINT `FK4B18CA217BA3C1DF` FOREIGN KEY (`roleId`) REFERENCES `ng_sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ng_sys_user` */

DROP TABLE IF EXISTS `ng_sys_user`;

CREATE TABLE `ng_sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `empId` int(11) DEFAULT NULL,
  `loginName` varchar(255) NOT NULL,
  `loginPass` varchar(255) NOT NULL,
  `enabled` int(11) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `loginRange` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKBDDEA1C3E23C8853` (`empId`),
  CONSTRAINT `FKBDDEA1C3E23C8853` FOREIGN KEY (`empId`) REFERENCES `ng_sys_emp` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Table structure for table `ng_sys_user_role` */

DROP TABLE IF EXISTS `ng_sys_user_role`;

CREATE TABLE `ng_sys_user_role` (
  `userId` int(11) NOT NULL,
  `roleId` int(11) NOT NULL,
  PRIMARY KEY (`userId`,`roleId`),
  KEY `FK4B4F39727BA3C1DF` (`roleId`),
  KEY `FK4B4F397280F91749` (`userId`),
  CONSTRAINT `FK4B4F39727BA3C1DF` FOREIGN KEY (`roleId`) REFERENCES `ng_sys_role` (`id`),
  CONSTRAINT `FK4B4F397280F91749` FOREIGN KEY (`userId`) REFERENCES `ng_sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
