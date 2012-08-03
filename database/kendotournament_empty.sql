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
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `duel`
--

LOCK TABLES `duel` WRITE;
/*!40000 ALTER TABLE `duel` DISABLE KEYS */;
/*!40000 ALTER TABLE `duel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `competitor`
--

DROP TABLE IF EXISTS `competitor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `competitor` (
  `ID` varchar(12) NOT NULL DEFAULT '0000000Z' COMMENT 'DNI or Passport',
  `Name` char(30) NOT NULL,
  `Surname` char(50) NOT NULL,
  `Club` char(25) DEFAULT NULL COMMENT 'Club belonging to',
  `Photo` mediumblob COMMENT 'Photo image',
  `PhotoSize` double NOT NULL DEFAULT '0' COMMENT 'Size of the photo',
  `ListOrder` int(6) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Order_UNIQUE` (`ListOrder`),
  KEY `ClubBelong` (`Club`),
  KEY `OrderIndex` (`ListOrder`),
  CONSTRAINT `ClubBelong` FOREIGN KEY (`Club`) REFERENCES `club` (`Name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='competitor in the tournament. Each line for each one. ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `competitor`
--

LOCK TABLES `competitor` WRITE;
/*!40000 ALTER TABLE `competitor` DISABLE KEYS */;
/*!40000 ALTER TABLE `competitor` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40000 ALTER TABLE `club` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tournament`
--

DROP TABLE IF EXISTS `tournament`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tournament` (
  `Name` char(50) NOT NULL,
  `Banner` mediumblob COMMENT 'Banner to be shown on the event.',
  `Size` double NOT NULL DEFAULT '0' COMMENT 'Size of the image file',
  `FightingAreas` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'Number of places where fights can be celebrated concurrently.',
  `PassingTeams` int(10) unsigned NOT NULL DEFAULT '1',
  `TeamSize` int(10) unsigned NOT NULL DEFAULT '3' COMMENT 'Max Competitors in each team',
  `Type` char(20) NOT NULL DEFAULT 'simple' COMMENT 'Kind of designed Tournament',
  `ScoreWin` int(11) NOT NULL DEFAULT '1',
  `ScoreDraw` int(11) NOT NULL DEFAULT '0',
  `ScoreType` char(15) NOT NULL DEFAULT 'Classic',
  `Diploma` mediumblob,
  `Accreditation` mediumblob,
  `DiplomaSize` double NOT NULL DEFAULT '0',
  `AccreditationSize` double NOT NULL,
  PRIMARY KEY (`Name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tournament`
--

LOCK TABLES `tournament` WRITE;
/*!40000 ALTER TABLE `tournament` DISABLE KEYS */;
/*!40000 ALTER TABLE `tournament` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fight`
--

LOCK TABLES `fight` WRITE;
/*!40000 ALTER TABLE `fight` DISABLE KEYS */;
/*!40000 ALTER TABLE `fight` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `Tournament` char(50) NOT NULL,
  `Competitor` varchar(12) NOT NULL DEFAULT '0000000Z',
  `Role` varchar(15) NOT NULL,
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ImpressCard` tinyint(1) DEFAULT '0',
  `Diploma` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `TournamentRole` (`Tournament`),
  KEY `CompetitorRole` (`Competitor`),
  CONSTRAINT `CompetitorRoleC` FOREIGN KEY (`Competitor`) REFERENCES `competitor` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `TournamentRoleC` FOREIGN KEY (`Tournament`) REFERENCES `tournament` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team` (
  `Name` varchar(50) NOT NULL COMMENT 'A name identificator for the team',
  `Member` varchar(12) DEFAULT NULL,
  `Position` int(11) unsigned NOT NULL COMMENT 'Order of the competitor to fight',
  `LevelTournament` int(11) NOT NULL DEFAULT '0' COMMENT 'The member order is only valid in this level.',
  `Tournament` char(50) NOT NULL,
  `LeagueGroup` int(11) NOT NULL DEFAULT '-1' COMMENT 'In a league a team is included in a group.',
  PRIMARY KEY (`Name`,`Position`,`LevelTournament`,`Tournament`) USING BTREE,
  KEY `CompetitorTeamIndex` (`Member`),
  KEY `TournamentTeamIndex` (`Tournament`),
  KEY `LevelTeamIndex` (`LevelTournament`),
  KEY `TCLTeamIndex` (`Name`,`LevelTournament`,`Tournament`),
  CONSTRAINT `Tournament` FOREIGN KEY (`Tournament`) REFERENCES `tournament` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='One team composed by different competitors.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team`
--

LOCK TABLES `team` WRITE;
/*!40000 ALTER TABLE `team` DISABLE KEYS */;
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `undraw`
--

DROP TABLE IF EXISTS `undraw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `undraw` (
  `Championship` char(50) NOT NULL,
  `UndrawGroup` int(11) NOT NULL,
  `LevelUndraw` int(11) NOT NULL,
  `Team` varchar(50) NOT NULL,
  `Player` int(11) NOT NULL,
  PRIMARY KEY (`Championship`,`UndrawGroup`,`LevelUndraw`,`Team`) USING BTREE,
  KEY `Team` (`Team`),
  CONSTRAINT `TeamDraw` FOREIGN KEY (`Team`) REFERENCES `team` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `TournamentUndraw` FOREIGN KEY (`Championship`) REFERENCES `tournament` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `undraw`
--

LOCK TABLES `undraw` WRITE;
/*!40000 ALTER TABLE `undraw` DISABLE KEYS */;
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

-- Dump completed on 2012-02-20 12:01:51
