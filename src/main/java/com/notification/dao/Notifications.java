package com.notification.dao;

import com.notification.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Notifications {
    
    public void sauvegarderNotification(String message, String expediteurId, String destinataireId) throws SQLException {
        String sql = "INSERT INTO notifications (message, expediteur_id, destinataire_id, date_envoi) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, message);
            pstmt.setString(2, expediteurId);
            pstmt.setString(3, destinataireId);
            pstmt.executeUpdate();
        }
    }

    public List<String> getNotificationsUtilisateur(String employeId) throws SQLException {
        List<String> notifications = new ArrayList<>();
        String sql = "SELECT n.message, n.date_envoi, e.nom as expediteur " +
                    "FROM notifications n " +
                    "LEFT JOIN employes e ON n.expediteur_id = e.id " +
                    "WHERE n.destinataire_id = ? " +
                    "ORDER BY n.date_envoi DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String expediteur = rs.getString("expediteur");
                    String message = rs.getString("message");
                    Timestamp dateEnvoi = rs.getTimestamp("date_envoi");
                    notifications.add(String.format("[%s] De: %s - %s",
                            dateEnvoi.toString(),
                            expediteur != null ? expediteur : "Système",
                            message));
                }
            }
        }
        return notifications;
    }

    public List<String> getHistoriqueNotifications() throws SQLException {
        List<String> notifications = new ArrayList<>();
        String sql = "SELECT n.message, n.date_envoi, " +
                    "e1.nom as expediteur, " +
                    "e2.nom as destinataire " +
                    "FROM notifications n " +
                    "LEFT JOIN employes e1 ON n.expediteur_id = e1.id " +
                    "LEFT JOIN employes e2 ON n.destinataire_id = e2.id " +
                    "ORDER BY n.date_envoi DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String expediteur = rs.getString("expediteur");
                String destinataire = rs.getString("destinataire");
                String message = rs.getString("message");
                Timestamp dateEnvoi = rs.getTimestamp("date_envoi");
                notifications.add(String.format("[%s] De: %s → À: %s - %s",
                        dateEnvoi.toString(),
                        expediteur != null ? expediteur : "Système",
                        destinataire,
                        message));
            }
        }
        return notifications;
    }
} 