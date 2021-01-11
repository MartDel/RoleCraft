-- phpMyAdmin SQL Dump
-- version 4.9.5deb2
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost:3306
-- Généré le : lun. 11 jan. 2021 à 08:04
-- Version du serveur :  8.0.22-0ubuntu0.20.04.3
-- Version de PHP : 7.4.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `rolecraft`
--

-- --------------------------------------------------------

--
-- Structure de la table `admin_grounds`
--

CREATE TABLE `admin_grounds` (
  `id` int NOT NULL,
  `owner_uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `x1` int DEFAULT NULL,
  `y1` int DEFAULT NULL,
  `z1` int DEFAULT NULL,
  `x2` int DEFAULT NULL,
  `y2` int DEFAULT NULL,
  `z2` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `admin_grounds`
--

INSERT INTO `admin_grounds` (`id`, `owner_uuid`, `x1`, `y1`, `z1`, `x2`, `y2`, `z2`) VALUES
(2, 'd985993e-f3ed-45fc-8cdf-0dc38748e73f', NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `builds`
--

CREATE TABLE `builds` (
  `id` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `owner_uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `x1` int NOT NULL,
  `y1` int NOT NULL,
  `z1` int NOT NULL,
  `x2` int NOT NULL,
  `y2` int NOT NULL,
  `z2` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `farms`
--

CREATE TABLE `farms` (
  `id` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `owner_uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `x1` int NOT NULL,
  `y1` int NOT NULL,
  `z1` int NOT NULL,
  `x2` int NOT NULL,
  `y2` int NOT NULL,
  `z2` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `houses`
--

CREATE TABLE `houses` (
  `id` int NOT NULL,
  `x1` int NOT NULL,
  `y1` int NOT NULL,
  `z1` int NOT NULL,
  `x2` int NOT NULL,
  `y2` int NOT NULL,
  `z2` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `players`
--

CREATE TABLE `players` (
  `uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `pseudo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `admin` bit(1) NOT NULL DEFAULT b'0',
  `level` int NOT NULL DEFAULT '1',
  `score` int NOT NULL DEFAULT '0',
  `job` int DEFAULT NULL,
  `spe` bit(1) NOT NULL DEFAULT b'0',
  `house` int DEFAULT NULL,
  `shop` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `shops`
--

CREATE TABLE `shops` (
  `id` int NOT NULL,
  `x1` int NOT NULL,
  `y1` int NOT NULL,
  `z1` int NOT NULL,
  `x2` int NOT NULL,
  `y2` int NOT NULL,
  `z2` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `admin_grounds`
--
ALTER TABLE `admin_grounds`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `builds`
--
ALTER TABLE `builds`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `farms`
--
ALTER TABLE `farms`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `houses`
--
ALTER TABLE `houses`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `players`
--
ALTER TABLE `players`
  ADD PRIMARY KEY (`uuid`);

--
-- Index pour la table `shops`
--
ALTER TABLE `shops`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `admin_grounds`
--
ALTER TABLE `admin_grounds`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `builds`
--
ALTER TABLE `builds`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `farms`
--
ALTER TABLE `farms`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `houses`
--
ALTER TABLE `houses`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `shops`
--
ALTER TABLE `shops`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
