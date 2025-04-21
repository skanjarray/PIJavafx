package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import models.SocieteRecyclage;
import service.SocieteRecyclageService;

public class AjouterSocieteRecyclageController {

    @FXML private TextField nomField;
    @FXML private TextField adresseField;
    @FXML private TextField emailField;

    private SocieteRecyclageService service = new SocieteRecyclageService();

    @FXML
    void ajouterSociete(ActionEvent event) {
        String nom = nomField.getText();
        String adresse = adresseField.getText();
        String email = emailField.getText();

        SocieteRecyclage societe = new SocieteRecyclage(nom, adresse, email);
        service.ajouter(societe);

        nomField.clear();
        adresseField.clear();
        emailField.clear();
    }
}
