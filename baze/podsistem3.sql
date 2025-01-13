CREATE DATABASE  IF NOT EXISTS `podsistem3` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `podsistem3`;
-- MySQL dump 10.13  Distrib 8.0.40, for macos14 (arm64)
--
-- Host: localhost    Database: podsistem3
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
-- Table structure for table `ocena`
--

DROP TABLE IF EXISTS `ocena`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ocena` (
  `idOcena` int NOT NULL AUTO_INCREMENT,
  `idKorisnik` int DEFAULT NULL,
  `idAudio` int DEFAULT NULL,
  `Ocena` int NOT NULL,
  `Datum` datetime NOT NULL,
  PRIMARY KEY (`idOcena`),
  KEY `FK_idKorisnik_Ocena_idx` (`idKorisnik`),
  KEY `FK_idAudio_Ocena_idx` (`idAudio`),
  CONSTRAINT `FK_idAudio_Ocena` FOREIGN KEY (`idAudio`) REFERENCES `podsistem2`.`Audio` (`idAudio`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_idKorisnik_Ocena` FOREIGN KEY (`idKorisnik`) REFERENCES `podsistem1`.`Korisnik` (`idKorisnik`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ocena`
--

LOCK TABLES `ocena` WRITE;
/*!40000 ALTER TABLE `ocena` DISABLE KEYS */;
INSERT INTO `ocena` VALUES (3,6,6,4,'2025-04-10 20:00:00'),(5,6,12,5,'2025-09-10 10:09:56'),(6,6,15,5,'2015-09-09 09:09:09'),(7,7,6,3,'2025-09-09 19:00:09'),(8,7,13,4,'2025-08-01 13:00:00'),(9,7,9,4,'2025-05-23 23:00:00'),(10,5,10,2,'2025-02-10 14:13:12'),(11,1,15,4,'2024-09-09 10:00:00'),(12,1,13,2,'2025-10-10 17:09:08'),(13,5,13,1,'2025-09-08 20:00:00'),(14,5,7,4,'2025-05-12 12:02:20');
/*!40000 ALTER TABLE `ocena` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `omiljeniSnimci`
--

DROP TABLE IF EXISTS `omiljeniSnimci`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `omiljeniSnimci` (
  `idKorisnik` int NOT NULL,
  `idAudio` int NOT NULL,
  PRIMARY KEY (`idKorisnik`,`idAudio`),
  KEY `FK_idAudio_OmiljeniSnimci_idx` (`idAudio`),
  CONSTRAINT `FK_idAudio_OmiljeniSnimci` FOREIGN KEY (`idAudio`) REFERENCES `podsistem2`.`Audio` (`idAudio`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_idKorisnik_OmiljeniSnimci` FOREIGN KEY (`idKorisnik`) REFERENCES `podsistem1`.`Korisnik` (`idKorisnik`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `omiljeniSnimci`
--

LOCK TABLES `omiljeniSnimci` WRITE;
/*!40000 ALTER TABLE `omiljeniSnimci` DISABLE KEYS */;
INSERT INTO `omiljeniSnimci` VALUES (1,6),(4,6),(5,6),(6,6),(5,7),(6,7),(1,9),(7,10),(1,11),(4,11),(7,11),(7,12),(1,13),(4,13);
/*!40000 ALTER TABLE `omiljeniSnimci` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `paket`
--

DROP TABLE IF EXISTS `paket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `paket` (
  `idPaket` int NOT NULL AUTO_INCREMENT,
  `Naziv` varchar(255) NOT NULL,
  `Cena` decimal(10,3) NOT NULL,
  PRIMARY KEY (`idPaket`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `paket`
--

LOCK TABLES `paket` WRITE;
/*!40000 ALTER TABLE `paket` DISABLE KEYS */;
INSERT INTO `paket` VALUES (3,'porodicni',3400.000),(4,'pojedinacni',1500.000),(5,'grupni',2500.000),(6,'limited',1200.000);
/*!40000 ALTER TABLE `paket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pretplata`
--

DROP TABLE IF EXISTS `pretplata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pretplata` (
  `idPretplata` int NOT NULL AUTO_INCREMENT,
  `idKorisnik` int DEFAULT NULL,
  `idPaket` int DEFAULT NULL,
  `DatumPocetka` datetime NOT NULL,
  `Cena` decimal(10,3) NOT NULL,
  PRIMARY KEY (`idPretplata`),
  KEY `FK_idKorisnik_Pretplata_idx` (`idKorisnik`),
  KEY `FK_idPaket_Pretplata_idx` (`idPaket`),
  CONSTRAINT `FK_idKorisnik_Pretplata` FOREIGN KEY (`idKorisnik`) REFERENCES `podsistem1`.`Korisnik` (`idKorisnik`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_idPaket_Pretplata` FOREIGN KEY (`idPaket`) REFERENCES `paket` (`idPaket`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pretplata`
--

LOCK TABLES `pretplata` WRITE;
/*!40000 ALTER TABLE `pretplata` DISABLE KEYS */;
INSERT INTO `pretplata` VALUES (2,1,3,'2025-01-01 12:00:00',3500.000),(3,4,4,'2025-02-07 13:00:00',1500.000),(4,5,6,'2025-01-15 15:00:00',1200.000),(5,7,5,'2025-01-25 16:15:00',2500.000);
/*!40000 ALTER TABLE `pretplata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `slusanje`
--

DROP TABLE IF EXISTS `slusanje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `slusanje` (
  `idSlusanje` int NOT NULL AUTO_INCREMENT,
  `idKorisnik` int DEFAULT NULL,
  `idAudio` int DEFAULT NULL,
  `DatumPocetka` datetime NOT NULL,
  `SekundPocetka` int NOT NULL,
  `SekundOdslusano` int NOT NULL,
  PRIMARY KEY (`idSlusanje`),
  KEY `FK_idKorisnik_Slusanje_idx` (`idKorisnik`),
  KEY `FK_idAudio_Slusanje_idx` (`idAudio`),
  CONSTRAINT `FK_idAudio_Slusanje` FOREIGN KEY (`idAudio`) REFERENCES `podsistem2`.`Audio` (`idAudio`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_idKorisnik_Slusanje` FOREIGN KEY (`idKorisnik`) REFERENCES `podsistem1`.`Korisnik` (`idKorisnik`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `slusanje`
--

LOCK TABLES `slusanje` WRITE;
/*!40000 ALTER TABLE `slusanje` DISABLE KEYS */;
INSERT INTO `slusanje` VALUES (10,1,6,'2025-02-01 13:00:00',2,34),(13,7,8,'2025-10-23 12:23:11',3,200),(14,7,10,'2025-05-12 12:00:12',4,900),(15,6,6,'2025-02-13 19:18:00',3,80),(16,6,7,'2025-08-13 11:31:40',40,50),(17,5,13,'2025-01-09 13:14:15',1,21),(18,5,6,'2025-02-01 19:00:00',4,100),(19,5,6,'2025-02-11 18:15:00',4,50),(20,4,9,'2025-06-12 20:00:00',56,20),(21,4,8,'2025-04-23 09:12:34',5,43),(22,4,7,'2025-04-12 23:04:02',40,70);
/*!40000 ALTER TABLE `slusanje` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-13  1:04:24
