package com.esprit.ecotounsi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    // Méthode pour récupérer tous les produits
    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit";  // Requête SQL pour récupérer tous les produits

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String unite = rs.getString("unite");
                int quantite = rs.getInt("quantite");
                produits.add(new Produit(id, nom, unite, quantite));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des produits : " + e.getMessage());
        }

        return produits;
    }

    // Méthode pour ajouter un produit
    public boolean addProduit(Produit produit) {
        String query = "INSERT INTO produit (nom, unite, quantite) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            System.out.println("Connexion établie avec succès.");
            ps.setString(1, produit.getNom());  // Set nom du produit
            ps.setString(2, produit.getUnite());  // Set unité du produit
            ps.setInt(3, produit.getQuantite());  // Set quantité du produit

            // Exécuter la requête d'insertion
            int rowsAffected = ps.executeUpdate();
            System.out.println("Nombre de lignes affectées : " + rowsAffected);

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        produit.setId(rs.getInt(1));  // Récupérer l'ID généré
                    }
                }
                return true;
            } else {
                System.out.println("Aucune ligne insérée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du produit : " + e.getMessage());
        }
        return false;
    }

    // Méthode pour supprimer un produit par son ID
    public boolean deleteProduit(int id) {
        String query = "DELETE FROM produit WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du produit : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour mettre à jour un produit dans la base de données
    public boolean updateProduit(Produit produit) {
        String query = "UPDATE produit SET nom = ?, unite = ?, quantite = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, produit.getNom());      // Set le nom du produit
            ps.setString(2, produit.getUnite());    // Set l'unité du produit
            ps.setInt(3, produit.getQuantite());    // Set la quantité du produit
            ps.setInt(4, produit.getId());          // Set l'ID du produit pour la condition WHERE

            int rowsAffected = ps.executeUpdate();  // Exécution de la mise à jour
            return rowsAffected > 0;                // Retourne true si au moins une ligne a été mise à jour

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du produit : " + e.getMessage());
            return false;
        }
    }
}
