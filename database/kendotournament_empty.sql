CREATE DATABASE  IF NOT EXISTS `kendotournament` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `kendotournament`;
-- MySQL dump 10.13  Distrib 5.5.31, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: kendotournament
-- ------------------------------------------------------
-- Server version	5.5.31-0ubuntu0.13.04.1

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
  `Team1` varchar(50) NOT NULL,
  `Team2` varchar(50) NOT NULL,
  `Tournament` varchar(50) NOT NULL,
  `GroupIndex` int(10) unsigned NOT NULL,
  `TournamentGroup` int(10) unsigned NOT NULL,
  `TournamentLevel` int(10) unsigned NOT NULL,
  `MemberOrder` int(10) unsigned NOT NULL,
  `PointPlayer1A` char(1) NOT NULL,
  `PointPlayer1B` char(1) NOT NULL,
  `PointPlayer2A` char(1) NOT NULL,
  `PointPlayer2B` char(1) NOT NULL,
  `FaultsPlayer1` int(1) unsigned NOT NULL,
  `FaultsPlayer2` int(1) unsigned NOT NULL,
  PRIMARY KEY (`Team1`,`Team2`,`Tournament`,`GroupIndex`,`TournamentLevel`,`MemberOrder`, `TournamentGroup`),
  KEY `fk_duel_1` (`Team1`,`Team2`,`Tournament`,`GroupIndex`,`TournamentLevel`),
  KEY `fight_FK` (`Team1`,`Team2`,`Tournament`,`GroupIndex`,`TournamentLevel`),
  CONSTRAINT `fight_FK` FOREIGN KEY (`Team1`, `Team2`, `Tournament`, `GroupIndex`, `TournamentLevel`, `TournamentGroup`) REFERENCES `fight` (`Team1`, `Team2`, `Tournament`, `GroupIndex`, `TournamentLevel`, `TournamentGroup`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  PRIMARY KEY (`ID`),
  KEY `ClubBelong` (`Club`),
  CONSTRAINT `ClubBelong` FOREIGN KEY (`Club`) REFERENCES `club` (`Name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='competitor in the tournament. Each line for each one. ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `customlinks`
--

DROP TABLE IF EXISTS `customlinks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customlinks` (
  `Tournament` char(50) NOT NULL,
  `SourceGroup` int(11) NOT NULL,
  `AddressGroup` int(11) NOT NULL,
  `WinnerOrder` int(11) NOT NULL,
  PRIMARY KEY (`Tournament`,`SourceGroup`,`AddressGroup`,`WinnerOrder`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Stores user defined links for custom championships.';
/*!40101 SET character_set_client = @saved_cs_client */;

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
-- Table structure for table `fight`
--

DROP TABLE IF EXISTS `fight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fight` (
  `Team1` varchar(50) NOT NULL COMMENT 'One team on the fight',
  `Team2` varchar(50) NOT NULL COMMENT 'Other team on the fight',
  `Tournament` varchar(50) NOT NULL,
  `GroupIndex` int(10) unsigned NOT NULL,
  `TournamentGroup` int(10) unsigned NOT NULL,
  `TournamentLevel` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Is a fight of group or tree of the league',
  `FightArea` int(10) unsigned NOT NULL DEFAULT '1',
  `Winner` int(2) unsigned NOT NULL DEFAULT '3' COMMENT '-1-> Winner left team, 1-> Winner right team, 0-> Draw Game, 3-> Not finished',
  PRIMARY KEY (`Team1`,`Team2`,`Tournament`,`GroupIndex`,`TournamentLevel`, `TournamentGroup`),
  KEY `TournamentFightIndex` (`Tournament`),
  KEY `TCL1FightIndex` (`Team1`,`Level`,`Tournament`),
  KEY `Team2Fight` (`Team2`,`Level`,`Tournament`),
  CONSTRAINT `TournamentFight` FOREIGN KEY (`Tournament`) REFERENCES `tournament` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `ImpressCardOrder` int(10) DEFAULT '0',
  `ImpressCardPrinted` tinyint(1) DEFAULT '0',
  `DiplomaPrinted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`Competitor`,`Tournament`),
  KEY `TournamentRole` (`Tournament`),
  KEY `CompetitorRole` (`Competitor`),
  CONSTRAINT `CompetitorRoleC` FOREIGN KEY (`Competitor`) REFERENCES `competitor` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `TournamentRoleC` FOREIGN KEY (`Tournament`) REFERENCES `tournament` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

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
-- Table structure for table `undraw`
--

DROP TABLE IF EXISTS `undraw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `undraw` (
  `Tournament` char(50) NOT NULL,
  `Points` int(11) NOT NULL DEFAULT '1',
  `TournamentLevel` int(11) NOT NULL,
  `Team` varchar(50) NOT NULL,
  `Player` int(11) NOT NULL,
  `TournamentGroup` int(11) NOT NULL,
  PRIMARY KEY (`Tournament`,`TournamentLevel`,`Team`,`TournamentGroup`) USING BTREE,
  KEY `Team` (`Team`),
  CONSTRAINT `TeamDraw` FOREIGN KEY (`Team`) REFERENCES `team` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `TournamentUndraw` FOREIGN KEY (`Tournament`) REFERENCES `tournament` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-06-15 19:32:56
