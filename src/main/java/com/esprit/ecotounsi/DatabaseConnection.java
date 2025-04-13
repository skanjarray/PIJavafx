package com.esprit.ecotounsi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection conn = null;

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) { // Vérifie si la connexion est déjà fermée
            // URL de connexion, utilisateur et mot de passe
            String url = "jdbc:mysql://localhost:3306/esprit?useSSL=false&serverTimezone=UTC";; // Remplace par l'URL de ta base
            String user = "root"; // Remplace par ton utilisateur
            String pwd = ""; // Remplace par ton mot de passe

            try {
                // Charger le driver JDBC si nécessaire (pas toujours requis avec les versions modernes)
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Etablissement de la connexion
                conn = DriverManager.getConnection(url, user, pwd);
                System.out.println("Connexion réussie !");
            } catch (SQLException e) {
                System.out.println("Erreur de connexion : " + e.getMessage());
                throw e; // Rejeter l'exception pour qu'elle soit gérée plus haut
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return conn;
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Connexion fermée.");
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}

