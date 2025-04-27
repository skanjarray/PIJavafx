package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class ClientDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button editProfileButton;
    @FXML private Button changePasswordButton;
    @FXML private Button logoutButton;
    @FXML private Button dashboardButton;
    @FXML private Button profileButton;
    @FXML private Button passwordButton;

    private User currentUser;
    private SessionManager sessionManager;

    @FXML
    public void initialize() {
        sessionManager = SessionManager.getInstance();
        
        // Set up button hover effects
        setupButtonHoverEffects();
        
        // Set up button actions
        editProfileButton.setOnAction(event -> handleEditProfile());
        changePasswordButton.setOnAction(event -> handleChangePassword());
        logoutButton.setOnAction(event -> handleLogout());
        dashboardButton.setOnAction(event -> handleDashboard());
        profileButton.setOnAction(event -> handleEditProfile());
        passwordButton.setOnAction(event -> handleChangePassword());
    }

    private void setupButtonHoverEffects() {
        // Sidebar buttons hover effect
        String hoverStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;";
        String normalStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;";
        
        dashboardButton.setOnMouseEntered(e -> dashboardButton.setStyle(hoverStyle));
        dashboardButton.setOnMouseExited(e -> dashboardButton.setStyle(normalStyle));
        
        profileButton.setOnMouseEntered(e -> profileButton.setStyle(hoverStyle));
        profileButton.setOnMouseExited(e -> profileButton.setStyle(normalStyle));
        
        passwordButton.setOnMouseEntered(e -> passwordButton.setStyle(hoverStyle));
        passwordButton.setOnMouseExited(e -> passwordButton.setStyle(normalStyle));
        
        // Logout button hover effect
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;"));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;"));
    }

    public void setUser(User user) {
        this.currentUser = user;
        updateUserInfo();
    }

    private void updateUserInfo() {
        if (currentUser != null) {
            // Update welcome message
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
            
            // Update user information
            usernameLabel.setText(currentUser.getUsername());
            emailLabel.setText(currentUser.getEmail());
            roleLabel.setText(currentUser.getRole());
            
            // Update sidebar user info
            userNameLabel.setText(currentUser.getUsername());
            userRoleLabel.setText(currentUser.getRole());
        }
    }

    @FXML
    private void handleDashboard() {
        // Refresh the dashboard view
        updateUserInfo();
    }

    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/editUserDialog.fxml"));
            Parent root = loader.load();
            
            EditUserDialogController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh user info after editing
            updateUserInfo();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to open edit profile dialog");
        }
    }

    @FXML
    private void handleChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/changePasswordDialog.fxml"));
            Parent root = loader.load();
            
            ChangePasswordController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage stage = new Stage();
            stage.setTitle("Change Password");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to open change password dialog");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Clear the session
            sessionManager.clearSession();
            
            // Close current window
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();
            
            // Show login window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/login.fxml"));
            Parent root = loader.load();
            
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to logout");
        }
    }

    private void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 