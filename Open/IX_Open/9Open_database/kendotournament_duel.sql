CREATE DATABASE  IF NOT EXISTS `kendotournament` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `kendotournament`;
-- MySQL dump 10.13  Distrib 5.1.58, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: kendotournament
-- ------------------------------------------------------
-- Server version	5.1.58-1ubuntu1

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
-- Table structure for table `duel`
--

DROP TABLE IF EXISTS `duel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `duel` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Fight` int(10) unsigned NOT NULL COMMENT 'Foreign Key of Fight ID',
  `OrderPlayer` int(10) unsigned NOT NULL,
  `PointPlayer1A` char(1) NOT NULL,
  `PointPlayer1B` char(1) NOT NULL,
  `PointPlayer2A` char(1) NOT NULL,
  `PointPlayer2B` char(1) NOT NULL,
  `FaultsPlayer1` int(10) unsigned NOT NULL,
  `FaultsPlayer2` int(10) unsigned NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `duelIndex` (`ID`),
  KEY `Fight` (`Fight`),
  CONSTRAINT `Fight` FOREIGN KEY (`Fight`) REFERENCES `fight` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5355 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `duel`
--

LOCK TABLES `duel` WRITE;
/*!40000 ALTER TABLE `duel` DISABLE KEYS */;
INSERT INTO `duel` VALUES (5119,1987,0,'M','','','',0,0),(5120,1988,0,'','','M','',0,0),(5121,1989,0,'M','','','',0,0),(5122,1990,0,'M','','','',0,0),(5124,1990,2,'I','I','','',0,0),(5125,1991,1,'M','','','',0,0),(5127,1992,0,'M','','','',0,0),(5128,1993,0,'M','','','',0,0),(5129,1994,0,'M','','','',0,0),(5195,2011,0,'M','','','',0,0),(5196,2012,0,'M','','','',0,0),(5197,1991,2,'I','I','','',0,0),(5198,2013,2,'M','','','',0,0),(5200,2021,0,'M','','','',0,0),(5201,2029,0,'M','','M','',0,0),(5203,2029,1,'M','M','','',0,0),(5205,2021,1,'M','M','','',0,0),(5207,2029,2,'M','M','','',0,0),(5210,2021,2,'K','K','','',0,0),(5211,2030,0,'M','M','','',0,0),(5213,2030,1,'M','M','','',0,0),(5216,2030,2,'M','K','','',0,0),(5218,2031,2,'','','D','M',0,0),(5223,2023,0,'M','','M','',0,0),(5224,2023,1,'','','','',1,0),(5227,2032,1,'M','','','',0,0),(5229,2032,2,'M','M','','',0,0),(5230,2033,0,'','','M','',0,0),(5232,2033,1,'M','M','','',0,0),(5233,2033,2,'','','K','',0,0),(5234,2034,0,'','','M','',0,0),(5235,2034,1,'M','','','',0,0),(5237,2035,0,'M','','','',1,0),(5239,2035,1,'','','M','',1,0),(5242,2035,2,'M','M','','',0,0),(5244,2036,0,'','','M','M',0,0),(5247,2036,1,'M','M','M','',0,0),(5250,2037,0,'','','K','M',0,0),(5253,2037,1,'','','M','M',1,0),(5255,2037,2,'','','K','K',0,0),(5256,2023,2,'','','M','',0,0),(5258,2024,0,'M','D','','',0,0),(5260,2024,1,'M','K','','',0,0),(5262,2024,2,'K','K','','',0,0),(5264,2025,0,'M','D','','',0,0),(5266,2025,1,'M','M','','',0,0),(5267,2025,2,'','','M','',0,0),(5268,2026,1,'','','M','',0,0),(5270,2026,2,'','','M','M',0,0),(5272,2027,0,'','','M','M',0,0),(5274,2027,1,'','','M','M',0,0),(5276,2027,2,'','','M','M',0,0),(5278,2014,0,'M','M','','',0,0),(5280,2014,1,'K','D','','',0,0),(5282,2014,2,'M','M','','',0,0),(5283,2015,1,'K','','','',0,0),(5284,2015,2,'D','','','',0,0),(5286,2016,0,'','','M','M',0,0),(5288,2016,1,'','','D','M',0,0),(5290,2016,2,'','','D','M',0,0),(5292,2017,0,'M','M','','',0,0),(5294,2017,1,'M','M','','',0,0),(5296,2017,2,'M','M','','',0,0),(5302,2018,2,'M','M','','',0,0),(5311,2020,0,'','','K','',0,0),(5314,2020,1,'K','K','M','',0,0),(5315,2020,2,'K','','','',0,0),(5317,2044,0,'M','','','',0,0),(5318,2046,0,'M','','','',0,0),(5319,2047,0,'M','','','',0,0),(5320,2045,0,'M','','','',0,0),(5321,2048,0,'','','M','',0,0),(5322,2049,0,'','','M','',0,0),(5323,2050,0,'M','','','',0,0),(5330,2019,0,'K','K','','',0,0),(5333,2018,1,'M','M','','',0,0),(5334,2018,0,'M','M','','',0,0),(5335,2019,1,'M','','M','',0,0),(5339,2019,2,'','','M','',1,0),(5340,2051,0,'','','K','',0,0),(5342,2051,1,'M','K','','',0,0),(5343,2051,2,'M','','','',0,0),(5345,2052,0,'','','M','M',0,0),(5347,2052,1,'','','K','M',0,0),(5348,2052,2,'M','','','',0,0),(5350,2053,0,'M','M','','',0,0),(5352,2053,1,'K','K','','',0,0),(5354,2053,2,'M','M','','',0,0);
/*!40000 ALTER TABLE `duel` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-03-25 19:03:38
