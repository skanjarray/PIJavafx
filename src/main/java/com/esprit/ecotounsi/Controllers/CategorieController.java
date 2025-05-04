package com.esprit.ecotounsi.Controllers;

import com.esprit.ecotounsi.Models.Categorie;
import com.esprit.ecotounsi.EcotounsiApplication;
import com.esprit.ecotounsi.Repositories.CategorieDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CategorieController {

    @FXML
    private TableView<Categorie> categorieTable;
    @FXML
    private TableColumn<Categorie, String> nomColonne;
    @FXML
    private TableColumn<Categorie, String> descriptionColonne;

    @FXML
    private TextField nomField;
    @FXML
    private TextField descriptionField;

    private final CategorieDAO categorieDAO = new CategorieDAO();
    private Categorie categorieSelectionnee = null;

    @FXML
    public void initialize() {
        nomColonne.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nom"));
        descriptionColonne.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));

        refreshCategorieTable();

        categorieTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categorieSelectionnee = newSelection;
                nomField.setText(newSelection.getNom());
                descriptionField.setText(newSelection.getDescription());
            }
        });
    }

    private void refreshCategorieTable() {
        List<Categorie> categories = categorieDAO.getAllCategories();
        ObservableList<Categorie> observableList = FXCollections.observableArrayList(categories);
        categorieTable.setItems(observableList);
    }

    @FXML
    public void ajouterCategorie(ActionEvent event) {
        String nom = nomField.getText();
        String description = descriptionField.getText();

        if (nom.isEmpty() || description.isEmpty()) {
            showError("Tous les champs doivent être remplis.");
            return;
        }

        Categorie categorie = new Categorie(nom, description);
        if (categorieDAO.addCategorie(categorie)) {
            refreshCategorieTable();
            clearFields();
        } else {
            showError("Erreur lors de l'ajout de la catégorie.");
        }
    }

    @FXML
    public void modifierCategorie(ActionEvent event) {
        if (categorieSelectionnee == null) {
            showError("Veuillez sélectionner une catégorie à modifier.");
            return;
        }

        String nom = nomField.getText();
        String description = descriptionField.getText();

        if (nom.isEmpty() || description.isEmpty()) {
            showError("Tous les champs doivent être remplis.");
            return;
        }

        categorieSelectionnee.setNom(nom);
        categorieSelectionnee.setDescription(description);

        if (categorieDAO.updateCategorie(categorieSelectionnee)) {
            refreshCategorieTable();
            clearFields();
            categorieSelectionnee = null;
        } else {
            showError("Erreur lors de la modification de la catégorie.");
        }
    }

    @FXML
    public void supprimerCategorie(ActionEvent event) {
        if (categorieSelectionnee == null) {
            showError("Veuillez sélectionner une catégorie à supprimer.");
            return;
        }

        if (categorieDAO.deleteCategorie(categorieSelectionnee.getId())) {
            refreshCategorieTable();
            clearFields();
            categorieSelectionnee = null;
        } else {
            showError("Erreur lors de la suppression de la catégorie.");
        }
    }

    @FXML
    public void Retourner(ActionEvent event) {
        try {
            Stage stage = (Stage) nomField.getScene().getWindow();
            EcotounsiApplication app = new EcotounsiApplication();
            app.start(stage);
        } catch (Exception e) {
            System.err.println("Erreur lors du retour à l'écran principal : " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nomField.clear();
        descriptionField.clear();
    }

    @FXML
    private void allerVersProduit(ActionEvent event) {
        try {
            // Récupération du Stage à partir du bouton cliqué
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Chargement de l'interface Produit.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/ecotounsi/Produit.fxml"));

            // Application de la nouvelle scène
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            System.err.println("Erreur lors du chargement de la page Produit : " + ex.getMessage());
        }
    }

    @FXML
    private void allerVersFrontOffice(ActionEvent event) {
        try {
            // Obtenir la scène à partir de l'événement (via le bouton)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Charger l'interface Front-Office
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/ecotounsi/ProduitClientSimple.fxml"));

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            System.err.println("Erreur lors du chargement de l'interface Front-Office : " + ex.getMessage());
        }
    }


    public void handleHover(javafx.scene.input.MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: #2C6B2F; -fx-text-fill: white;");
    }

    public void handleExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white;");
    }

}
