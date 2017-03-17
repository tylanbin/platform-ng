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
  KEY `FK_emp_org` (`orgId`),
  CONSTRAINT `FK_emp_org` FOREIGN KEY (`orgId`) REFERENCES `ng_sys_org` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  KEY `FK_org_self` (`parentId`),
  CONSTRAINT `FK_org_self` FOREIGN KEY (`parentId`) REFERENCES `ng_sys_org` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ng_sys_perm` */

DROP TABLE IF EXISTS `ng_sys_perm`;

CREATE TABLE `ng_sys_perm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parentId` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_perm_self` (`parentId`),
  CONSTRAINT `FK_perm_self` FOREIGN KEY (`parentId`) REFERENCES `ng_sys_perm` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ng_sys_role` */

DROP TABLE IF EXISTS `ng_sys_role`;

CREATE TABLE `ng_sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orgId` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_role_org` (`orgId`),
  CONSTRAINT `FK_role_org` FOREIGN KEY (`orgId`) REFERENCES `ng_sys_org` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ng_sys_role_perm` */

DROP TABLE IF EXISTS `ng_sys_role_perm`;

CREATE TABLE `ng_sys_role_perm` (
  `roleId` int(11) NOT NULL,
  `permId` int(11) NOT NULL,
  PRIMARY KEY (`roleId`,`permId`),
  KEY `FK_roleperm_perm` (`permId`),
  KEY `FK_roleperm_role` (`roleId`),
  CONSTRAINT `FK_roleperm_perm` FOREIGN KEY (`permId`) REFERENCES `ng_sys_perm` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_roleperm_role` FOREIGN KEY (`roleId`) REFERENCES `ng_sys_role` (`id`) ON DELETE CASCADE
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
  KEY `FK_user_emp` (`empId`),
  CONSTRAINT `FK_user_emp` FOREIGN KEY (`empId`) REFERENCES `ng_sys_emp` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ng_sys_user_role` */

DROP TABLE IF EXISTS `ng_sys_user_role`;

CREATE TABLE `ng_sys_user_role` (
  `userId` int(11) NOT NULL,
  `roleId` int(11) NOT NULL,
  PRIMARY KEY (`userId`,`roleId`),
  KEY `FK_userrole_role` (`roleId`),
  KEY `FK_userrole_user` (`userId`),
  CONSTRAINT `FK_userrole_role` FOREIGN KEY (`roleId`) REFERENCES `ng_sys_role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_userrole_user` FOREIGN KEY (`userId`) REFERENCES `ng_sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
