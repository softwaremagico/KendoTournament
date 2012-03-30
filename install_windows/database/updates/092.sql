--
-- Change Primary Key of team.
--

ALTER TABLE `kendotournament`.`team` DROP PRIMARY KEY,
 ADD PRIMARY KEY  USING BTREE(`Name`, `Tournament`, `Position`);


ALTER TABLE `kendotournament`.`team` MODIFY COLUMN `LeagueGroup` INTEGER  NOT NULL DEFAULT '0' COMMENT 'In a league, a team is included in a group.';


ALTER TABLE `kendotournament`.`tournament` ADD COLUMN `ScoreDraw` INT NOT NULL DEFAULT 0  AFTER `Type` , ADD COLUMN `ScoreWin` INT NOT NULL DEFAULT 1  AFTER `Type` , ADD COLUMN `ScoreType` CHAR(15) NOT NULL DEFAULT 'Classic'  AFTER `ScoreDraw` ;


ALTER TABLE `kendotournament`.`club` ADD COLUMN `Address` CHAR(100) NULL DEFAULT NULL  AFTER `Web` ;

ALTER TABLE `kendotournament`.`tournament` ADD COLUMN `Diploma` MEDIUMBLOB NULL  AFTER `ScoreType` ;

ALTER TABLE `kendotournament`.`tournament` ADD COLUMN `Accreditation` MEDIUMBLOB NULL  AFTER `Diploma` ;

ALTER TABLE `kendotournament`.`role` ADD COLUMN `ImpressCard` TINYINT(1)  NULL DEFAULT FALSE  AFTER `ID` ;

ALTER TABLE `kendotournament`.`club` CHANGE COLUMN `Phone` `Phone` BIGINT NULL DEFAULT NULL COMMENT 'Phone number of the representative of the club'  ;

ALTER TABLE `kendotournament`.`club` CHANGE COLUMN `Mail` `Mail` CHAR(50) NULL DEFAULT NULL COMMENT 'Email of the representative'  ;

ALTER IGNORE TABLE `kendotournament`.`team` DROP PRIMARY KEY, ADD PRIMARY KEY (`Tournament`, `Position`, `Name`, `Member`) ;

ALTER TABLE `kendotournament`.`role` ADD COLUMN `ImpressDiploma` TINYINT(1)  NULL DEFAULT '0'  AFTER `ImpressCard` ;

ALTER TABLE `kendotournament`.`team` ADD COLUMN `Level` INT(2) NOT NULL DEFAULT 0  AFTER `LeagueGroup` ;

ALTER TABLE `kendotournament`.`club` CHANGE COLUMN `Phone` `Phone` VARCHAR(20) NULL DEFAULT NULL COMMENT 'Phone number of the representative of the club'  ;

ALTER TABLE `kendotournament`.`team` DROP PRIMARY KEY, ADD PRIMARY KEY (`Name`, `Tournament`, `Position`) ;







