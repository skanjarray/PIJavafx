package com.esprit.ecotounsi;

import com.esprit.ecotounsi.Models.Categorie;
import com.esprit.ecotounsi.Models.Produit;
import com.esprit.ecotounsi.Repositories.ProduitDAO;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class EcotounsiApplication extends Application {
    private ProduitDAO produitDAO = new ProduitDAO();
    private Produit produitSelectionne = null;
    private TableView<Produit> tableView;

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/ecotounsi/produit.fxml"));
            Scene scene = new Scene(root, 600, 600);
            primaryStage.setTitle("Gestion des Produits - By Mariouma");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void refreshTable() {
        List<Produit> produits = produitDAO.getAllProduits();
        ObservableList<Produit> observableList = FXCollections.observableArrayList(produits);
        tableView.setItems(observableList);
    }

    private void clearForm(TextField nomField, TextField uniteField, TextField quantiteField, ComboBox<Categorie> categorieComboBox) {
        nomField.clear();
        uniteField.clear();
        quantiteField.clear();
        categorieComboBox.getSelectionModel().clearSelection();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void naviguerVersCategorie(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/ecotounsi/categorie.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            System.err.println("Erreur lors du chargement de l'interface catégorie : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
