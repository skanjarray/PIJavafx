package com.esprit.ecotounsi.Repositories;

import com.esprit.ecotounsi.Models.Categorie;
import com.esprit.ecotounsi.Models.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProduitDAO {

    private List<Produit> produits;

    // Méthode pour récupérer tous les produits avec un objet Categorie, même ceux sans catégorie
    public List<Produit> getAllProduits() {
        String query = "SELECT p.id, p.nom, p.unite, p.quantite, p.favori, c.id AS categorie_id, c.nom AS categorie_nom " +
                "FROM produit p " +
                "LEFT JOIN categorie c ON p.categorie_id = c.id";  // Utilisation de LEFT JOIN pour inclure les produits sans catégorie
        List<Produit> produits = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {  // Exécute la requête et récupère le résultat

            while (rs.next()) {  // Parcourt les résultats ligne par ligne
                Produit produit = new Produit();
                produit.setId(rs.getInt("id"));  // Récupère l'ID du produit
                produit.setNom(rs.getString("nom"));  // Récupère le nom du produit
                produit.setUnite(rs.getString("unite"));  // Récupère l'unité
                produit.setQuantite(rs.getInt("quantite"));  // Récupère la quantité
                produit.setFavori(rs.getBoolean("favori")); // Récupère favori du produit

                // Vérifier si la catégorie existe (dans le cas d'un produit sans catégorie, elle sera NULL)
                if (rs.getObject("categorie_id") != null) {
                    // Créer un objet Categorie et l'assigner au produit
                    Categorie categorie = new Categorie();
                    categorie.setId(rs.getInt("categorie_id"));  // Récupère l'ID de la catégorie
                    categorie.setNom(rs.getString("categorie_nom"));  // Récupère le nom de la catégorie
                    produit.setCategorie(categorie);  // Assigne l'objet Categorie au produit
                } else {
                    produit.setCategorie(null);  // Si aucune catégorie, on l'assigne à null
                }

                produits.add(produit);  // Ajoute le produit à la liste
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produits;
    }

    // Méthode pour récupérer une catégorie par son ID
    private Categorie getCategorieById(int id) {
        Categorie categorie = null;
        String query = "SELECT * FROM categorie WHERE id = ?";  // Requête SQL pour récupérer la catégorie par ID

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);  // Set le paramètre de l'ID de catégorie
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String nom = rs.getString("nom");
                String description = rs.getString("description");
                categorie = new Categorie(id, nom, description);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la catégorie : " + e.getMessage());
            e.printStackTrace();
        }

        return categorie;
    }

    // Méthode pour obtenir toutes les catégories
    public List<Categorie> getAllCategories() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie";  // Remplace par le nom de ta table des catégories

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Utilise rs directement dans la boucle while
            while (rs.next()) {
                // Créer une nouvelle instance de Categorie à partir des données de la base de données
                int id = rs.getInt("id");
                String nom = rs.getString("nom"); // Remplace par les colonnes réelles
                String description = rs.getString("description"); // Remplace par les colonnes réelles

                Categorie categorie = new Categorie(id, nom, description);
                categories.add(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public Map<String, Integer> getCategoryProductCounts() {
        String query = "SELECT COALESCE(c.nom, 'Sans catégorie') AS categorie_nom, COUNT(p.id) AS product_count " +
                "FROM categorie c " +
                "LEFT JOIN produit p ON c.id = p.categorie_id " +
                "GROUP BY COALESCE(c.nom, 'Sans catégorie')";

        Map<String, Integer> categoryProductCounts = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                categoryProductCounts.put(rs.getString("categorie_nom"), rs.getInt("product_count"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoryProductCounts;
    }

    // Insertion dans la BD : Méthode pour ajouter un produit
    public boolean addProduit(Produit produit) {
        String query = "INSERT INTO produit (nom, unite, quantite, categorie_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, produit.getNom());
            ps.setString(2, produit.getUnite());
            ps.setInt(3, produit.getQuantite());

            // S'il y a une catégorie, on insère son ID. Sinon, on met NULL.
            if (produit.getCategorie() != null) {
                ps.setInt(4, produit.getCategorie().getId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        produit.setId(rs.getInt(1));
                    }
                }
                return true;
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
        // Requête SQL pour mettre à jour le produit
        String query = "UPDATE produit SET nom = ?, unite = ?, quantite = ?, categorie_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            // Set les valeurs du produit
            ps.setString(1, produit.getNom());       // Set le nom du produit
            ps.setString(2, produit.getUnite());     // Set l'unité du produit
            ps.setInt(3, produit.getQuantite());     // Set la quantité du produit
            // Si la catégorie existe, on met son ID, sinon on met NULL.
            if (produit.getCategorie() != null) {
                ps.setInt(4, produit.getCategorie().getId());  // Set l'ID de la catégorie
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);  // Si pas de catégorie, on met NULL
            }
            ps.setInt(5, produit.getId());  // Set l'ID du produit pour la condition WHERE

            int rowsAffected = ps.executeUpdate();  // Exécution de la mise à jour
            return rowsAffected > 0;  // Retourne true si au moins une ligne a été mise à jour

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du produit : " + e.getMessage());
            return false;
        }
    }

    // Mettre à jour l'état "favori" d'un produit
    public void updateFavori(int idProduit, boolean estFavori) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE produit SET favori = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, estFavori);
            stmt.setInt(2, idProduit);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
