CREATE DATABASE  IF NOT EXISTS `processing` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `processing`;
-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: drsharvard.c5ewwyfxyijg.us-east-1.rds.amazonaws.com    Database: processing
-- ------------------------------------------------------
-- Server version	5.6.37-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `process_hosts`
--

DROP TABLE IF EXISTS `process_hosts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_hosts` (
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `idprocess_host` int(11) NOT NULL AUTO_INCREMENT,
  `process_host` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idprocess_host`),
  KEY `text` (`process_host`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process_projects`
--

DROP TABLE IF EXISTS `process_projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_projects` (
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `idprocess_project` int(11) NOT NULL AUTO_INCREMENT,
  `process_project` varchar(255) NOT NULL COMMENT 'Freeform text field',
  PRIMARY KEY (`idprocess_project`),
  KEY `index2` (`process_project`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process_states`
--

DROP TABLE IF EXISTS `process_states`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_states` (
  `idprocess_state` int(11) NOT NULL AUTO_INCREMENT,
  `state_name` varchar(45) NOT NULL,
  `state_desc` varchar(255) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`idprocess_state`),
  UNIQUE KEY `idprocess_state_UNIQUE` (`idprocess_state`),
  UNIQUE KEY `state_name_UNIQUE` (`state_name`),
  UNIQUE KEY `state_desc_UNIQUE` (`state_desc`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `process_view`
--

DROP TABLE IF EXISTS `process_view`;
/*!50001 DROP VIEW IF EXISTS `process_view`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `process_view` AS SELECT 
 1 AS `process_project`,
 1 AS `process_work`,
 1 AS `image_count`,
 1 AS `first_obs_date`,
 1 AS `last_obs_date`,
 1 AS `process_state`,
 1 AS `create_time`,
 1 AS `update_time`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `process_work_states`
--

DROP TABLE IF EXISTS `process_work_states`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_work_states` (
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `process_project_id` int(11) NOT NULL,
  `process_host_id` int(11) NOT NULL,
  `process_work_id` int(11) NOT NULL,
  `process_state_id` int(11) NOT NULL,
  `first_obs_date` datetime NOT NULL,
  `last_obs_date` datetime NOT NULL,
  `obs_count` int(11) DEFAULT '1',
  `process_create_date` datetime NOT NULL,
  `image_count` int(11) DEFAULT '0',
  PRIMARY KEY (`process_work_id`,`process_state_id`),
  KEY `process_work_state__fk__process_work_idx` (`process_work_id`),
  KEY `process_work_state__fk__process_state_idx` (`process_state_id`),
  KEY `process_work_state__fk__process_host_idx` (`process_host_id`),
  KEY `process_work_state__fk__process_project_idx` (`process_project_id`),
  KEY `process_work_state__first_obs_asc_idx` (`first_obs_date`),
  KEY `process_work_state__last_obs_asc_idx` (`last_obs_date`),
  CONSTRAINT `process_work_state__fk__process_host` FOREIGN KEY (`process_host_id`) REFERENCES `process_hosts` (`idprocess_host`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `process_work_state__fk__process_project` FOREIGN KEY (`process_project_id`) REFERENCES `process_projects` (`idprocess_project`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `process_work_state__fk__process_state` FOREIGN KEY (`process_state_id`) REFERENCES `process_states` (`idprocess_state`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `process_work_state__fk__process_work` FOREIGN KEY (`process_work_id`) REFERENCES `process_works` (`idprocess_work`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process_works`
--

DROP TABLE IF EXISTS `process_works`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_works` (
  `idprocess_work` int(11) NOT NULL AUTO_INCREMENT,
  `process_work` varchar(255) CHARACTER SET utf8 NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`idprocess_work`),
  UNIQUE KEY `process_works_process_work_uindex` (`process_work`)
) ENGINE=InnoDB AUTO_INCREMENT=3802 DEFAULT CHARSET=latin1 COMMENT='Replica of works table to track processing';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `publish_states`
--

DROP TABLE IF EXISTS `publish_states`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `publish_states` (
  `idpublish_state` int(11) NOT NULL AUTO_INCREMENT,
  `state_name` varchar(45) NOT NULL,
  `state_desc` varchar(255) NOT NULL,
  PRIMARY KEY (`idpublish_state`),
  UNIQUE KEY `state_name_UNIQUE` (`state_name`),
  UNIQUE KEY `state_desc_UNIQUE` (`state_desc`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'processing'
--

--
-- Dumping routines for database 'processing'
--
/*!50003 DROP PROCEDURE IF EXISTS `AddWorkProcessState` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`bdrc`@`%` PROCEDURE `AddWorkProcessState`( 
    IN p_obsdate varchar(50),
	IN hostname varchar (255), 
    IN srcdir varchar (255), 
    IN workName varchar (255),
    IN p_workCreateDate varchar(50),
    IN process_state varchar (255),
    IN imagecount int)
    COMMENT 'Arguments Must follow header ordering of corresponding csv:   obsdate, hostname, srcdir, workdir, workCreateDate, state, imagecount.yyyy-mm-dd or mm-dd-yyyy accepted for dates.'
BEGIN
/*
* Processing: Add a record for each distinct Work X Process State tuple. Update last_obs_date where a work X Process State tuple is found.
*/
-- expect different, non-standard dates
set @obsdate = Str_to_date(p_obsdate,'%m-%d-%Y');
if isnull(@obsdate) then set @obsdate = str_to_date(p_obsdate,'%Y-%m-%d');
end if;

set @workCreatedate = Str_to_date(p_workCreateDate,'%m-%d-%Y');
if isnull(@workCreatedate) then set @workCreatedate = str_to_date(p_workCreateDate,'%Y-%m-%d');
end if;

set @hostId = (select idprocess_host  from process_hosts ph where ph.process_host = hostname);
if @hostId is NULL THEN
  INSERT INTO `process_hosts` (`process_host`) VALUES (hostname);
  set @hostId = (select idprocess_host  from process_hosts ph where ph.process_host = hostname);
END IF;



set @srcDirId = (select idprocess_project from process_projects pps where pps.process_project = srcdir);
if @srcDirId is NULL THEN

  INSERT INTO `process_projects`(`process_project`)VALUES (srcdir);
  set @srcDirId = (select idprocess_project from process_projects pps where pps.process_project = srcdir);
END IF ;


-- Do we have a work
set @workId = ( select idprocess_work  from process_works w where w.process_work = workName );

if @workId IS NULL THEN
    INSERT INTO `process_works`(`process_work`)VALUES (workName);
    set @workId = ( select idprocess_work  from process_works w where w.process_work = workName );
END IF;

-- Must exist. We dont create on the fly
SELECT 
    idprocess_state
INTO @processStateId FROM
    process_states
WHERE
    UPPER(state_name) = UPPER(process_state);

-- First revision stepped all over existing obs dates,
/*
* if only one obs_date for the work_state pair, insert
* Get the max and min obs_dates observed for the work
* if none, insert
* if 
*/




if (select count(1)  from process_work_states where process_state_id = @processStateId and process_work_id = @workId)  = 0  then 
-- debug select  'new' ,  @workCreateDate,  p_workCreateDate, @processStateId, @workId, count(1) from process_work_states where process_state_id = @processStateId and process_work_id = @workId;
INSERT INTO `process_work_states`(
	`process_host_id`,
	`process_project_id`,
	`process_state_id`,
	`process_work_id`,
	`process_create_date`,
	`image_count`,
	`first_obs_date`,
	`last_obs_date`)
VALUES(
	@hostId,
	@srcDirId,
	@processStateId,
	@workId,
	@workCreatedate,
	imagecount,
	@obsdate,
	@obsdate);
else
-- debug select  * from process_work_states where process_state_id = @processStateId and process_work_id = @workId;
update  `process_work_states`set 
`image_count` =  imagecount,
`last_obs_date` = @obsDate
where process_state_id = @processStateId and process_work_id = @workId ;
END IF;


END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `ETLStatusFromCSV` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`bdrc`@`%` PROCEDURE `ETLStatusFromCSV`()
BEGIN

	declare v_done Integer default 0;
				  

	declare v_host, v_srcdir, v_workdir, v_state varchar(255);
	declare v_createDate, v_obsDate DATE ;
	declare v_imagecount INT ;

	declare wcur cursor for select hostname, srcdir, workdir, `date`, state, imagecount, obsdate	from `incoming_readytoprocess_image_data`;

	-- declare NOT FOUND handler
	 DECLARE CONTINUE HANDLER 
			FOR NOT FOUND SET v_done = 1;
			

	open wcur;


	etlOne: loop
		fetch wcur into v_host, v_srcdir, v_workdir, v_createDate,v_state, v_imagecount, v_obsDate;

		-- boogey on out
		if v_done <> 0 then
			leave etlOne;
		end if;

		-- otherwise do real work
	call AddWorkProcessState(v_host, v_srcdir, v_workdir, v_createDate,v_state, v_imagecount, v_obsDate);
	 -- select v_host, v_srcdir, v_workdir, v_createDate,v_state, v_imagecount, v_obsDate;
	end loop etlOne;


END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `InventoryReport` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`bdrc`@`%` PROCEDURE `InventoryReport`()
BEGIN
  select ph.process_host, pp.process_project, pw.process_work,ps.state_desc,pws.first_obs_date, pws.last_obs_date,pws.image_count from process_work_states pws
    join process_works pw on pws.process_work_id = pw.idprocess_work
    join process_states ps on pws.process_state_id = ps.idprocess_state
    join process_hosts ph on pws.process_host_id = ph.idprocess_host
    join process_projects pp on pws.process_project_id = pp.idprocess_project;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `new_procedure` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`bdrc`@`%` PROCEDURE `new_procedure`()
BEGIN
  select ph.process_host, pp.process_project, pw.process_work,ps.state_desc,pws.first_obs_date, pws.last_obs_date,pws.image_count from process_work_states pws
    join process_works pw on pws.process_work_id = pw.idprocess_work
    join process_states ps on pws.process_state_id = ps.idprocess_state
    join process_hosts ph on pws.process_host_id = ph.idprocess_host
    join process_projects pp on pws.process_project_id = pp.idprocess_project;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `process_view`
--

/*!50001 DROP VIEW IF EXISTS `process_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`bdrc`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `process_view` AS select `pp`.`process_project` AS `process_project`,`pw`.`process_work` AS `process_work`,`pws`.`image_count` AS `image_count`,`pws`.`first_obs_date` AS `first_obs_date`,`pws`.`last_obs_date` AS `last_obs_date`,`ps`.`state_desc` AS `process_state`,`pws`.`create_time` AS `create_time`,`pws`.`update_time` AS `update_time` from (((`process_work_states` `pws` join `process_works` `pw` on((`pws`.`process_work_id` = `pw`.`idprocess_work`))) join `process_projects` `pp` on((`pws`.`process_project_id` = `pp`.`idprocess_project`))) join `process_states` `ps` on((`pws`.`process_state_id` = `ps`.`idprocess_state`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-08-15 15:11:27
