package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private final String url = "jdbc:mysql://localhost:3306/votre_base_de_donnees"; // 🛠 Replace with your DB name
    private final String user = "root"; // your MySQL user
    private final String pws = ""; // your MySQL password

    private Connection connection;
    private static MyDataBase instance;

    private MyDataBase() {
        connect(); // try initial connection
    }

    // Singleton instance
    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    // Connect method to initialize or reconnect
    private void connect() {
        try {
            connection = DriverManager.getConnection(url, user, pws);
            System.out.println("✅ Connected to the database");
        } catch (SQLException e) {
            System.err.println("❌ Connection error: " + e.getMessage());
        }
    }

    // Get connection (reconnect if closed)
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("🔁 Reconnecting to the database...");
                connect();
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to check connection: " + e.getMessage());
        }
        return connection;
    }

    // Close the connection manually if needed
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error while closing connection: " + e.getMessage());
        }
    }

    // Check if connected
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
