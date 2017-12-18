SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `nimby_acc_server_db_test` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `nimby_acc_server_db_test` ;

-- -----------------------------------------------------
-- Table `nimby_acc_server_db_test`.`Account`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db_test`.`Account` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db_test`.`Account` (
  `profileName` VARCHAR(20) NOT NULL,
  `password` VARCHAR(40) NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `shipSlot` INT NULL DEFAULT '10',
  `token` CHAR(32) NULL,
  PRIMARY KEY (`profileName`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db_test`.`ScoreBoard`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db_test`.`ScoreBoard` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db_test`.`ScoreBoard` (
  `name` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db_test`.`Ship`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db_test`.`Ship` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db_test`.`Ship` (
  `name` VARCHAR(20) NOT NULL,
  `description` VARCHAR(255) NULL DEFAULT 'No description',
  `data` BLOB NOT NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db_test`.`friendWith`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db_test`.`friendWith` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db_test`.`friendWith` (
  `account1` VARCHAR(20) NOT NULL,
  `account2` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`account1`, `account2`),
  INDEX `friendWithAcc1_idx` (`account1` ASC),
  INDEX `friendWithAcc2_idx` (`account2` ASC),
  CONSTRAINT `friendWithAcc1`
    FOREIGN KEY (`account1`)
    REFERENCES `nimby_acc_server_db_test`.`Account` (`profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `friendWithAcc2`
    FOREIGN KEY (`account2`)
    REFERENCES `nimby_acc_server_db_test`.`Account` (`profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db_test`.`builtBy`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db_test`.`builtBy` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db_test`.`builtBy` (
  `ship` VARCHAR(20) NOT NULL,
  `account` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`ship`, `account`),
  INDEX `builtByAcc_idx` (`account` ASC),
  INDEX `builtByShip_idx` (`ship` ASC),
  CONSTRAINT `builtByAcc`
    FOREIGN KEY (`account`)
    REFERENCES `nimby_acc_server_db_test`.`Account` (`profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `builtByShip`
    FOREIGN KEY (`ship`)
    REFERENCES `nimby_acc_server_db_test`.`Ship` (`name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db_test`.`hasScore`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db_test`.`hasScore` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db_test`.`hasScore` (
  `account` VARCHAR(20) NOT NULL,
  `scoreBoard` VARCHAR(40) NOT NULL,
  `score` INT NULL DEFAULT 0,
  PRIMARY KEY (`account`, `scoreBoard`),
  INDEX `hasScoreAcc_idx` (`account` ASC),
  INDEX `hasScorename_idx` (`scoreBoard` ASC),
  CONSTRAINT `hasScoreAcc`
    FOREIGN KEY (`account`)
    REFERENCES `nimby_acc_server_db_test`.`Account` (`profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `hasScorename`
    FOREIGN KEY (`scoreBoard`)
    REFERENCES `nimby_acc_server_db_test`.`ScoreBoard` (`name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db_test`.`Federation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db_test`.`Federation` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db_test`.`Federation` (
  `federationName` VARCHAR(40) NOT NULL,
  `logo` BLOB NULL,
  PRIMARY KEY (`federationName`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nimby_acc_server_db_test`.`federationMember`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db_test`.`federationMember` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db_test`.`federationMember` (
  `account` VARCHAR(20) NOT NULL,
  `federationName` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`account`, `federationName`),
  INDEX `federationMemAcc_idx` (`account` ASC),
  INDEX `federationMemFed_idx` (`federationName` ASC),
  CONSTRAINT `federationMemAcc`
    FOREIGN KEY (`account`)
    REFERENCES `nimby_acc_server_db_test`.`Account` (`profileName`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `federationMemFed`
    FOREIGN KEY (`federationName`)
    REFERENCES `nimby_acc_server_db_test`.`Federation` (`federationName`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `nimby_acc_server_db_test` ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
