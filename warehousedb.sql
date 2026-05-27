-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema warehousedb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema warehousedb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `warehousedb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `warehousedb` ;

-- -----------------------------------------------------
-- Table `warehousedb`.`address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `warehousedb`.`address` (
  `addressId` INT NOT NULL AUTO_INCREMENT,
  `street` VARCHAR(45) NULL DEFAULT NULL,
  `number` INT NULL DEFAULT NULL,
  `city` VARCHAR(45) NULL DEFAULT NULL,
  `judet` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`addressId`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `warehousedb`.`client`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `warehousedb`.`client` (
  `clientId` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `addressId` INT NULL DEFAULT NULL,
  `telephone` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`clientId`),
  INDEX `fk_address_idx` (`addressId` ASC) VISIBLE,
  CONSTRAINT `fk_address`
    FOREIGN KEY (`addressId`)
    REFERENCES `warehousedb`.`address` (`addressId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `warehousedb`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `warehousedb`.`product` (
  `productId` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL DEFAULT NULL,
  `price` DECIMAL(10,2) NULL DEFAULT NULL,
  `stock` INT NULL DEFAULT NULL,
  PRIMARY KEY (`productId`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `warehousedb`.`orders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `warehousedb`.`orders` (
  `orderId` INT NOT NULL AUTO_INCREMENT,
  `clientId` INT NULL DEFAULT NULL,
  `productId` INT NULL DEFAULT NULL,
  `quantity` INT NULL DEFAULT NULL,
  PRIMARY KEY (`orderId`),
  INDEX `fk_client_idx` (`clientId` ASC) VISIBLE,
  INDEX `fk_product_idx` (`productId` ASC) VISIBLE,
  CONSTRAINT `fk_client`
    FOREIGN KEY (`clientId`)
    REFERENCES `warehousedb`.`client` (`clientId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_product`
    FOREIGN KEY (`productId`)
    REFERENCES `warehousedb`.`product` (`productId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
