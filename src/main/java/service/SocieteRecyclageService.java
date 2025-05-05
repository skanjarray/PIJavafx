package service;

import models.SocieteRecyclage;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocieteRecyclageService {

    // Add SocieteRecyclage to the database
    public boolean ajouter(SocieteRecyclage s) {
        String query = "INSERT INTO societe_recyclage (nom, adresse, email) VALUES (?, ?, ?)";

        try (Connection con = MyDataBase.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, s.getNom());
            pstmt.setString(2, s.getAdresse());
            pstmt.setString(3, s.getEmail());

            pstmt.executeUpdate();

            // Get the generated ID after insertion
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    s.setId(rs.getInt(1));  // Set the generated ID
                }
            }

            return true;  // Successfully added

        } catch (SQLException e) {
            System.err.println("❌ Error while adding Societe: " + e.getMessage());
            return false;  // Failed to add
        }
    }

    // Modify an existing SocieteRecyclage
    public void modifier(SocieteRecyclage s) {
        String query = "UPDATE societe_recyclage SET nom = ?, adresse = ?, email = ? WHERE id = ?";

        try (Connection con = MyDataBase.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, s.getNom());
            pstmt.setString(2, s.getAdresse());
            pstmt.setString(3, s.getEmail());
            pstmt.setInt(4, s.getId());

            pstmt.executeUpdate();
            System.out.println("✔ Societe updated successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Error while updating Societe: " + e.getMessage());
        }
    }

    // Delete a SocieteRecyclage by its ID
    public void supprimer(int id) {
        String query = "DELETE FROM societe_recyclage WHERE id = ?";

        try (Connection con = MyDataBase.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("✔ Societe deleted successfully!");

        } catch (SQLException e) {
            System.err.println("❌ Error while deleting Societe: " + e.getMessage());
        }
    }

    // Fetch all SocieteRecyclage objects from the database
    public List<SocieteRecyclage> afficher() {
        List<SocieteRecyclage> societes = new ArrayList<>();
        String query = "SELECT * FROM `societe_recyclage` ";

        try
                (
                        Connection con = MyDataBase.getInstance().getConnection();
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String adresse = rs.getString("adresse");
                String email = rs.getString("email");

                SocieteRecyclage societe = new SocieteRecyclage(id, nom, adresse, email);
                System.out.println(societes);
                societes.add(societe);

            }


        }
        catch (SQLException e) {
            System.err.println("❌ Error while fetching SocieteRecyclage: " + e.getMessage());
        }
        System.out.println("list         return societes;\n"+societes);
        return societes;
    }
}