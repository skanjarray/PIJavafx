package com.esprit.ecotounsi.Repositories;

import com.esprit.ecotounsi.Models.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieDAO {

    // Récupérer toutes les catégories
    public List<Categorie> getAllCategories() {
        List<Categorie> categories = new ArrayList<>();
        String query = "SELECT * FROM categorie";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String description = rs.getString("description");
                categories.add(new Categorie(id, nom, description));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des catégories : " + e.getMessage());
        }

        return categories;
    }

    // Insertion dans la BD : Ajouter une catégorie
    public boolean addCategorie(Categorie categorie) {
        String query = "INSERT INTO categorie (nom, description) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, categorie.getNom());
            ps.setString(2, categorie.getDescription());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        categorie.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la catégorie : " + e.getMessage());
        }

        return false;
    }

    // Supprimer une catégorie par ID
    public boolean deleteCategorie(int id) {
        String query = "DELETE FROM categorie WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la catégorie : " + e.getMessage());
            return false;
        }
    }

    // Mettre à jour une catégorie
    public boolean updateCategorie(Categorie categorie) {
        String query = "UPDATE categorie SET nom = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, categorie.getNom());
            ps.setString(2, categorie.getDescription());
            ps.setInt(3, categorie.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la catégorie : " + e.getMessage());
            return false;
        }
    }
}
