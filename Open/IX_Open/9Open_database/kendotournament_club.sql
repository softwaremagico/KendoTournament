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
-- Table structure for table `club`
--

DROP TABLE IF EXISTS `club`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `club` (
  `Name` char(50) NOT NULL COMMENT 'The name identificator of the club',
  `Country` char(20) NOT NULL,
  `Representative` varchar(12) DEFAULT NULL COMMENT 'Representative of the club',
  `Mail` char(50) DEFAULT NULL COMMENT 'Email of the representative',
  `Phone` bigint(20) DEFAULT NULL COMMENT 'Phone number of the representative of the club',
  `City` char(20) DEFAULT NULL,
  `Web` char(100) DEFAULT NULL,
  `Address` char(100) DEFAULT NULL,
  PRIMARY KEY (`Name`),
  KEY `Representative` (`Representative`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='A club where the competitors belong to';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `club`
--

LOCK TABLES `club` WRITE;
/*!40000 ALTER TABLE `club` DISABLE KEYS */;
INSERT INTO `club` VALUES ('Bushinkai','España','','',NULL,'Valencia','',''),('Club Shisuikan Herca','España','29193556R','shisuikan@gmail.com',659731036,'Valencia','',''),('Club Yoshinkai','España','29189061-Z','teresavalsan@gmail.com',686820351,'Valencia','',''),('Euskadi Kendo','España','','',NULL,'Bilbao','',''),('Kensei Dojo','España','','',NULL,'-','',''),('Kenseikai Navarra','España','','',NULL,'Pamplona','',''),('Kenwakai','España','51944276H','rubenpl77@hotmail.com',691672014,'Madrid','',''),('Kobukan','España','','',NULL,'Sevilla','',''),('Kyohan','España','','',NULL,'Sevilla','http://www.kyohan.es/joomla/index.php?option=com_contact&Itemid=3',''),('Makoto Shin Kai','España','24365514G','makoto.kendovalencia@gmail.com',677681161,'Valencia','',''),('Makoto Shinkai','España','','',NULL,'Valencia','',''),('Makoto Shinkai Madrid','España','','',NULL,'Madrid','',''),('Meibukan','','XD296227','arseniacorcoba@yahoo.es',32476247157,'Bruselas','',''),('Meigetsu La Eliana','España','19841565-V','kendo_laeliana@yahoo.es',657583020,'La Eliana','',''),('Mix Barcelona','España','23276571L','juanjose_oliver@yahoo.es',650175423,'Barcelona','',''),('Palma de Mallorca','España','','',NULL,'','',''),('Renshinkan','España','','',NULL,'Barcelona','',''),('Ryoshinkai','España','','',NULL,'Barcelona','',''),('Ryoshinkai Canarias','España','34069100M','pedrolara348@hotmail.com',638560565,'Tenerife','',''),('Ryoshinkai Mallorca','España','18237278A','jmipba@gmail.com',653912731,'Palma De Mallorca','',''),('Shindokai Vitoria','España','','',NULL,'Vitoria','',''),('Universidad de Valencia','España','','',NULL,'Valencia','',''),('Universidad de Waseda','Japon','','',NULL,'Waseda','',''),('UPC','España','','vrodrigogarcia@gmail.com',646791634,'Barcelona','',''),('Zanshin','España','23284229H','consultas.zanshin@gmail.com',666469123,'Madrid','','');
/*!40000 ALTER TABLE `club` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-03-25 19:03:46
