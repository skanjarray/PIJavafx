package com.esprit.ecotounsi.Controllers;

import com.esprit.ecotounsi.EcotounsiApplication;
import com.esprit.ecotounsi.Repositories.ProduitDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public class CircularPercentageController implements Initializable {

    @FXML private Arc backgroundArc, percentageArc;
    @FXML private Text percentageText, annotationText;
    @FXML private VBox legend, buttonBox;

    private ProduitDAO productDAO;

    public CircularPercentageController() {
        productDAO = new ProduitDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCategoryStatistics();
        VBox.setMargin(buttonBox, new Insets(10));
    }

    // Mettre à jour les statistiques des catégories
    public void setCategoryStatistics() {
        Map<String, Integer> categoryProductCounts = productDAO.getCategoryProductCounts();
        int totalProducts = categoryProductCounts.values().stream().mapToInt(Integer::intValue).sum();
        updateLegendAndArc(categoryProductCounts, totalProducts);
    }

    // Méthode pour mettre à jour l'arc et la légende
    private void updateLegendAndArc(Map<String, Integer> categoryProductCounts, int totalProducts) {
        double startAngle = 90;
        VBox legendBox = (VBox) legend.getChildren().get(0);
        legendBox.getChildren().clear();

        for (Map.Entry<String, Integer> entry : categoryProductCounts.entrySet()) {
            String category = entry.getKey();
            int productCount = entry.getValue();
            double percentage = (double) productCount / totalProducts * 100;
            double angle = (percentage / 100.0) * 360;

            updateArc(angle);
            updateAnnotation(category, percentage, startAngle, angle);
            updateLegendBox(legendBox, category, percentage);

            startAngle += angle;
        }
    }

    // Mettre à jour l'arc principal
    private void updateArc(double angle) {
        percentageArc.setLength(angle);
    }

    // Mettre à jour l'annotation
    private void updateAnnotation(String category, double percentage, double startAngle, double angle) {
        double annotationX = 200 + 120 * Math.cos(Math.toRadians(startAngle + angle / 2));
        double annotationY = 200 + 120 * Math.sin(Math.toRadians(startAngle + angle / 2));
        annotationText.setText(String.format("%s: %.0f%%", category, percentage));
        annotationText.setX(annotationX);
        annotationText.setY(annotationY);
        annotationText.setVisible(true);
    }

    // Mettre à jour la légende
    private void updateLegendBox(VBox legendBox, String category, double percentage) {
        Text legendItem = new Text(String.format("%s: %.0f%%", category, percentage));
        legendBox.getChildren().add(legendItem);
    }

    // Navigation vers la gestion des catégories
    @FXML
    private void allerVersCategorie() {
        navigateToScene("/com/esprit/ecotounsi/categorie.fxml");
    }

    // Navigation vers la gestion des produits
    @FXML
    private void allerVersProduits(ActionEvent event) {
        try {
            Stage stage = (Stage) backgroundArc.getScene().getWindow();
            new EcotounsiApplication().start(stage);
        } catch (Exception e) {
            showError("Erreur lors du retour à l'écran principal : " + e.getMessage());
        }
    }

    // Méthode générique pour la navigation
    private void navigateToScene(String fxmlPath) {
        try {
            Stage stage = (Stage) backgroundArc.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            showError("Erreur lors du chargement de l'interface : " + ex.getMessage());
        }
    }

    // Afficher un message d'erreur à l'utilisateur
    private void showError(String message) {
        System.err.println(message);
    }
}
