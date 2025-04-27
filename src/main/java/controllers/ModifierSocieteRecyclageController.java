package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import models.SocieteRecyclage;
import service.SocieteRecyclageService;

public class ModifierSocieteRecyclageController extends BaseController {

    @FXML private TextField idField;
    @FXML private TextField nomField;
    @FXML private TextField adresseField;
    @FXML private TextField emailField;

    private SocieteRecyclageService service = new SocieteRecyclageService();

    @FXML
    void modifierSociete(ActionEvent event) {
        int id = Integer.parseInt(idField.getText());
        String nom = nomField.getText();
        String adresse = adresseField.getText();
        String email = emailField.getText();

        SocieteRecyclage s = new SocieteRecyclage(id, nom, adresse, email);
        service.modifier(s);
    }
}