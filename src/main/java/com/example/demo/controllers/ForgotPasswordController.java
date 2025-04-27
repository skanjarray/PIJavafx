package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.service.EmailService;
import com.example.demo.utils.DbConnection;
import com.example.demo.utils.ConfigLoader;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.mail.MessagingException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

public class ForgotPasswordController {
    @FXML private TextField emailField;
    @FXML private TextField otpField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button sendOtpButton;
    @FXML private Button verifyOtpButton;
    @FXML private Button resetPasswordButton;
    @FXML private Button closeButton;
    @FXML private Label statusLabel;
    @FXML private VBox emailStep;
    @FXML private VBox otpStep;
    @FXML private VBox newPasswordStep;

    private UserService userService;
    private EmailService emailService;
    private User currentUser;
    private String generatedOtp;

    @FXML
    public void initialize() {
        Connection connection = DbConnection.getInstance().getCnx();
        userService = new UserService(connection);
        
        // Initialize email service with credentials from config
        emailService = new EmailService(
            ConfigLoader.getEmailUsername(),
            ConfigLoader.getEmailPassword()
        );
    }

    @FXML
    private void handleSendOtp() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            showError("Please enter your email address");
            return;
        }

        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                showError("No account found with this email address");
                return;
            }

            // Generate and store OTP
            generatedOtp = generateOtp();
            currentUser = user;
            
            // Send OTP via email
            try {
                emailService.sendOtpEmail(email, generatedOtp);
                showSuccess("OTP has been sent to your email address");
            } catch (MessagingException e) {
                showError("Failed to send OTP: " + e.getMessage());
                return;
            }
            
            // Update user's OTP in database
            user.setOtp(generatedOtp);
            userService.update(user);
            
            // Show OTP step
            emailStep.setVisible(false);
            otpStep.setVisible(true);
            
        } catch (SQLException e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleVerifyOtp() {
        String enteredOtp = otpField.getText().trim();
        
        if (enteredOtp.isEmpty()) {
            showError("Please enter the OTP");
            return;
        }

        if (!enteredOtp.equals(generatedOtp)) {
            showError("Invalid OTP");
            return;
        }

        // Show new password step
        otpStep.setVisible(false);
        newPasswordStep.setVisible(true);
    }

    @FXML
    private void handleResetPassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please enter and confirm your new password");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (newPassword.length() < 6) {
            showError("Password must be at least 6 characters long");
            return;
        }

        try {
            // Hash the new password
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            currentUser.setPassword(hashedPassword);
            currentUser.setOtp(null); // Clear the OTP
            userService.update(currentUser);
            
            // Send confirmation email
            try {
                emailService.sendPasswordResetConfirmation(currentUser.getEmail());
            } catch (MessagingException e) {
                System.err.println("Failed to send confirmation email: " + e.getMessage());
            }
            
            showSuccess("Password reset successful");
            handleClose();
            
        } catch (SQLException e) {
            showError("Error resetting password: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusLabel.setText(message);
    }

    private void showSuccess(String message) {
        statusLabel.setStyle("-fx-text-fill: #2ecc71;");
        statusLabel.setText(message);
    }
} 