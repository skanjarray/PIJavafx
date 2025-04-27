package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

/**
 * Base controller class that provides common navigation methods and utilities
 * for all controllers in the application.
 * All other controllers should extend this class.
 */
public class BaseController {

    /**
     * Navigate to the Home screen
     */
    @FXML
    public void goToHome(ActionEvent event) {
        loadScene("Home.fxml", event);
    }

    @FXML
    public void goToAjouterLivraison(ActionEvent event) {
        loadScene("AjouterLivraison.fxml", event);
    }

    @FXML
    public void goToAfficherLivraisons(ActionEvent event) {
        loadScene("AfficherLivraisons.fxml", event);
    }

    @FXML
    public void goToModifierLivraison(ActionEvent event) {
        loadScene("ModifierLivraison.fxml", event);
    }

    @FXML
    public void goToSupprimerLivraison(ActionEvent event) {
        loadScene("SupprimerLivraison.fxml", event);
    }

    @FXML
    public void goToAjouterSocieteRecyclage(ActionEvent event) {
        loadScene("AjouterSociete.fxml", event);  // Changed to match your file name
    }

    @FXML
    public void goToAfficherSocietesRecyclage(ActionEvent event) {
        loadScene("AfficherSocietes.fxml", event);
    }

    @FXML
    public void goToModifierSocieteRecyclage(ActionEvent event) {
        loadScene("ModifierSocieteRecyclage.fxml", event);
    }

    @FXML
    public void goToSupprimerSocieteRecyclage(ActionEvent event) {
        loadScene("SupprimerSocieteRecyclage.fxml", event);
    }

    @FXML
    public void goToChatbot(ActionEvent event) {
        loadScene("Chatbot.fxml", event);
    }

    /**
     * Utility method to load a scene from an FXML file
     */
    protected void loadScene(String fxmlFile, ActionEvent event) {
        try {
            // Try different ways to load the resource
            URL resourceUrl = null;

            // Method 1: Try with class loader
            resourceUrl = getClass().getClassLoader().getResource(fxmlFile);
            System.out.println("Method 1 URL: " + resourceUrl);

            // Method 2: Try with direct path
            if (resourceUrl == null) {
                resourceUrl = getClass().getResource("/" + fxmlFile);
                System.out.println("Method 2 URL: " + resourceUrl);
            }

            // Method 3: Try without leading slash
            if (resourceUrl == null) {
                resourceUrl = getClass().getResource(fxmlFile);
                System.out.println("Method 3 URL: " + resourceUrl);
            }

            // If we found the URL, use it
            if (resourceUrl != null) {
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else {
                // If all methods fail, try one more approach with direct stream
                try {
                    FXMLLoader loader = new FXMLLoader();
                    Parent root = loader.load(getClass().getClassLoader().getResourceAsStream(fxmlFile));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                    return;
                } catch (Exception e) {
                    System.err.println("Failed to load with stream: " + e.getMessage());
                    throw new IOException("FXML file not found: " + fxmlFile);
                }
            }
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Could not load the requested page: " + fxmlFile, e.getMessage());
            e.printStackTrace();

            // Print debugging information
            System.out.println("Current working directory: " + System.getProperty("user.dir"));
            System.out.println("Trying to load: " + fxmlFile);
        }
    }

    protected void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected void showInfoAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected boolean showConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait().get().getButtonData().isDefaultButton();
    }
}