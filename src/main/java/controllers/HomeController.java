package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    private void loadScene(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToAjouterLivraison(ActionEvent event) {
        loadScene("AjouterLivraison.fxml", event);
    }

    @FXML
    public void goToModifierLivraison(ActionEvent event) {
        loadScene("ModifierLivraison.fxml", event);
    }

    @FXML
    public void goToAfficherLivraisons(ActionEvent event) {
        loadScene("AfficherLivraisons.fxml", event);
    }

    @FXML
    public void goToAjouterSociete(ActionEvent event) {
        loadScene("AjouterSociete.fxml", event);
    }

    @FXML
    public void goToModifierSociete(ActionEvent event) {
        loadScene("ModifierSocieteRecyclage.fxml", event);
    }

    @FXML
    public void goToAfficherSocietes(ActionEvent event) {
        loadScene("AfficherSocietes.fxml", event);
    }
}
