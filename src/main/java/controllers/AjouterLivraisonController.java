package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import models.Livraison;
import models.SocieteRecyclage;
import service.LivraisonService;
import service.SocieteRecyclageService;
import utils.EmailService;
import utils.QRCodeGenerator;

import java.io.File;
import java.time.LocalDate;

public class AjouterLivraisonController extends BaseController {

    @FXML private DatePicker dateField;
    @FXML private TextField poidsField;
    @FXML private TextField produitField;
    @FXML private ComboBox<SocieteRecyclage> societeCombo;
    @FXML private TextField emailField;

    private LivraisonService livraisonService = new LivraisonService();
    private SocieteRecyclageService societeService = new SocieteRecyclageService();

    // Update these with your actual email credentials
    private final String EMAIL_USERNAME = "your.actual.email@gmail.com"; // Replace with your actual email
    private final String EMAIL_PASSWORD = "your-actual-app-password"; // Use app password for Gmail
    private final String EMAIL_HOST = "smtp.gmail.com";
    private final int EMAIL_PORT = 587;

    @FXML
    public void initialize() {
        // Load SocieteRecyclage data into the ComboBox
        societeCombo.getItems().addAll(societeService.afficher());
    }

    @FXML
    void ajouterLivraison(ActionEvent event) {
        try {
            // Get values from the form
            LocalDate date = dateField.getValue();
            if (date == null) {
                showError("Date cannot be empty!");
                return;
            }

            float poids = 0;
            try {
                poids = Float.parseFloat(poidsField.getText());
            } catch (NumberFormatException e) {
                showError("Poids must be a valid number!");
                return;
            }

            String produit = produitField.getText();
            if (produit == null || produit.isEmpty()) {
                showError("Produit cannot be empty!");
                return;
            }

            SocieteRecyclage societe = societeCombo.getValue();
            if (societe == null) {
                showError("Please select a Societe Recyclage!");
                return;
            }

            String email = emailField.getText();
            if (email == null || email.isEmpty() || !email.contains("@")) {
                showError("Please enter a valid email address!");
                return;
            }

            // Create a new Livraison object
            Livraison livraison = new Livraison(date, poids, produit, societe);

            // Call the service to insert into the database
            boolean success = livraisonService.ajouter(livraison);

            if (success) {
                // Generate QR code
                String qrCodePath = "qrcodes/livraison_" + livraison.getId() + ".png";
                File qrDir = new File("qrcodes");
                if (!qrDir.exists()) {
                    qrDir.mkdirs();
                }

                String fullPath = QRCodeGenerator.generateQRCodeForLivraison(livraison, qrCodePath);

                if (fullPath != null) {
                    // Send email with QR code
                    EmailService emailService = new EmailService(
                            EMAIL_USERNAME,
                            EMAIL_PASSWORD,
                            EMAIL_HOST,
                            EMAIL_PORT,
                            true,
                            true);

                    String subject = "EcoRecycle - Confirmation de Livraison #" + livraison.getId();
                    String body = "Bonjour,\n\n" +
                            "Votre livraison a été enregistrée avec succès.\n\n" +
                            "Détails de la livraison:\n" +
                            "ID: " + livraison.getId() + "\n" +
                            "Produit: " + livraison.getProduit() + "\n" +
                            "Poids: " + livraison.getPoids() + " kg\n" +
                            "Date: " + livraison.getDate() + "\n" +
                            "Société de recyclage: " + livraison.getSocieteRecyclage().getNom() + "\n\n" +
                            "Veuillez trouver ci-joint le QR code de votre livraison.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe EcoRecycle";

                    boolean emailSent = emailService.sendEmailWithAttachment(
                            email,
                            subject,
                            body,
                            fullPath,
                            "livraison_qrcode.png");

                    if (emailSent) {
                        showSuccess("Livraison added successfully and email sent with QR code!");
                    } else {
                        showSuccess("Livraison added successfully but failed to send email!");
                    }
                } else {
                    showSuccess("Livraison added successfully but failed to generate QR code!");
                }

                // Reset form
                dateField.setValue(null);
                poidsField.clear();
                produitField.clear();
                societeCombo.setValue(null);
                emailField.clear();
            } else {
                showError("Failed to add livraison!");
            }

        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
            e.printStackTrace(); // Add this to see detailed error in console
        }
    }

    // Show error alert
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Show success alert
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}