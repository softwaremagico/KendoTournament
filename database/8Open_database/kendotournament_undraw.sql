CREATE DATABASE  IF NOT EXISTS `kendotournament` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `kendotournament`;
-- MySQL dump 10.13  Distrib 5.1.40, for Win32 (ia32)
--
-- Host: localhost    Database: kendotournament
-- ------------------------------------------------------
-- Server version	5.1.30-community

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
-- Table structure for table `undraw`
--

DROP TABLE IF EXISTS `undraw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `undraw` (
  `Championship` char(50) NOT NULL,
  `LeagueLevel` int(11) NOT NULL,
  `Team` varchar(50) NOT NULL,
  `Player` int(11) NOT NULL,
  `Winner` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Championship`,`LeagueLevel`,`Team`) USING BTREE,
  KEY `Team` (`Team`),
  CONSTRAINT `Team` FOREIGN KEY (`Team`) REFERENCES `team` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Tourna` FOREIGN KEY (`Championship`) REFERENCES `tournament` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `undraw`
--

LOCK TABLES `undraw` WRITE;
/*!40000 ALTER TABLE `undraw` DISABLE KEYS */;
INSERT INTO `undraw` VALUES ('LigaIntDic2010',1,'Latorre Contreras, Fredi',0,0),('LigaIntDic2010',1,'Sancho Aguilera, Argimiro',0,1),('Trofeo Rector 2010 (Masculino)',0,'Giménez Costa, Moisés',0,0),('Trofeo Rector 2010 (Masculino)',0,'Hortelano, Jorge',0,1),('VIII Open & Clinic (Masculino)',0,'Kenwakai 2 ',0,1),('VIII Open & Clinic (Masculino)',0,'Ryoshinkai Can-meibu ',0,0),('VIII Open & Clinic (Masculino)',1,'Kenwakai 1 ',0,1),('VIII Open & Clinic (Masculino)',1,'Uv 2 ',0,0);
/*!40000 ALTER TABLE `undraw` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-02-21 16:25:22
