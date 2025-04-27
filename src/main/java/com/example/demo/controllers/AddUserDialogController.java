package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;

public class AddUserDialogController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;

    private UserService userService;
    private Stage dialogStage;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        // Initialize role options
        roleComboBox.getItems().addAll("ROLE_CLIENT", "ROLE_ADMIN");
        roleComboBox.setValue("ROLE_CLIENT");
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            try {
                User newUser = new User(
                    usernameField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    roleComboBox.getValue()
                );
                
                userService.create(newUser);
                saveClicked = true;
                dialogStage.close();
            } catch (SQLException e) {
                showAlert("Error", "Failed to create user: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    @FXML
    public void handleButtonHover(MouseEvent event) {
        Button button = (Button) event.getSource();
        String currentStyle = button.getStyle();
        
        // Determine the appropriate hover color based on the button's current background color
        if (currentStyle.contains("#2ecc71")) {
            // Green button (Save)
            button.setStyle(currentStyle.replace("#2ecc71", "#27ae60"));
        } else if (currentStyle.contains("transparent")) {
            // Transparent button (Cancel)
            button.setStyle(currentStyle.replace("transparent", "#f5f7fa"));
        }
    }
    
    @FXML
    public void handleButtonExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        String currentStyle = button.getStyle();
        
        // Restore the original color based on the button's purpose
        if (currentStyle.contains("#27ae60")) {
            // Green button (Save)
            button.setStyle(currentStyle.replace("#27ae60", "#2ecc71"));
        } else if (currentStyle.contains("#f5f7fa")) {
            // Transparent button (Cancel)
            button.setStyle(currentStyle.replace("#f5f7fa", "transparent"));
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (usernameField.getText() == null || usernameField.getText().trim().isEmpty()) {
            errorMessage += "Username is required!\n";
        }
        if (emailField.getText() == null || emailField.getText().trim().isEmpty()) {
            errorMessage += "Email is required!\n";
        } else if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorMessage += "Invalid email format!\n";
        }
        if (passwordField.getText() == null || passwordField.getText().trim().isEmpty()) {
            errorMessage += "Password is required!\n";
        } else if (passwordField.getText().length() < 6) {
            errorMessage += "Password must be at least 6 characters!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showAlert("Validation Error", errorMessage, Alert.AlertType.ERROR);
            return false;
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 