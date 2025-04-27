package com.example.demo.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    private final String username;
    private final String password;
    private final Properties props;
    private Session session;

    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;
        
        // Configure email properties
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.debug", "true");
        
        // Create session
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void sendOtpEmail(String recipientEmail, String otp) throws MessagingException {
        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Password Reset OTP");
            
            // Email content
            String emailContent = String.format(
                "Dear User,\n\n" +
                "You have requested to reset your password. Please use the following OTP to proceed:\n\n" +
                "OTP: %s\n\n" +
                "This OTP is valid for 10 minutes.\n\n" +
                "If you did not request this password reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Your Application Team", otp
            );
            
            message.setText(emailContent);

            // Send email
            Transport.send(message);
            LOGGER.info("OTP email sent successfully to: " + recipientEmail);
            
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send OTP email", e);
            throw e;
        }
    }

    public void sendPasswordResetConfirmation(String recipientEmail) throws MessagingException {
        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Password Reset Successful");
            
            // Email content
            String emailContent = 
                "Dear User,\n\n" +
                "Your password has been successfully reset.\n\n" +
                "If you did not make this change, please contact support immediately.\n\n" +
                "Best regards,\n" +
                "Your Application Team";
            
            message.setText(emailContent);

            // Send email
            Transport.send(message);
            LOGGER.info("Password reset confirmation email sent successfully to: " + recipientEmail);
            
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send password reset confirmation email", e);
            throw e;
        }
    }

    public void sendWelcomeEmail(String recipientEmail, String username) throws MessagingException {
        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Welcome to Our Application");
            
            // Email content
            String emailContent = String.format(
                "Dear %s,\n\n" +
                "Welcome to our application! We're excited to have you on board.\n\n" +
                "Your account has been successfully created. You can now log in using your credentials.\n\n" +
                "If you have any questions or need assistance, please don't hesitate to contact us.\n\n" +
                "Best regards,\n" +
                "Your Application Team", username
            );
            
            message.setText(emailContent);

            // Send email
            Transport.send(message);
            LOGGER.info("Welcome email sent successfully to: " + recipientEmail);
            
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send welcome email", e);
            throw e;
        }
    }

    public void sendAccountDeletionConfirmation(String recipientEmail, String username) throws MessagingException {
        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Account Deletion Confirmation");
            
            // Email content
            String emailContent = String.format(
                "Dear %s,\n\n" +
                "Your account has been successfully deleted from our system.\n\n" +
                "We're sorry to see you go. If you change your mind, you can always create a new account.\n\n" +
                "Best regards,\n" +
                "Your Application Team", username
            );
            
            message.setText(emailContent);

            // Send email
            Transport.send(message);
            LOGGER.info("Account deletion confirmation email sent successfully to: " + recipientEmail);
            
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send account deletion confirmation email", e);
            throw e;
        }
    }
} 