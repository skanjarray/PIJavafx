package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.DbConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

public class ChangePasswordController {
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private User currentUser;
    private UserService userService;
    private Connection connection;

    public void initialize() {
        connection = DbConnection.getInstance().getCnx();
        userService = new UserService(connection);
        setupValidation();
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    private void setupValidation() {
        // Enable save button only when all fields are filled
        saveButton.setDisable(true);
        
        // Add listeners to all password fields
        currentPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private void validateFields() {
        boolean isValid = !currentPasswordField.getText().isEmpty() &&
                         !newPasswordField.getText().isEmpty() &&
                         !confirmPasswordField.getText().isEmpty();
        saveButton.setDisable(!isValid);
    }

    @FXML
    private void handleSave() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        try {
            // First get the fresh user data from database
            User freshUser = userService.findByUsernameOrEmail(currentUser.getUsername());
            if (freshUser == null) {
                showError("Error", "User not found");
                return;
            }

            // Verify current password
            if (!BCrypt.checkpw(currentPassword, freshUser.getPassword())) {
                showError("Error", "Current password is incorrect");
                return;
            }

            // Validate new password
            if (!newPassword.equals(confirmPassword)) {
                showError("Error", "New passwords do not match");
                return;
            }

            if (newPassword.length() < 6) {
                showError("Error", "New password must be at least 6 characters long");
                return;
            }

            // Hash the new password
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            freshUser.setPassword(hashedPassword);
            userService.update(freshUser);
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Password changed successfully");
            alert.showAndWait();

            // Close the dialog
            closeDialog();
        } catch (SQLException e) {
            showError("Error", "Failed to change password: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 