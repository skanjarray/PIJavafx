package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import models.Livraison;
import models.SocieteRecyclage;
import service.LivraisonService;
import service.SocieteRecyclageService;

import java.time.LocalDate;

public class ModifierLivraisonController {

    @FXML private TextField idField;
    @FXML private DatePicker dateField;
    @FXML private TextField poidsField;
    @FXML private TextField produitField;
    @FXML private ComboBox<SocieteRecyclage> societeCombo;

    private final LivraisonService livraisonService = new LivraisonService();
    private final SocieteRecyclageService societeService = new SocieteRecyclageService();

    @FXML
    public void initialize() {
        societeCombo.getItems().addAll(societeService.afficher());
    }

    @FXML
    public void modifierLivraison(ActionEvent event) {
        try {
            int id = Integer.parseInt(idField.getText());
            LocalDate date = dateField.getValue();
            float poids = Float.parseFloat(poidsField.getText());
            String produit = produitField.getText();
            SocieteRecyclage societe = societeCombo.getValue();

            Livraison l = new Livraison(id, date, poids, produit, societe);
            livraisonService.modifier(l);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Livraison modifiée avec succès !");
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la modification : " + e.getMessage());
            alert.showAndWait();
        }
    }
}
