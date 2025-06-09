package com.notification.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/init_database";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void initializeDatabase() {
        try {
            // Créer la base de données si elle n'existe pas
            Connection tempConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306", USER, PASSWORD);
            Statement statement = tempConnection.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS init_database");
            statement.close();
            tempConnection.close();

            // Connecter à la base de données et créer les tables
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();

            // Table des employés
            String createEmployesTable = """
                CREATE TABLE IF NOT EXISTS employes (
                    id VARCHAR(50) PRIMARY KEY,
                    nom VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    mot_de_passe VARCHAR(100) NOT NULL,
                    is_admin BOOLEAN DEFAULT FALSE
                )
            """;
            stmt.executeUpdate(createEmployesTable);

            // Table des notifications
            String createNotificationsTable = """
                CREATE TABLE IF NOT EXISTS notifications (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    message TEXT NOT NULL,
                    expediteur_id VARCHAR(50),
                    destinataire_id VARCHAR(50),
                    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (expediteur_id) REFERENCES employes(id),
                    FOREIGN KEY (destinataire_id) REFERENCES employes(id)
                )
            """;
            stmt.executeUpdate(createNotificationsTable);

            // Table des abonnements
            String createAbonnementsTable = """
                CREATE TABLE IF NOT EXISTS abonnements (
                    employe_id VARCHAR(50),
                    date_abonnement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (employe_id) REFERENCES employes(id),
                    PRIMARY KEY (employe_id)
                )
            """;
            stmt.executeUpdate(createAbonnementsTable);

            // Créer l'administrateur s'il n'existe pas
            String checkAdmin = """
                INSERT IGNORE INTO employes (id, nom, email, mot_de_passe, is_admin)
                VALUES ('1', 'Admin', 'admin@gmail.com', 'admin123', true)
            """;
            stmt.executeUpdate(checkAdmin);

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }
} 