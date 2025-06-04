-- Création de la base de données
CREATE DATABASE IF NOT EXISTS notification_system;
USE notification_system;

-- Table des employés
CREATE TABLE IF NOT EXISTS employes (
    id VARCHAR(50) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE
);

-- Table des notifications
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT NOT NULL,
    expediteur_id VARCHAR(50),
    destinataire_id VARCHAR(50),
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (expediteur_id) REFERENCES employes(id),
    FOREIGN KEY (destinataire_id) REFERENCES employes(id)
);

-- Table des abonnements
CREATE TABLE IF NOT EXISTS abonnements (
    employe_id VARCHAR(50),
    date_abonnement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employe_id) REFERENCES employes(id),
    PRIMARY KEY (employe_id)
);

-- Création de l'administrateur par défaut
INSERT IGNORE INTO employes (id, nom, email, mot_de_passe, is_admin)
VALUES ('1', 'Admin', 'admin@entreprise.com', 'admin123', true); 