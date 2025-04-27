package com.example.demo.service;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;

import java.util.Random;

public class CaptchaService {
    private String currentCaptcha;
    private final Random random = new Random();

    public VBox createCaptchaWidget() {
        VBox captchaBox = new VBox(10);
        captchaBox.setAlignment(Pos.CENTER);
        
        // Generate and display CAPTCHA
        Label captchaLabel = new Label();
        captchaLabel.setFont(Font.font("Courier New", 24));
        captchaLabel.setTextFill(Color.BLUE);
        
        TextField captchaInput = new TextField();
        captchaInput.setPromptText("Enter the code shown above");
        captchaInput.setMaxWidth(200);
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        // Generate initial CAPTCHA
        generateNewCaptcha(captchaLabel);
        
        // Add refresh functionality
        refreshButton.setOnAction(e -> generateNewCaptcha(captchaLabel));
        
        captchaBox.getChildren().addAll(captchaLabel, captchaInput, refreshButton);
        return captchaBox;
    }
    
    private void generateNewCaptcha(Label label) {
        // Generate a random 6-character CAPTCHA
        StringBuilder captcha = new StringBuilder();
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        for (int i = 0; i < 6; i++) {
            captcha.append(chars.charAt(random.nextInt(chars.length())));
        }
        currentCaptcha = captcha.toString();
        label.setText(currentCaptcha);
    }
    
    public boolean verifyCaptcha(String input) {
        return input != null && input.equalsIgnoreCase(currentCaptcha);
    }
} 