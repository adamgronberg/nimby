SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `nimby_acc_server_db` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `nimby_acc_server_db` ;

-- -----------------------------------------------------
-- Table `nimby_acc_server_db`.`Account`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db`.`Account` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`Account` (
  `profileName` VARCHAR(20) NOT NULL,
  `password` VARCHAR(40) NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `shipSlot` INT NULL DEFAULT '10',
  `token` CHAR(32) NULL,
  PRIMARY KEY (`profileName`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db`.`ScoreBoard`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db`.`ScoreBoard` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`ScoreBoard` (
  `name` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db`.`Ship`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db`.`Ship` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`Ship` (
  `name` VARCHAR(20) NOT NULL,
  `description` VARCHAR(255) NULL DEFAULT 'No description',
  `data` BLOB NOT NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db`.`friendWith`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db`.`friendWith` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`friendWith` (
  `account1` VARCHAR(20) NOT NULL,
  `account2` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`account1`, `account2`),
  INDEX `profileName_idx` (`account1` ASC, `account2` ASC),
  foreign key(`account1`) references  `nimby_acc_server_db`.`Account` (`profileName`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
foreign key(`account2`) references  `nimby_acc_server_db`.`Account` (`profileName`)
  ON DELETE CASCADE
  ON UPDATE CASCADE) 
/*CONSTRAINT `profileName`
    FOREIGN KEY (`account1` , `account2`)
    REFERENCES `nimby_acc_server_db`.`Account` (`profileName` , `profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)*/
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db`.`builtBy`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db`.`builtBy` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`builtBy` (
  `ship` VARCHAR(20) NOT NULL,
  `account` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`ship`, `account`),
  INDEX `profileName_idx` (`account` ASC),
  INDEX `name_idx` (`ship` ASC),
  CONSTRAINT `profileName`
    FOREIGN KEY (`account`)
    REFERENCES `nimby_acc_server_db`.`Account` (`profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `name`
    FOREIGN KEY (`ship`)
    REFERENCES `nimby_acc_server_db`.`Ship` (`name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db`.`hasScore`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db`.`hasScore` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`hasScore` (
  `account` VARCHAR(20) NOT NULL,
  `scoreBoard` VARCHAR(40) NOT NULL,
  `score` INT NULL DEFAULT 0,
  PRIMARY KEY (`account`, `scoreBoard`),
  INDEX `profileName_idx` (`account` ASC),
  INDEX `name_idx` (`scoreBoard` ASC),
  CONSTRAINT `hasScore_profileName`
    FOREIGN KEY (`account`)
    REFERENCES `nimby_acc_server_db`.`Account` (`profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `hasScore_name`
    FOREIGN KEY (`scoreBoard`)
    REFERENCES `nimby_acc_server_db`.`ScoreBoard` (`name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db`.`Federation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db`.`Federation` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`Federation` (
  `federationName` VARCHAR(40) NOT NULL,
  `logo` BLOB NULL,
  PRIMARY KEY (`federationName`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db`.`federationMember`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db`.`federationMember` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`federationMember` (
  `account` VARCHAR(20) NOT NULL,
  `federationName` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`account`, `federationName`),
  INDEX `profileName_idx` (`account` ASC),
  INDEX `federationName_idx` (`federationName` ASC),
  CONSTRAINT `federationMember_profileName`
    FOREIGN KEY (`account`)
    REFERENCES `nimby_acc_server_db`.`Account` (`profileName`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `federationMember_federationName`
    FOREIGN KEY (`federationName`)
    REFERENCES `nimby_acc_server_db`.`Federation` (`federationName`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `nimby_acc_server_db` ;

-- -----------------------------------------------------
-- Placeholder table for view `nimby_acc_server_db`.`scores`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`scores` (`profileName` INT);

-- -----------------------------------------------------
-- View `nimby_acc_server_db`.`scores`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `nimby_acc_server_db`.`scores` ;
DROP TABLE IF EXISTS `nimby_acc_server_db`.`scores`;
USE `nimby_acc_server_db`;
CREATE  OR REPLACE VIEW `nimby_acc_server_db`.`scores` AS

SELECT A.`profileName` , B.scoreBoard, B.score
FROM `nimby_acc_server_db`.`Account` A
NATURAL JOIN `nimby_acc_server_db`.`hasScore` B
WHERE A.profileName LIKE B.account
ORDER BY score;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
