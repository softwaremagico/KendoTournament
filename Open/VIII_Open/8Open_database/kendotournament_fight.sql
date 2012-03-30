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
  KEY `Team1` (`Team1`),
  KEY `Team2` (`Team2`),
  KEY `Tournam` (`Tournament`),
  KEY `tournament` (`Tournament`(10)),
  CONSTRAINT `Team1` FOREIGN KEY (`Team1`) REFERENCES `team` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Team2` FOREIGN KEY (`Team2`) REFERENCES `team` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Tournam` FOREIGN KEY (`Tournament`) REFERENCES `tournament` (`Name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1769 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fight`
--

LOCK TABLES `fight` WRITE;
/*!40000 ALTER TABLE `fight` DISABLE KEYS */;
INSERT INTO `fight` VALUES ('Waseda 1','Zanshin 1','VII Open i Clínic (Masculino)',0,486,-1,0,2),('UV4','Zanshin 1','VII Open i Clínic (Masculino)',0,487,1,0,2),('UPC','UV3','VII Open i Clínic (Masculino)',0,490,1,0,2),('Meigetsu 1','UV3','VII Open i Clínic (Masculino)',0,491,1,0,2),('Meigetsu 1','Kenwakai 2 / UCM','VII Open i Clínic (Masculino)',0,492,1,0,2),('UPC','Kenwakai 2 / UCM','VII Open i Clínic (Masculino)',0,493,1,0,2),('Kenwakai 1','Mixto','VII Open i Clínic (Masculino)',1,494,-1,0,2),('UV 5','Mixto','VII Open i Clínic (Masculino)',1,495,0,0,2),('UV 5','Ryushinkai M. / UIB','VII Open i Clínic (Masculino)',1,496,1,0,2),('Kenwakai 1','Ryushinkai M. / UIB','VII Open i Clínic (Masculino)',1,497,-1,0,2),('UV 7','Renshinkan 1','VII Open i Clínic (Masculino)',1,498,1,0,2),('UV 7','Waseda 2','VII Open i Clínic (Masculino)',1,501,1,0,2),('Kenwakai 4','Renshinkan','VII Open i Clínic (Femenino)',0,502,-1,0,1),('UV - UW','Renshinkan','VII Open i Clínic (Femenino)',0,503,1,0,1),('UV - UW','Kenwakai 5 / UCM','VII Open i Clínic (Femenino)',0,504,-1,0,1),('UV 2','Kenwakai 5 / UCM','VII Open i Clínic (Femenino)',0,505,1,0,1),('UV 2','Kenwakai 4','VII Open i Clínic (Femenino)',0,506,1,0,1),('UV - UW','Kenwakai 4','VII Open i Clínic (Femenino)',0,507,0,0,1),('UV - UW','UV 2','VII Open i Clínic (Femenino)',0,508,-1,0,1),('Renshinkan','UV 2','VII Open i Clínic (Femenino)',0,509,-1,0,1),('Renshinkan','Kenwakai 5 / UCM','VII Open i Clínic (Femenino)',0,510,0,0,1),('Kenwakai 4','Kenwakai 5 / UCM','VII Open i Clínic (Femenino)',0,511,2,0,1),('Waseda 1','Renshinkan 1','VII Open i Clínic (Masculino)',0,771,-1,1,1),('Kenwakai 2 / UCM','Ryushinkai M. / UIB','VII Open i Clínic (Masculino)',1,772,-1,1,1),('UV3','Kenwakai 1','VII Open i Clínic (Masculino)',0,773,1,1,1),('Waseda 1','Kenwakai 2 / UCM','VII Open i Clínic (Masculino)',0,775,-1,2,1),('Kenwakai 1','Waseda 2','VII Open i Clínic (Masculino)',1,776,1,2,1),('Waseda 1','Waseda 2','VII Open i Clínic (Masculino)',0,777,2,3,1),('Civera  Durban, Ana','Fernández Barjau, Mar','Trofeo Rector 2010 (Femenino)',0,931,1,0,1),('Gómez Navas, Francis','Fernández Barjau, Mar','Trofeo Rector 2010 (Femenino)',0,932,1,0,1),('Gómez Navas, Francis','Civera  Durban, Ana','Trofeo Rector 2010 (Femenino)',0,937,1,0,1),('Garcia Martinez, Xavi','Sancho Aguilera, Argimiro','Trofeo Rector 2010 (Masculino)',0,986,-1,0,2),('Gomez Verdejo, Fernando','Sancho Aguilera, Argimiro','Trofeo Rector 2010 (Masculino)',0,987,1,0,2),('Gomez Verdejo, Fernando','Garcia Martinez, Xavi','Trofeo Rector 2010 (Masculino)',0,988,1,0,2),('Quintanilla Serrano, Pedro','Hortelano, Jorge','Trofeo Rector 2010 (Masculino)',0,990,1,0,2),('Fernández Valls, Carlos','Martínez García, Alejandro','Trofeo Rector 2010 (Masculino)',0,994,1,0,2),('Sancho Aguilera, Argimiro','Martínez García, Alejandro','Trofeo Rector 2010 (Masculino)',0,1002,-1,1,1),('Garcia Martinez, Xavi','Giménez Costa, Moisés','Trofeo Rector 2010 (Masculino)',0,1005,-1,2,1),('Sancho Aguilera, Argimiro','Garcia Martinez, Xavi','Trofeo Rector 2010 (Masculino)',0,1007,1,3,1),('7','8','LigaInternaOct2010',0,1192,2,0,1),('9','8','LigaInternaOct2010',0,1193,2,0,1),('7','9','LigaInternaOct2010',0,1198,2,0,1),('Gómez Navas, Francis','Fernández Barjau, Mar','LigaIntDic2010',0,1259,1,0,2),('Sancho Aguilera, Argimiro','Hortelano Otero, Jorge','LigaIntDic2010',0,1260,-1,0,2),('Gomez Verdejo, Fernando','Hortelano Otero, Jorge','LigaIntDic2010',0,1261,1,0,2),('Gomez Verdejo, Fernando','Sancho Aguilera, Argimiro','LigaIntDic2010',0,1262,1,0,2),('Garcia Martinez, Xavi','Latorre Contreras, Fredi','LigaIntDic2010',0,1263,-1,0,2),('Navarro García, Luis','Latorre Contreras, Fredi','LigaIntDic2010',0,1264,1,0,2),('Navarro García, Luis','El Idrissi, Adell','LigaIntDic2010',0,1265,0,0,2),('Garcia Martinez, Xavi','El Idrissi, Adell','LigaIntDic2010',0,1266,-1,0,2),('García Pimentel, Jose Miguel','Quintanilla Serrano, Pedro','LigaIntDic2010',0,1269,1,0,2),('Martínez García, Alejandro','Quintanilla Serrano, Pedro','LigaIntDic2010',0,1270,-1,0,2),('Sancho Aguilera, Argimiro','Latorre Contreras, Fredi','LigaIntDic2010',0,1272,0,1,1),('Hortelano Otero, Jorge','Garcia Martinez, Xavi','LigaIntDic2010',0,1273,1,1,1),('Fernández Barjau, Mar','Sancho Aguilera, Argimiro','LigaIntDic2010',0,1275,-1,2,1),('Garcia Martinez, Xavi','Martínez García, Alejandro','LigaIntDic2010',0,1276,1,2,1),('Fernández Barjau, Mar','Martínez García, Alejandro','LigaIntDic2010',0,1277,-1,3,1),('Adobes Tarazona, Margarita','Alarcón Rosello, Samuel','Prueba',0,1628,-1,0,2),('Alcazar Tomas, Jorge','Alarcón Rosello, Samuel','Prueba',0,1629,-1,0,2),('Amagai, Mizuki','Aparicio Vaya, Jose Vicente','Prueba',0,1631,-1,0,2),('Arias Mateu, Melanie','Aparicio Vaya, Jose Vicente','Prueba',0,1632,-1,0,2),('Arias Mateu, Melanie','Amagai, Mizuki','Prueba',0,1633,-1,0,2),('Arnal Villalsa, Noemi','Aznar Flor, Carmen','Prueba',0,1634,1,0,2),('Baro, Jose','Aznar Flor, Carmen','Prueba',0,1635,1,0,2),('Baro, Jose','Arnal Villalsa, Noemi','Prueba',0,1636,1,0,2),('Beas Careaga, Virginia','Bisquert Alcaraz, Ricardo','Prueba',1,1637,-1,0,2),('Bonet Collado, Guillermo','Bisquert Alcaraz, Ricardo','Prueba',1,1638,-1,0,2),('Bonet Collado, Guillermo','Beas Careaga, Virginia','Prueba',1,1639,-1,0,2),('Boutonnet Antelo, Víctor','Cabas Fernández, Daniel','Prueba',1,1640,-1,0,2),('Calabuig Simó, Carlos','Cabas Fernández, Daniel','Prueba',1,1641,-1,0,2),('Calabuig Simó, Carlos','Boutonnet Antelo, Víctor','Prueba',1,1642,-1,0,2),('Alcazar Tomas, Jorge','Boutonnet Antelo, Víctor','Prueba',0,1643,-1,1,1),('Adobes Tarazona, Margarita','Arias Mateu, Melanie','Prueba',1,1644,0,1,1),('Amagai, Mizuki','Aznar Flor, Carmen','Prueba',0,1645,-1,1,1),('Arnal Villalsa, Noemi','Bonet Collado, Guillermo','Prueba',1,1646,-1,1,1),('Beas Careaga, Virginia','Calabuig Simó, Carlos','Prueba',0,1647,-1,1,1),('Alcazar Tomas, Jorge','Beas Careaga, Virginia','Prueba',0,1648,-1,2,1),('Adobes Tarazona, Margarita','Amagai, Mizuki','Prueba',1,1649,-1,2,1),('Alcazar Tomas, Jorge','Arnal Villalsa, Noemi','Prueba',0,1650,-1,3,1),('Alcazar Tomas, Jorge','Adobes Tarazona, Margarita','Prueba',0,1651,1,4,1),('Equipo A','Equipo B','Prueba2',0,1656,-1,0,1),('Equipo X','Equipo B','Prueba2',0,1657,-1,0,1),('Equipo X','Equipo A','Prueba2',0,1658,2,0,1),('Kenwakai 3 ','Youshinkai ','VIII Open & Clinic (Femenino)',0,1712,-1,0,2),('Mix Barcelona/upc 1 ','Youshinkai ','VIII Open & Clinic (Femenino)',0,1713,1,0,2),('Mix Barcelona/upc 1 ','Kenwakai 3 ','VIII Open & Clinic (Femenino)',0,1714,1,0,2),('Zanshin/meibukan ','Meigetsu La Eliana 1 ','VIII Open & Clinic (Femenino)',0,1715,-1,0,2),('Uv - Ryo ','Meigetsu La Eliana 1 ','VIII Open & Clinic (Femenino)',0,1716,2,0,2),('Uv - Ryo ','Kwk-meig-you ','VIII Open & Clinic (Femenino)',0,1717,2,0,2),('Zanshin/meibukan ','Kwk-meig-you ','VIII Open & Clinic (Femenino)',0,1718,0,0,2),('Kenwakai 2 ','Ryoshinkai Can-meibu ','VIII Open & Clinic (Masculino)',0,1742,0,0,2),('Uv 3 ','Ryoshinkai Can-meibu ','VIII Open & Clinic (Masculino)',0,1743,1,0,2),('Uv 3 ','Ryoshinkai Mallorca 1 ','VIII Open & Clinic (Masculino)',0,1744,1,0,2),('Uv 7 ','Ryoshinkai Mallorca 1 ','VIII Open & Clinic (Masculino)',0,1745,1,0,2),('Uv 7 ','Kenwakai 2 ','VIII Open & Clinic (Masculino)',0,1746,1,0,2),('Uv 2 ','Meigetsu La Eliana 2 ','VIII Open & Clinic (Masculino)',0,1747,-1,0,2),('Uv 6-bcn ','Meigetsu La Eliana 2 ','VIII Open & Clinic (Masculino)',0,1748,-1,0,2),('Uv 6-bcn ','Zanshin 1 ','VIII Open & Clinic (Masculino)',0,1749,1,0,2),('Uv 2 ','Zanshin 1 ','VIII Open & Clinic (Masculino)',0,1750,1,0,2),('Kenwakai 1 ','Ryoshinkai Mallorca 2 ','VIII Open & Clinic (Masculino)',1,1751,-1,0,2),('Uw ','Ryoshinkai Mallorca 2 ','VIII Open & Clinic (Masculino)',1,1752,-1,0,2),('Uw ','Uv 4 ','VIII Open & Clinic (Masculino)',1,1753,-1,0,2),('Makoto Shin Kai ','Uv 4 ','VIII Open & Clinic (Masculino)',1,1754,-1,0,2),('Makoto Shin Kai ','Kenwakai 1 ','VIII Open & Clinic (Masculino)',1,1755,1,0,2),('Meigetsu La Eliana 3 ','Mix Barcelona 2 ','VIII Open & Clinic (Masculino)',1,1756,0,0,2),('Shisuikan Herca ','Mix Barcelona 2 ','VIII Open & Clinic (Masculino)',1,1757,1,0,2),('Shisuikan Herca ','Zanshin 2 ','VIII Open & Clinic (Masculino)',1,1758,-1,0,2),('Uv 5 ','Zanshin 2 ','VIII Open & Clinic (Masculino)',1,1759,0,0,2),('Uv 5 ','Meigetsu La Eliana 3 ','VIII Open & Clinic (Masculino)',1,1760,0,0,2),('Ryoshinkai Mallorca 1 ','Shisuikan Herca ','VIII Open & Clinic (Masculino)',0,1765,-1,1,1),('Zanshin 1 ','Uw ','VIII Open & Clinic (Masculino)',1,1766,2,1,1),('Uv 2 ','Kenwakai 1 ','VIII Open & Clinic (Masculino)',0,1767,0,1,1),('Kenwakai 2 ','Mix Barcelona 2 ','VIII Open & Clinic (Masculino)',1,1768,2,1,1);
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

-- Dump completed on 2011-02-21 16:25:19
