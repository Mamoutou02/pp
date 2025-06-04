package com.notification.dao;

import com.notification.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    
    public void sauvegarderNotification(String message, String expediteurId, String destinataireId) throws SQLException {
        // Vérifier si une notification identique existe déjà
        String checkSql = """
            SELECT COUNT(*) FROM notifications 
            WHERE message = ? AND expediteur_id = ? AND destinataire_id = ? 
            AND date_envoi >= NOW() - INTERVAL 5 SECOND
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, message);
            checkStmt.setString(2, expediteurId);
            checkStmt.setString(3, destinataireId);
            
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Une notification identique existe déjà, ne pas la dupliquer
                return;
            }
        }

        // Si aucune notification identique n'existe, insérer la nouvelle
        String insertSql = "INSERT INTO notifications (message, expediteur_id, destinataire_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, message);
            pstmt.setString(2, expediteurId);
            pstmt.setString(3, destinataireId);
            pstmt.executeUpdate();
        }
    }

    public List<String> getNotificationsUtilisateur(String employeId) throws SQLException {
        List<String> notifications = new ArrayList<>();
        String sql = """
            SELECT n.message, e.nom as expediteur_nom, n.date_envoi 
            FROM notifications n 
            JOIN employes e ON n.expediteur_id = e.id 
            WHERE n.destinataire_id = ? 
            ORDER BY n.date_envoi DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String notification = String.format("Message de %s [%s] : %s",
                    rs.getString("expediteur_nom"),
                    rs.getTimestamp("date_envoi"),
                    rs.getString("message")
                );
                notifications.add(notification);
            }
        }
        return notifications;
    }
} 