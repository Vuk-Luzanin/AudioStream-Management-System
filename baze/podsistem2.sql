CREATE DATABASE  IF NOT EXISTS `podsistem2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `podsistem2`;
-- MySQL dump 10.13  Distrib 8.0.40, for macos14 (arm64)
--
-- Host: localhost    Database: podsistem2
-- ------------------------------------------------------
-- Server version	9.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audio`
--

DROP TABLE IF EXISTS `audio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audio` (
  `idAudio` int NOT NULL AUTO_INCREMENT,
  `Naziv` varchar(255) NOT NULL,
  `Trajanje` decimal(10,3) NOT NULL,
  `idKorisnik` int DEFAULT NULL,
  `DatumPostavljanja` datetime NOT NULL,
  PRIMARY KEY (`idAudio`),
  KEY `FK_idKorisnik_Audio_idx` (`idKorisnik`),
  CONSTRAINT `FK_idKorisnik_Audio` FOREIGN KEY (`idKorisnik`) REFERENCES `podsistem1`.`korisnik` (`idKorisnik`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audio`
--

LOCK TABLES `audio` WRITE;
/*!40000 ALTER TABLE `audio` DISABLE KEYS */;
INSERT INTO `audio` VALUES (6,'VukSnimak1',55.400,1,'2025-01-12 12:00:00'),(7,'VukSnimak2',1.400,1,'2024-03-03 12:30:00'),(8,'JelenaSnimak1',34.700,4,'2022-12-10 21:00:03'),(9,'JelenaSnimak2',34.000,4,'2020-09-08 13:45:00'),(10,'PavleSnimak1',21.000,5,'2014-04-26 14:26:15'),(11,'PavleSnimak2',45.000,5,'2011-08-08 14:15:00'),(12,'LukaSnimak1',2.140,6,'2025-04-01 21:00:00'),(13,'MarkoSnimak1',13.000,7,'2023-04-15 13:14:24'),(15,'LukaSnimak2',4.200,6,'2010-09-10 12:00:00');
/*!40000 ALTER TABLE `audio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audioKategorija`
--

DROP TABLE IF EXISTS `audioKategorija`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audioKategorija` (
  `idAudio` int NOT NULL,
  `idKategorija` int NOT NULL,
  PRIMARY KEY (`idAudio`,`idKategorija`),
  KEY `FK_idKategorija_audioKategorija_idx` (`idKategorija`),
  CONSTRAINT `FK_idAudio_audioKategorija` FOREIGN KEY (`idAudio`) REFERENCES `audio` (`idAudio`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_idKategorija_audioKategorija` FOREIGN KEY (`idKategorija`) REFERENCES `kategorija` (`idKategorija`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audioKategorija`
--

LOCK TABLES `audioKategorija` WRITE;
/*!40000 ALTER TABLE `audioKategorija` DISABLE KEYS */;
INSERT INTO `audioKategorija` VALUES (7,5),(12,5),(8,6),(10,6),(6,7),(7,8),(6,9),(11,9),(6,10),(13,10),(12,12);
/*!40000 ALTER TABLE `audioKategorija` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kategorija`
--

DROP TABLE IF EXISTS `kategorija`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kategorija` (
  `idKategorija` int NOT NULL AUTO_INCREMENT,
  `Naziv` varchar(255) NOT NULL,
  PRIMARY KEY (`idKategorija`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kategorija`
--

LOCK TABLES `kategorija` WRITE;
/*!40000 ALTER TABLE `kategorija` DISABLE KEYS */;
INSERT INTO `kategorija` VALUES (5,'kratak'),(6,'srednji'),(7,'dug'),(8,'zabavni'),(9,'edukativni'),(10,'podcast'),(11,'standardan'),(12,'pesma');
/*!40000 ALTER TABLE `kategorija` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-13  1:04:56
