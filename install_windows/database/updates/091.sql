--
-- Definition of table `kendotournament`.`undraw`
--

CREATE TABLE IF NOT EXISTS  `kendotournament`.`undraw` (
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

--
-- Dumping data for table `kendotournament`.`undraw`
--

/*!40000 ALTER TABLE `undraw` DISABLE KEYS */;
LOCK TABLES `undraw` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `undraw` ENABLE KEYS */;


--
-- Update table `kendotournament`.`club`
--

ALTER TABLE `kendotournament`.`club` add Web char(100);
ALTER TABLE `kendotournament`.`club` add City char(20);

