package service;

import models.Livraison;
import models.SocieteRecyclage;
import utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LivraisonService implements IService<Livraison> {

    @Override
    public boolean ajouter(Livraison l) {
        String query = "INSERT INTO livraison (date, poids, produit, societe_recyclage_id) VALUES (?, ?, ?, ?)";

        try (Connection con = MyDataBase.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(l.getDate()));  // Convert LocalDate to SQL Date
            stmt.setFloat(2, l.getPoids());
            stmt.setString(3, l.getProduit());
            stmt.setInt(4, l.getSocieteRecyclage().getId()); // Assuming SocieteRecyclage has an ID field

            int rowsAffected = stmt.executeUpdate();

            // Get the generated ID
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        l.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✔ Livraison added successfully with ID: " + l.getId());
                return true;
            } else {
                System.err.println("❌ Failed to add livraison.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void modifier(Livraison l) {
        String query = "UPDATE livraison SET date = ?, poids = ?, produit = ?, societe_recyclage_id = ? WHERE id = ?";

        try (Connection con = MyDataBase.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(l.getDate()));
            stmt.setFloat(2, l.getPoids());
            stmt.setString(3, l.getProduit());
            stmt.setInt(4, l.getSocieteRecyclage().getId());
            stmt.setInt(5, l.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✔ Livraison updated successfully.");
            } else {
                System.err.println("❌ Failed to update livraison.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String query = "DELETE FROM livraison WHERE id = ?";

        try (Connection con = MyDataBase.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✔ Livraison deleted successfully.");
            } else {
                System.err.println("❌ Failed to delete livraison.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
    }

    @Override
    public List<Livraison> afficher() {
        List<Livraison> livraisons = new ArrayList<>();

        String query = "SELECT l.id, l.date, l.poids, l.produit, " +
                "r.id as societe_id, r.nom as societe_nom, r.adresse as societe_adresse, r.email as societe_email " +
                "FROM livraison l JOIN societe_recyclage r ON l.societe_recyclage_id = r.id";

        try (Connection con = MyDataBase.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                LocalDate date = rs.getDate("date").toLocalDate();
                float poids = rs.getFloat("poids");
                String produit = rs.getString("produit");

                // Get complete societe information
                int societeId = rs.getInt("societe_id");
                String societeNom = rs.getString("societe_nom");
                String societeAdresse = rs.getString("societe_adresse");
                String societeEmail = rs.getString("societe_email");

                SocieteRecyclage sr = new SocieteRecyclage(societeId, societeNom, societeAdresse, societeEmail);
                Livraison l = new Livraison(id, date, poids, produit, sr);
                livraisons.add(l);
            }

            System.out.println("✔ Livraisons fetched: " + livraisons.size());

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }

        return livraisons;
    }
}