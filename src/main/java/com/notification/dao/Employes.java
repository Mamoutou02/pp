package com.notification.dao;

import com.notification.db.DatabaseConnection;
import com.notification.modele.Employe;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe gérant la persistance des employés
 */
public class Employes {
    
    public boolean existeParEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employes WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean existeParId(String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    public boolean sauvegarder(Employe employe) throws SQLException {
        // Vérifier si l'email existe déjà
        if (existeParEmail(employe.getEmail())) {
            System.out.println("Un employé avec cet email existe déjà.");
            return false;
        }

        // Vérifier si l'ID existe déjà
        if (existeParId(employe.getId())) {
            System.out.println("Un employé avec cet ID existe déjà.");
            return false;
        }

        String sql = "INSERT INTO employes (id, nom, email, mot_de_passe) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employe.getId());
            pstmt.setString(2, employe.getNom());
            pstmt.setString(3, employe.getEmail());
            pstmt.setString(4, employe.getMotDePasse());
            pstmt.executeUpdate();
            System.out.println("Employé ajouté avec succès !");
            return true;
        }
    }

    public Optional<Employe> trouverParEmail(String email) throws SQLException {
        String sql = "SELECT * FROM employes WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Employe(
                    rs.getString("id"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe")
                ));
            }
        }
        return Optional.empty();
    }

    public Optional<Employe> trouverParId(String id) throws SQLException {
        String sql = "SELECT * FROM employes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Employe(
                    rs.getString("id"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe")
                ));
            }
        }
        return Optional.empty();
    }

    public List<Employe> listerTous() throws SQLException {
        List<Employe> employes = new ArrayList<>();
        String sql = "SELECT * FROM employes WHERE is_admin = false";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employes.add(new Employe(
                    rs.getString("id"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe")
                ));
            }
        }
        return employes;
    }

    public void supprimer(String id) throws SQLException {
        String sql = "DELETE FROM employes WHERE id = ? AND is_admin = false";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    public boolean verifierIdentifiants(String email, String motDePasse) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employes WHERE email = ? AND mot_de_passe = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, motDePasse);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean verifierAdmin(String email, String motDePasse) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employes WHERE email = ? AND mot_de_passe = ? AND is_admin = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, motDePasse);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

} 