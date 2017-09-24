/*!40101 SET NAMES utf8 */;
/*!40101 SET SQL_MODE=''*/;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Data for the table `act_id_group` */
insert  into `act_id_group`(`ID_`,`REV_`,`NAME_`,`TYPE_`) values ('1',1,'公司管理员',NULL),('2',1,'项目经理',NULL),('3',1,'软件工程师',NULL);
/*Data for the table `act_id_user` */
insert  into `act_id_user`(`ID_`,`REV_`,`FIRST_`,`LAST_`,`EMAIL_`,`PWD_`,`PICTURE_ID_`) values ('1',1,NULL,'zhangsan',NULL,'42EB2E42C2806CDAFA2F4E28591B5083',NULL),('2',1,NULL,'lisi',NULL,'42EB2E42C2806CDAFA2F4E28591B5083',NULL),('3',1,NULL,'wangwu',NULL,'42EB2E42C2806CDAFA2F4E28591B5083',NULL),('4',2,NULL,'admin',NULL,'CBDF53E5E55C9158755627DC228E16A9',NULL),('5',1,NULL,'root',NULL,'84816AFDC390BC7EE12BEBF1302E7AD5',NULL);
/*Data for the table `act_id_membership` */
insert  into `act_id_membership`(`USER_ID_`,`GROUP_ID_`) values ('4','1'),('5','1'),('1','2'),('2','3'),('3','3');
/*Data for the table `ng_sys_org` */
insert  into `ng_sys_org`(`id`,`contact`,`leader`,`name`,`serialNum`,`workPlace`,`parentId`) values (1,'','','示例公司','demo','',NULL),(2,'','','市场部','demo-1','',1),(3,'','','行政部','demo-2','',1),(4,'','','研发部','demo-3','',1);
/*Data for the table `ng_sys_emp` */
insert  into `ng_sys_emp`(`id`,`birthday`,`contact`,`dateOfConfirm`,`dateOfEntry`,`dateOfLeave`,`education`,`email`,`gender`,`idCard`,`isOnJob`,`job`,`name`,`orgId`) values (1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'男',NULL,NULL,NULL,'张三',4),(2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'男',NULL,NULL,NULL,'李四',4),(3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'男',NULL,NULL,NULL,'王五',4),(4,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'男',NULL,NULL,NULL,'管理员',1);
/*Data for the table `ng_sys_role` */
insert  into `ng_sys_role`(`id`,`description`,`name`,`orgId`) values (1,'公司管理员','公司管理员',1),(2,'项目经理','项目经理',4),(3,'软件工程师','软件工程师',4);
/*Data for the table `ng_sys_user` */
insert  into `ng_sys_user`(`id`,`createDate`,`enabled`,`loginName`,`loginPass`,`loginRange`,`empId`) values (1,'2017-02-08 14:02:35',1,'zhangsan','42EB2E42C2806CDAFA2F4E28591B5083',NULL,1),(2,'2017-02-08 14:04:44',1,'lisi','42EB2E42C2806CDAFA2F4E28591B5083',NULL,2),(3,'2017-02-08 14:04:53',1,'wangwu','42EB2E42C2806CDAFA2F4E28591B5083',NULL,3),(4,'2017-02-08 14:56:55',1,'admin','CBDF53E5E55C9158755627DC228E16A9',NULL,4),(5,'2017-02-08 14:57:05',1,'root','84816AFDC390BC7EE12BEBF1302E7AD5',NULL,4);
/*Data for the table `ng_sys_user_role` */
insert  into `ng_sys_user_role`(`userId`,`roleId`) values (4,1),(5,1),(1,2),(2,3),(3,3);
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;