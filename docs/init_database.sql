-- ============================================
-- Script SQL de création de la base de données
-- Cabinet Médical - JEE Project
-- ============================================

-- Création de la base de données
CREATE DATABASE IF NOT EXISTS cabinet_medical
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE cabinet_medical;

-- ============================================
-- Table PATIENT
-- ============================================
DROP TABLE IF EXISTS PATIENT;
CREATE TABLE PATIENT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    date_naissance DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Table MEDECIN
-- ============================================
DROP TABLE IF EXISTS MEDECIN;
CREATE TABLE MEDECIN (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    specialite VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Table RENDEZVOUS
-- ============================================
DROP TABLE IF EXISTS RENDEZVOUS;
CREATE TABLE RENDEZVOUS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    medecin_id BIGINT NOT NULL,
    date_rendez_vous DATETIME NOT NULL,
    motif TEXT,
    statut ENUM('PLANIFIE', 'TERMINE', 'ANNULE') DEFAULT 'PLANIFIE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES PATIENT(id) ON DELETE RESTRICT,
    FOREIGN KEY (medecin_id) REFERENCES MEDECIN(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Données de test
-- ============================================

-- Insertion de patients
INSERT INTO PATIENT (nom, prenom, email, telephone, date_naissance) VALUES
('Ben Ali', 'Ahmed', 'ahmed.benali@email.com', '98123456', '1990-05-15'),
('Trabelsi', 'Fatma', 'fatma.trabelsi@email.com', '96234567', '1985-08-22'),
('Mallek', 'Mohamed', 'mohamed.mallek@email.com', '98345678', '1992-03-10'),
('Hadj', 'Sarra', 'sarra.hadj@email.com', '96456789', '1995-11-30'),
('Kammoun', 'Youssef', 'youssef.kammoun@email.com', '98567890', '1988-07-18');

-- Insertion de médecins
INSERT INTO MEDECIN (nom, prenom, specialite, email) VALUES
('Sassi', 'Nabil', 'Cardiologie', 'dr.sassi@cabinet.com'),
('Boudawara', 'Leila', 'Pédiatrie', 'dr.boudawara@cabinet.com'),
('Gharbi', 'Sami', 'Généraliste', 'dr.gharbi@cabinet.com'),
('Mansour', 'Hela', 'Dermatologie', 'dr.mansour@cabinet.com'),
('Hamdi', 'Ali', 'Neurologie', 'dr.hamdi@cabinet.com');

-- Insertion de rendez-vous
INSERT INTO RENDEZVOUS (patient_id, medecin_id, date_rendez_vous, motif, statut) VALUES
(1, 1, '2026-04-25 09:00:00', 'Contrôle cardiaque annuel', 'PLANIFIE'),
(2, 2, '2026-04-25 10:30:00', 'Vaccination enfant', 'PLANIFIE'),
(3, 3, '2026-04-25 14:00:00', 'Consultation générale', 'PLANIFIE'),
(4, 4, '2026-04-26 09:30:00', 'Problème de peau', 'PLANIFIE'),
(5, 5, '2026-04-26 11:00:00', 'Maux de tête persistants', 'PLANIFIE'),
(1, 1, '2026-03-15 08:00:00', 'Urgence cardiaque', 'TERMINE'),
(2, 2, '2026-03-10 15:00:00', 'Visite de contrôle', 'TERMINE');

-- ============================================
-- Index pour optimisation
-- ============================================
CREATE INDEX idx_patient_email ON PATIENT(email);
CREATE INDEX idx_patient_nom ON PATIENT(nom);
CREATE INDEX idx_medecin_specialite ON MEDECIN(specialite);
CREATE INDEX idx_rendezvous_date ON RENDEZVOUS(date_rendez_vous);
CREATE INDEX idx_rendezvous_statut ON RENDEZVOUS(statut);

-- ============================================
-- Vérification
-- ============================================
SELECT 'Tables créées avec succès!' AS message;

SHOW TABLES;
SELECT COUNT(*) AS 'Nombre de patients' FROM PATIENT;
SELECT COUNT(*) AS 'Nombre de médecins' FROM MEDECIN;
SELECT COUNT(*) AS 'Nombre de rendez-vous' FROM RENDEZVOUS;
