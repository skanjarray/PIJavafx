package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;

public class EditUserDialogController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button saveButton;

    private User user;
    private UserService userService;
    private Stage dialogStage;
    private boolean saveClicked = false;
    private boolean usernameValid = true;
    private boolean emailValid = true;
    private boolean passwordValid = true;
    private boolean roleValid = true;

    @FXML
    private void initialize() {
        // Add validation listeners
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (usernameField.isFocused()) {
                validateUsername();
            }
        });
        
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (emailField.isFocused()) {
                validateEmail();
            }
        });
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordField.isFocused()) {
                validatePassword();
            }
        });
        
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (roleComboBox.isFocused()) {
                validateRole();
            }
        });

        // Add focus listeners to trigger validation on focus lost
        usernameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateUsername();
        });
        
        emailField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateEmail();
        });
        
        passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validatePassword();
        });
        
        roleComboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateRole();
        });

        // Initialize role combo box
        roleComboBox.setItems(FXCollections.observableArrayList("admin", "user"));
    }

    public void setUser(User user) {
        this.user = user;
        
        // Populate fields with user data
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        roleComboBox.setValue(user.getRole());
        // Don't populate password field for security reasons
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
            user.setUsername(usernameField.getText());
            user.setEmail(emailField.getText());
            user.setRole(roleComboBox.getValue());
            
            // Only update password if a new one was entered
            if (!passwordField.getText().isEmpty()) {
                user.setPassword(passwordField.getText());
            }

            try {
                userService.update(user);
                saveClicked = true;
                showSuccess("Success", "User updated successfully!");
                closeDialog();
            } catch (Exception e) {
                showError("Error", "Failed to update user", e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        dialogStage.close();
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
        if (roleComboBox.getValue() == null) {
            errorMessage += "Role is required!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showError("Invalid Fields", "Please correct invalid fields", errorMessage);
            return false;
        }
    }

    private void validateUsername() {
        usernameValid = !usernameField.getText().trim().isEmpty();
        usernameField.setStyle(usernameValid ? 
            "-fx-background-color: #f5f7fa; -fx-border-color: #e0e0e0;" :
            "-fx-background-color: #fff5f5; -fx-border-color: #e74c3c;");
        validateFields();
    }

    private void validateEmail() {
        String email = emailField.getText().trim();
        emailValid = !email.isEmpty() && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        emailField.setStyle(emailValid ? 
            "-fx-background-color: #f5f7fa; -fx-border-color: #e0e0e0;" :
            "-fx-background-color: #fff5f5; -fx-border-color: #e74c3c;");
        validateFields();
    }

    private void validatePassword() {
        String password = passwordField.getText();
        passwordValid = password.isEmpty() || password.length() >= 6;
        passwordField.setStyle(passwordValid ? 
            "-fx-background-color: #f5f7fa; -fx-border-color: #e0e0e0;" :
            "-fx-background-color: #fff5f5; -fx-border-color: #e74c3c;");
        validateFields();
    }

    private void validateRole() {
        roleValid = roleComboBox.getValue() != null;
        roleComboBox.setStyle(roleValid ? 
            "-fx-background-color: #f5f7fa; -fx-border-color: #e0e0e0;" :
            "-fx-background-color: #fff5f5; -fx-border-color: #e74c3c;");
        validateFields();
    }

    private void validateFields() {
        saveButton.setDisable(!(usernameValid && emailValid && passwordValid && roleValid));
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public boolean isSaveClicked() {
        return saveClicked;
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
            button.setStyle(currentStyle.replace("transparent", "#f5f5f5"));
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
        } else if (currentStyle.contains("#f5f5f5")) {
            // Transparent button (Cancel)
            button.setStyle(currentStyle.replace("#f5f5f5", "transparent"));
        }
    }
} 