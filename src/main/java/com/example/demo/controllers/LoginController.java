package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.service.HCaptchaService;
import com.example.demo.utils.DbConnection;
import com.example.demo.utils.WebServer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;
    @FXML private WebView captchaWebView;

    private UserService userService;
    private HCaptchaService hCaptchaService;
    private String captchaResponse;

    @FXML
    public void initialize() {
        // Initialize database connection
        Connection connection = DbConnection.getInstance().getCnx();
        userService = new UserService(connection);
        hCaptchaService = new HCaptchaService();

        // Initialize hCaptcha
        initializeHCaptcha();

        // Add hover effects to buttons
        setupButtonHoverEffects();
    }

    private void initializeHCaptcha() {
        try {
            // Start the web server
            WebServer.start();
            
            // Load the CAPTCHA from the web server
            String captchaUrl = WebServer.getCaptchaUrl();
            System.out.println("Loading hCaptcha from: " + captchaUrl);
            
            captchaWebView.getEngine().load(captchaUrl);
            captchaWebView.getEngine().setJavaScriptEnabled(true);
            
            // Set user agent to mimic a modern browser
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
            captchaWebView.getEngine().setUserAgent(userAgent);
            System.out.println("Set user agent: " + userAgent);
            
            // Add console logging for debugging
            captchaWebView.getEngine().setOnAlert(event -> {
                System.out.println("Alert: " + event.getData());
            });
            
            captchaWebView.getEngine().setOnError(event -> {
                System.out.println("Error: " + event.getMessage());
                System.out.println("Error type: " + event.getEventType());
                System.out.println("Error source: " + event.getSource());
            });
            
            captchaWebView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldVal, newVal) -> {
                System.out.println("Load state changed: " + oldVal + " -> " + newVal);
                if (newVal == javafx.concurrent.Worker.State.SUCCEEDED) {
                    System.out.println("Page loaded successfully");
                    // Execute debug script
                    String debugScript = 
                        "window.java = {" +
                        "    onHCaptchaComplete: function(response) {" +
                        "        console.log('Java callback called with: ' + response);" +
                        "        window.java.onHCaptchaComplete(response);" +
                        "    }" +
                        "};" +
                        "console.log('Java object initialized');";
                    
                    captchaWebView.getEngine().executeScript(debugScript);
                    
                    // Check for errors
                    if (captchaWebView.getEngine().getLoadWorker().getException() != null) {
                        System.out.println("Error during script execution: " + 
                            captchaWebView.getEngine().getLoadWorker().getException().getMessage());
                    }
                } else if (newVal == javafx.concurrent.Worker.State.FAILED) {
                    System.out.println("Page load failed");
                    System.out.println("Error: " + captchaWebView.getEngine().getLoadWorker().getException());
                }
            });

        } catch (Exception e) {
            System.err.println("Error initializing hCaptcha: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onHCaptchaComplete(String response) {
        System.out.println("Java received hCaptcha response: " + response);
        if (response != null && !response.isEmpty()) {
            this.captchaResponse = response;
            System.out.println("CAPTCHA response stored: " + this.captchaResponse);
            
            // Verify the response is stored
            String storedResponse = (String) captchaWebView.getEngine().executeScript("window.getCaptchaResponse()");
            System.out.println("Stored response in JavaScript: " + storedResponse);
        } else {
            System.out.println("Received empty CAPTCHA response");
        }
    }

    private void setupButtonHoverEffects() {
        // Login button hover effect
        loginButton.setOnMouseEntered(e -> 
            loginButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 20 12 20; -fx-background-radius: 5; -fx-cursor: hand;")
        );
        loginButton.setOnMouseExited(e -> 
            loginButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 20 12 20; -fx-background-radius: 5; -fx-cursor: hand;")
        );
    }

    @FXML
    private void handleLogin() {
        String usernameOrEmail = usernameField.getText().trim();
        String password = passwordField.getText();

        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            showError("Please enter both username/email and password");
            return;
        }

        // Check if CAPTCHA is completed
        if (captchaResponse == null || captchaResponse.isEmpty()) {
            System.out.println("CAPTCHA not completed. Current response: " + captchaResponse);
            // Try to get the response from JavaScript
            String jsResponse = (String) captchaWebView.getEngine().executeScript("window.getCaptchaResponse()");
            System.out.println("Response from JavaScript: " + jsResponse);
            if (jsResponse != null && !jsResponse.isEmpty()) {
                captchaResponse = jsResponse;
                System.out.println("Retrieved response from JavaScript: " + captchaResponse);
            } else {
                showError("Please complete the security check");
                return;
            }
        }

        System.out.println("Verifying CAPTCHA response: " + captchaResponse);
        if (!hCaptchaService.verifyCaptcha(captchaResponse)) {
            showError("Security check failed. Please try again.");
            return;
        }

        try {
            User user = userService.verifyUserCredentials(usernameOrEmail, password);
            if (user != null) {
                // Role-based navigation
                if ("ROLE_ADMIN".equals(user.getRole())) {
                    showAdminDashboard(user);
                } else if ("ROLE_CLIENT".equals(user.getRole())) {
                    showClientDashboard(user);
                } else {
                    showError("Invalid user role: " + user.getRole());
                }
            } else {
                showError("Invalid username/email or password");
            }
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            showError("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
            showError("An unexpected error occurred. Please try again.");
        }
    }

    private void showAdminDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/addUser.fxml"));
            Parent root = loader.load();
            
            AddUserController controller = loader.getController();
            controller.initialize(DbConnection.getInstance().getCnx());
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            showError("Could not load admin dashboard: " + e.getMessage());
        }
    }

    private void showClientDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/clientDashboard.fxml"));
            Parent root = loader.load();
            
            ClientDashboardController controller = loader.getController();
            controller.initialize();
            controller.setUser(user);
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Client Dashboard");
        } catch (IOException e) {
            System.err.println("Error loading client dashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Could not load client dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/signup.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign Up");
        } catch (IOException e) {
            showError("Could not load signup form: " + e.getMessage());
        }
    }

    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/forgotPasswordDialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Forgot Password");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            showError("Could not load forgot password dialog: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
    }

    // Add cleanup method
    public void cleanup() {
        WebServer.stop();
    }
} 