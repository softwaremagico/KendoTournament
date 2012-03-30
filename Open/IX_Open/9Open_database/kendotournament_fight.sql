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
-- Table structure for table `fight`
--

DROP TABLE IF EXISTS `fight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fight` (
  `Team1` varchar(50) NOT NULL COMMENT 'One team on the fight',
  `Team2` varchar(50) NOT NULL COMMENT 'Other team on the fight',
  `Tournament` char(50) NOT NULL,
  `FightArea` int(10) unsigned NOT NULL DEFAULT '1',
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Winner` int(11) NOT NULL DEFAULT '3' COMMENT '-1-> Winner left team, 1-> Winner right team, 0-> Draw Game, 3-> Not finished',
  `LeagueLevel` int(11) NOT NULL DEFAULT '0' COMMENT 'Is a fight of group or tree of the league',
  `MaxWinners` int(11) NOT NULL DEFAULT '1' COMMENT 'Number of winners of a group that pass to the next round.',
  PRIMARY KEY (`ID`),
  KEY `TournamentFightIndex` (`Tournament`),
  KEY `TCL1FightIndex` (`Team1`,`LeagueLevel`,`Tournament`),
  KEY `Team2Fight` (`Team2`,`LeagueLevel`,`Tournament`),
  CONSTRAINT `TournamentFight` FOREIGN KEY (`Tournament`) REFERENCES `tournament` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2054 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fight`
--

LOCK TABLES `fight` WRITE;
/*!40000 ALTER TABLE `fight` DISABLE KEYS */;
INSERT INTO `fight` VALUES ('1','2','Prueba',0,1987,-1,0,2),('3','4','Prueba',0,1988,1,0,2),('5','6','Prueba',0,1989,-1,0,2),('7','8','Prueba',0,1990,-1,0,2),('1','8','Prueba',0,1991,-1,1,1),('4','6','Prueba',0,1992,-1,1,1),('3','5','Prueba',0,1993,-1,1,1),('2','7','Prueba',0,1994,-1,1,1),('1','4','Prueba',0,2011,-1,2,1),('3','2','Prueba',0,2012,-1,2,1),('1','3','Prueba',0,2013,-1,3,1),('Universidad de Waseda','Makoto Shinkai Valencia 1','IX Open de Kendo (Femenino)',0,2014,-1,0,2),('Universidad de Valencia','Makoto Shinkai Valencia 1','IX Open de Kendo (Femenino)',0,2015,-1,0,2),('Universidad de Valencia','Universidad de Waseda','IX Open de Kendo (Femenino)',0,2016,1,0,2),('KWK 1','UV / Shindokai','IX Open de Kendo (Femenino)',0,2017,-1,0,2),('Renshinkan BCN','UV / Shindokai','IX Open de Kendo (Femenino)',0,2018,-1,0,2),('Renshinkan BCN','Zanshin/Yoshinkai','IX Open de Kendo (Femenino)',0,2019,-1,0,2),('KWK 1','Zanshin/Yoshinkai','IX Open de Kendo (Femenino)',0,2020,-1,0,2),('Kenwakai 2','Makoto Shinaki VLC 2','IX Open de Kendo',0,2021,-1,0,2),('Universidad de Valencia 5','Makoto Shinaki VLC 2','IX Open de Kendo',0,2022,0,0,2),('Universidad de Valencia 5','Makoto Madrid 1','IX Open de Kendo',0,2023,1,0,2),('Kenwakai 2','Makoto Madrid 1','IX Open de Kendo',0,2024,-1,0,2),('Kenwakai 3','UW / UV','IX Open de Kendo',0,2025,-1,0,2),('Universidad de Navarra','UW / UV','IX Open de Kendo',0,2026,1,0,2),('Universidad de Navarra','Yoshinkai','IX Open de Kendo',0,2027,1,0,2),('Kenwakai 3','Yoshinkai','IX Open de Kendo',0,2028,0,0,2),('Euskadi','Makoto Madrid 2','IX Open de Kendo',1,2029,-1,0,2),('Universidad de Valencia 3','Makoto Madrid 2','IX Open de Kendo',1,2030,-1,0,2),('Universidad de Valencia 3','Zanshin 2','IX Open de Kendo',1,2031,1,0,2),('Euskadi','Zanshin 2','IX Open de Kendo',1,2032,-1,0,2),('Zanshin 1','Shindokai Vitoria','IX Open de Kendo',1,2033,1,0,2),('Universidad de Valencia 4','Shindokai Vitoria','IX Open de Kendo',1,2034,0,0,2),('Universidad de Valencia 4','Meigetsu Eliana','IX Open de Kendo',1,2035,-1,0,2),('Universidad de Valencia 6','Meigetsu Eliana','IX Open de Kendo',1,2036,1,0,2),('Universidad de Valencia 6','Zanshin 1','IX Open de Kendo',1,2037,1,0,2),('Kenwakai 2','Universidad de Valencia 4','IX Open de Kendo',0,2044,-1,1,1),('Yoshinkai','Universidad de Valencia 3','IX Open de Kendo',0,2045,-1,1,1),('UW / UV','Euskadi','IX Open de Kendo',1,2046,-1,1,1),('Makoto Madrid 1','Zanshin 1','IX Open de Kendo',1,2047,0,1,1),('Kenwakai 2','Yoshinkai','IX Open de Kendo',0,2048,0,2,1),('UW / UV','Makoto Madrid 1','IX Open de Kendo',1,2049,1,2,1),('Yoshinkai','Makoto Madrid 1','IX Open de Kendo',0,2050,0,3,1),('Universidad de Waseda','Renshinkan BCN','IX Open de Kendo (Femenino)',0,2051,-1,1,1),('Universidad de Valencia','KWK 1','IX Open de Kendo (Femenino)',0,2052,1,1,1),('Universidad de Waseda','KWK 1','IX Open de Kendo (Femenino)',0,2053,-1,2,1);
/*!40000 ALTER TABLE `fight` ENABLE KEYS */;
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
