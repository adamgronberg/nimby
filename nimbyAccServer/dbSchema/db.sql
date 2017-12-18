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
  `token` VARCHAR(100) NULL,
  `hash` VARCHAR(100) NULL,
  `salt` VARCHAR(100) NULL,
  PRIMARY KEY (`profileName`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `email_UNIQUE` ON `nimby_acc_server_db`.`Account` (`email` ASC);


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
  `builder` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`name`, `builder`),
  CONSTRAINT `fk_Ship_Account1`
    FOREIGN KEY (`builder`)
    REFERENCES `nimby_acc_server_db`.`Account` (`profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fk_Ship_Account1_idx` ON `nimby_acc_server_db`.`Ship` (`builder` ASC);


-- -----------------------------------------------------
-- Table `nimby_acc_server_db`.`friendWith`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db`.`friendWith` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`friendWith` (
  `account1` VARCHAR(20) NOT NULL,
  `account2` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`account1`, `account2`),
  CONSTRAINT `friendWithAcc1`
    FOREIGN KEY (`account1`)
    REFERENCES `nimby_acc_server_db`.`Account` (`profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `friendWithAcc2`
    FOREIGN KEY (`account2`)
    REFERENCES `nimby_acc_server_db`.`Account` (`profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `friendWithAcc1_idx` ON `nimby_acc_server_db`.`friendWith` (`account1` ASC);

CREATE INDEX `friendWithAcc2_idx` ON `nimby_acc_server_db`.`friendWith` (`account2` ASC);


-- -----------------------------------------------------
-- Table `nimby_acc_server_db`.`hasScore`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db`.`hasScore` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`hasScore` (
  `account` VARCHAR(20) NOT NULL,
  `scoreBoard` VARCHAR(40) NOT NULL,
  `score` INT NULL DEFAULT 0,
  PRIMARY KEY (`account`, `scoreBoard`),
  CONSTRAINT `hasScoreAcc`
    FOREIGN KEY (`account`)
    REFERENCES `nimby_acc_server_db`.`Account` (`profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `hasScorename`
    FOREIGN KEY (`scoreBoard`)
    REFERENCES `nimby_acc_server_db`.`ScoreBoard` (`name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `hasScoreAcc_idx` ON `nimby_acc_server_db`.`hasScore` (`account` ASC);

CREATE INDEX `hasScorename_idx` ON `nimby_acc_server_db`.`hasScore` (`scoreBoard` ASC);


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
  `federation` VARCHAR(40) NOT NULL,
  `rank` VARCHAR(10) NOT NULL DEFAULT 'Member',
  PRIMARY KEY (`account`),
  CONSTRAINT `fk_federationMember_Federation1`
    FOREIGN KEY (`federation`)
    REFERENCES `nimby_acc_server_db`.`Federation` (`federationName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_federationMember_Account1`
    FOREIGN KEY (`account`)
    REFERENCES `nimby_acc_server_db`.`Account` (`profileName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
	CONSTRAINT `right_rank` CHECK (rank LIKE 'Cadet' OR rank LIKE 'General' OR rank LIKE 'Admiral'))
ENGINE = InnoDB;

CREATE INDEX `fk_federationMember_Federation1_idx` ON `nimby_acc_server_db`.`federationMember` (`federation` ASC);


-- -----------------------------------------------------
-- Table `nimby_acc_server_db`.`Part`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nimby_acc_server_db`.`Part` ;

CREATE TABLE IF NOT EXISTS `nimby_acc_server_db`.`Part` (
  `name` VARCHAR(20) NOT NULL,
  `hp` INT NOT NULL,
  `mass` INT NOT NULL,
  `texture` VARCHAR(45) NOT NULL,
  `skillCode` BLOB NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
