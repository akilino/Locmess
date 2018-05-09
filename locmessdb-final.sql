SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------------
-- Schema LocMessDB
-- -----------------------------------------------------------
DROP SCHEMA IF EXISTS `LocMessDB`;

-- -----------------------------------------------------------
-- Schema LocMessDB
-- -----------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `LocMessDB` DEFAULT CHARACTER SET utf8;
USE `LocMessDB`;

-- -----------------------------------------------------------
-- Table 'LocMessDB'.'users'
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `LocMessDB`.`user` (
	`username` VARCHAR(25) NOT NULL,
	`password` VARCHAR(25) NOT NULL,
	`sessionid` VARCHAR(25),
	PRIMARY KEY (`username`))
ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table 'LocMessDB'.'profiles'
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `LocMessDB`.`profile` (
	`profile_id` INT NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(25) NOT NULL,
	`keypair` VARCHAR(25) NOT NULL,
	PRIMARY KEY (`profile_id`),
	FOREIGN KEY (`username`) REFERENCES `LocMessDB`.`user` (`username`))	
ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table 'LocMessDB'.'locations'
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `LocMessDB`.`location` (
	`location_name` VARCHAR(50) NOT NULL,
	`longitude` FLOAT NOT NULL,
	`latitude` FLOAT NOT NULL,
	`radius` INT NOT NULL,
	PRIMARY KEY (`location_name`))
ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table 'LocMessDB'.'messages'
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `LocMessDB`.`message` (
	`message_id` INT NOT NULL AUTO_INCREMENT,
	`location_name` VARCHAR(50) NOT NULL,
	`username` VARCHAR(25) NOT NULL,
	`policy` VARCHAR(25) NOT NULL,
	`policy_keypairs` VARCHAR(255),
	`creation_time` VARCHAR(20) NOT NULL,
	`begin_time` VARCHAR(20) NOT NULL,
	`end_time` VARCHAR(20) NOT NULL,
	`content` VARCHAR(255) NOT NULL,
	PRIMARY KEY (`message_id`),
	FOREIGN KEY (`location_name`)
	REFERENCES `LocMessDB`.`location` (`location_name`),
	FOREIGN KEY (`username`)
	REFERENCES `LocMessDB`.`user` (`username`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;