package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import models.Livraison;
import models.SocieteRecyclage;
import service.LivraisonService;
import service.SocieteRecyclageService;

import java.time.LocalDate;

public class AjouterLivraisonController {

    @FXML private DatePicker dateField;
    @FXML private TextField poidsField;
    @FXML private TextField produitField;
    @FXML private ComboBox<SocieteRecyclage> societeCombo;

    private LivraisonService livraisonService = new LivraisonService();
    private SocieteRecyclageService societeService = new SocieteRecyclageService();

    @FXML
    public void initialize() {
        societeCombo.getItems().addAll(societeService.afficher());
    }

    @FXML
    void ajouterLivraison(ActionEvent event) {
        LocalDate date = dateField.getValue();
        float poids = Float.parseFloat(poidsField.getText());
        String produit = produitField.getText();
        SocieteRecyclage societe = societeCombo.getValue();

        Livraison livraison = new Livraison(date, poids, produit, societe);
        livraisonService.ajouter(livraison);

        // Reset form
        dateField.setValue(null);
        poidsField.clear();
        produitField.clear();
        societeCombo.setValue(null);
    }
}
