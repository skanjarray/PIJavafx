package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import service.LivraisonService;

public class SupprimerLivraisonController {

    @FXML private TextField idField;

    private LivraisonService livraisonService = new LivraisonService();

    @FXML
    void supprimerLivraison(ActionEvent event) {
        int id = Integer.parseInt(idField.getText());
        livraisonService.supprimer(id);
        idField.clear();
    }
}
