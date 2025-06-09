package com.notification.dao;

import com.notification.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Abonnements {
    
    public void abonner(String employeId) throws SQLException {
        String sql = "INSERT INTO abonnements (employe_id) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeId);
            pstmt.executeUpdate();
        }
    }

    public void desabonner(String employeId) throws SQLException {
        String sql = "DELETE FROM abonnements WHERE employe_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeId);
            pstmt.executeUpdate();
        }
    }

    public boolean estAbonne(String employeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM abonnements WHERE employe_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<String> getAbonnesIds() throws SQLException {
        List<String> abonnesIds = new ArrayList<>();
        String sql = """
            SELECT a.employe_id 
            FROM abonnements a 
            JOIN employes e ON a.employe_id = e.id 
            WHERE e.is_admin = false
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                abonnesIds.add(rs.getString("employe_id"));
            }
        }
        return abonnesIds;
    }
} 