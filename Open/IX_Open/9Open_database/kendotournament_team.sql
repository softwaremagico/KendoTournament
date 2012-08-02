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
INSERT INTO `team` VALUES ('1','29186284C',0,0,'Prueba',0),('1','51446248D',0,2,'Prueba',0),('1','51446248D',1,0,'Prueba',0),('1','43228517V',1,2,'Prueba',0),('1','43228517V',2,0,'Prueba',0),('1','29186284C',2,2,'Prueba',0),('2','48581921X',0,0,'Prueba',0),('2','53059685L',1,0,'Prueba',0),('2','20452372J',2,0,'Prueba',0),('3','TK1005724',0,0,'Prueba',0),('3','53094829L',1,0,'Prueba',0),('3','32625328N',2,0,'Prueba',0),('4','20444223Y',0,0,'Prueba',0),('4','09048414F',1,0,'Prueba',0),('4','04618780N',2,0,'Prueba',0),('5','22595373N',0,0,'Prueba',0),('5','33570644M',1,0,'Prueba',0),('5','48450739C',2,0,'Prueba',0),('6','53432384A',0,0,'Prueba',0),('6','33565664C',1,0,'Prueba',0),('6','33450464T',2,0,'Prueba',0),('7','44514429E',0,0,'Prueba',0),('7','33562183P',1,0,'Prueba',0),('7','22630165S',2,0,'Prueba',0),('8','22586203L',0,0,'Prueba',0),('8','20432315N',1,0,'Prueba',0),('8','',2,0,'Prueba',0),('Euskadi','79117526X',0,0,'IX Open de Kendo',0),('Euskadi','16076871D',1,0,'IX Open de Kendo',0),('Euskadi','78953359V',2,0,'IX Open de Kendo',0),('Kenwakai 2','51075692Y',0,0,'IX Open de Kendo',0),('Kenwakai 2','51944276H',1,0,'IX Open de Kendo',0),('Kenwakai 2','X42263197D',2,0,'IX Open de Kendo',0),('Kenwakai 3','47288888Z',0,0,'IX Open de Kendo',0),('Kenwakai 3','X0710754P',1,0,'IX Open de Kendo',0),('Kenwakai 3','2651422',2,0,'IX Open de Kendo',0),('KWK 1','53624856B',0,0,'IX Open de Kendo (Femenino)',0),('KWK 1','50749747V',1,0,'IX Open de Kendo (Femenino)',0),('KWK 1','415537L',2,0,'IX Open de Kendo (Femenino)',0),('Makoto Madrid 1','51131471X',0,0,'IX Open de Kendo',0),('Makoto Madrid 1','70056364C',1,0,'IX Open de Kendo',0),('Makoto Madrid 1','50992682A',2,0,'IX Open de Kendo',0),('Makoto Madrid 2','47220539K',0,0,'IX Open de Kendo',0),('Makoto Madrid 2','52885182V',1,0,'IX Open de Kendo',0),('Makoto Madrid 2','52880769C',2,0,'IX Open de Kendo',0),('Makoto Shinaki VLC 2','33473885F',0,0,'IX Open de Kendo',0),('Makoto Shinaki VLC 2','53059050M',1,0,'IX Open de Kendo',0),('Makoto Shinaki VLC 2','44881753J',2,0,'IX Open de Kendo',0),('Makoto Shinkai Valencia 1','18050438S',0,0,'IX Open de Kendo (Femenino)',0),('Makoto Shinkai Valencia 1','53255923K',1,0,'IX Open de Kendo (Femenino)',0),('Makoto Shinkai Valencia 1','53059685L',2,0,'IX Open de Kendo (Femenino)',0),('Meigetsu Eliana','25395783B',0,0,'IX Open de Kendo',0),('Meigetsu Eliana','33450895V',1,0,'IX Open de Kendo',0),('Meigetsu Eliana','53094829L',2,0,'IX Open de Kendo',0),('Renshinkan BCN','Y1944431W',0,0,'IX Open de Kendo (Femenino)',0),('Renshinkan BCN','47729850C',1,0,'IX Open de Kendo (Femenino)',0),('Renshinkan BCN','52400840D',2,0,'IX Open de Kendo (Femenino)',0),('Shindokai Vitoria','72498438T',0,0,'IX Open de Kendo',0),('Shindokai Vitoria','44870236L',1,0,'IX Open de Kendo',0),('Shindokai Vitoria','72719928T',2,0,'IX Open de Kendo',0),('Universidad de Navarra','40335956M',0,0,'IX Open de Kendo',0),('Universidad de Navarra','72820515P',1,0,'IX Open de Kendo',0),('Universidad de Navarra','47949235P',2,0,'IX Open de Kendo',0),('Universidad de Valencia','53073081Y',0,0,'IX Open de Kendo (Femenino)',0),('Universidad de Valencia','53629653R',1,0,'IX Open de Kendo (Femenino)',0),('Universidad de Valencia','25414641D',2,0,'IX Open de Kendo (Femenino)',0),('Universidad de Valencia 3','53225181F',0,0,'IX Open de Kendo',0),('Universidad de Valencia 3','20438523X',1,0,'IX Open de Kendo',0),('Universidad de Valencia 3','48378412M',2,0,'IX Open de Kendo',0),('Universidad de Valencia 4','09048414F',0,0,'IX Open de Kendo',0),('Universidad de Valencia 4','48693477Q',1,0,'IX Open de Kendo',0),('Universidad de Valencia 4','53055859B',2,0,'IX Open de Kendo',0),('Universidad de Valencia 5','20432315N',0,0,'IX Open de Kendo',0),('Universidad de Valencia 5','53258546L',1,0,'IX Open de Kendo',0),('Universidad de Valencia 5','44866987J',2,0,'IX Open de Kendo',0),('Universidad de Valencia 6','48538627W',0,0,'IX Open de Kendo',0),('Universidad de Valencia 6','53750170K',1,0,'IX Open de Kendo',0),('Universidad de Valencia 6','53254181G',2,0,'IX Open de Kendo',0),('Universidad de Waseda','4152344556J',0,0,'IX Open de Kendo (Femenino)',0),('Universidad de Waseda','5454171K',1,0,'IX Open de Kendo (Femenino)',0),('Universidad de Waseda','5454453454U',2,0,'IX Open de Kendo (Femenino)',0),('UV / Shindokai','48583506P',0,0,'IX Open de Kendo (Femenino)',0),('UV / Shindokai','533555877N',1,0,'IX Open de Kendo (Femenino)',0),('UV / Shindokai','09343594M',2,0,'IX Open de Kendo (Femenino)',0),('UW / UV','44514429E',0,0,'IX Open de Kendo',0),('UW / UV','53626877P',1,0,'IX Open de Kendo',0),('UW / UV','TK5930427',2,0,'IX Open de Kendo',0),('Yoshinkai','44731254A',0,0,'IX Open de Kendo',0),('Yoshinkai','20844381X',1,0,'IX Open de Kendo',0),('Yoshinkai','29204876M',2,0,'IX Open de Kendo',0),('Zanshin 1','98767698H',0,0,'IX Open de Kendo',0),('Zanshin 1','02649643C',1,0,'IX Open de Kendo',0),('Zanshin 1','50975145S',2,0,'IX Open de Kendo',0),('Zanshin 2','05387290T',0,0,'IX Open de Kendo',0),('Zanshin 2','12345679J',1,0,'IX Open de Kendo',0),('Zanshin 2','51456837H',2,0,'IX Open de Kendo',0),('Zanshin/Yoshinkai','29189061Z',0,0,'IX Open de Kendo (Femenino)',0),('Zanshin/Yoshinkai','50855956N',1,0,'IX Open de Kendo (Femenino)',0),('Zanshin/Yoshinkai','11857517M',2,0,'IX Open de Kendo (Femenino)',0);
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
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
