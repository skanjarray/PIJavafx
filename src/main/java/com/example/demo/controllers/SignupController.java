package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.DbConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class SignupController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button signupButton;
    @FXML private Label statusLabel;

    private UserService userService;

    @FXML
    public void initialize() {
        // Initialize database connection
        Connection connection = DbConnection.getInstance().getCnx();
        userService = new UserService(connection);

        // Remove role selection since all new users will be clients
        roleComboBox.setVisible(false);
        roleComboBox.setManaged(false);
    }

    @FXML
    private void handleSignUp() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        // Set role to ROLE_CLIENT by default
        String role = "ROLE_CLIENT";

        // Validate input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid email format");
            return;
        }

        try {
            // Check if username or email already exists
            if (userService.findByUsernameOrEmail(username) != null) {
                showError("Username already exists");
                return;
            }
            if (userService.findByUsernameOrEmail(email) != null) {
                showError("Email already exists");
                return;
            }

            // Create new user with plain password
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPlainPassword(password); // This will hash the password
            newUser.setRole(role);
            userService.create(newUser);

            // Show success message and go to login
            showSuccess("Account created successfully! Please sign in.");
            handleLogin();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign In");
        } catch (IOException e) {
            showError("Could not load login form: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 