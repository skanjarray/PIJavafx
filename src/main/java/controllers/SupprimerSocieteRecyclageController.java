package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import service.SocieteRecyclageService;

public class SupprimerSocieteRecyclageController extends BaseController {

    @FXML private TextField idField;

    private SocieteRecyclageService service = new SocieteRecyclageService();

    @FXML
    void supprimerSociete(ActionEvent event) {
        int id = Integer.parseInt(idField.getText());
        service.supprimer(id);
        idField.clear();
    }
}