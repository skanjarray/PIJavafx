package com.esprit.ecotounsi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
            HelloApplication app = new HelloApplication();
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
}
