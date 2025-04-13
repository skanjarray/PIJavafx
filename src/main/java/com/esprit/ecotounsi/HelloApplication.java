package com.esprit.ecotounsi;

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

public class HelloApplication extends Application {
    private ProduitDAO produitDAO = new ProduitDAO();
    private Produit produitSelectionne = null;
    private TableView<Produit> tableView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestion des Produits - By Mariouma");

        // TableView setup
        tableView = new TableView<>();
        TableColumn<Produit, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Produit, String> uniteCol = new TableColumn<>("Unité");
        uniteCol.setCellValueFactory(new PropertyValueFactory<>("unite"));

        TableColumn<Produit, Integer> quantiteCol = new TableColumn<>("Quantité");
        quantiteCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        TableColumn<Produit, String> categorieCol = new TableColumn<>("Catégorie");
        categorieCol.setCellValueFactory(cellData -> {
            Categorie categorie = cellData.getValue().getCategorie();
            return new SimpleStringProperty(categorie != null ? categorie.getNom() : "Aucune catégorie");
        });

        tableView.getColumns().addAll(nomCol, uniteCol, quantiteCol, categorieCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        refreshTable();

        // Champs de saisie
        TextField nomField = new TextField();
        nomField.setPromptText("Nom du produit");

        TextField uniteField = new TextField();
        uniteField.setPromptText("Unité du produit");

        TextField quantiteField = new TextField();
        quantiteField.setPromptText("Quantité du produit");

        // Sélectionner la catégorie à partir d'une liste (à implémenter)
        ComboBox<Categorie> categorieComboBox = new ComboBox<>();
        categorieComboBox.setPromptText("Sélectionner une catégorie");

        // Récupérer toutes les catégories depuis la base de données
        categorieComboBox.setItems(FXCollections.observableArrayList(produitDAO.getAllCategories()));

        // Ajout
        Button btnAjouter = new Button("Ajouter un produit");
        btnAjouter.setOnAction(e -> {
            try {
                String nom = nomField.getText();
                String unite = uniteField.getText();
                int quantite = Integer.parseInt(quantiteField.getText());
                Categorie categorie = categorieComboBox.getValue();

                if (nom.isEmpty() || unite.isEmpty() || categorie == null) {
                    showErrorDialog("Tous les champs doivent être remplis.");
                    return;
                }

                Produit nouveauProduit = new Produit(nom, unite, quantite, categorie);
                if (produitDAO.addProduit(nouveauProduit)) {
                    showInfoDialog("Produit inséré avec succès !");
                    refreshTable();
                    clearForm(nomField, uniteField, quantiteField, categorieComboBox);
                } else {
                    showErrorDialog("Erreur lors de l'ajout.");
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Quantité invalide.");
            }
        });

        // Suppression
        Button btnSupprimer = new Button("Supprimer un produit");
        btnSupprimer.setOnAction(e -> {
            Produit selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null && produitDAO.deleteProduit(selected.getId())) {
                refreshTable();
            } else {
                showErrorDialog("Sélection invalide ou erreur de suppression.");
            }
        });

        // Mise à jour
        Button btnMettreAJour = new Button("Mettre à jour un produit");
        btnMettreAJour.setOnAction(e -> {
            if (produitSelectionne != null) {
                try {
                    String nom = nomField.getText();
                    String unite = uniteField.getText();
                    int quantite = Integer.parseInt(quantiteField.getText());
                    Categorie categorie = categorieComboBox.getValue();

                    if (nom.isEmpty() || unite.isEmpty() || categorie == null) {
                        showErrorDialog("Tous les champs doivent être remplis.");
                        return;
                    }

                    produitSelectionne.setNom(nom);
                    produitSelectionne.setUnite(unite);
                    produitSelectionne.setQuantite(quantite);
                    produitSelectionne.setCategorie(categorie);

                    if (produitDAO.updateProduit(produitSelectionne)) {
                        refreshTable();
                        clearForm(nomField, uniteField, quantiteField, categorieComboBox);
                        produitSelectionne = null;
                    } else {
                        showErrorDialog("Erreur de mise à jour.");
                    }
                } catch (NumberFormatException ex) {
                    showErrorDialog("Quantité invalide.");
                }
            } else {
                showErrorDialog("Aucun produit sélectionné.");
            }
        });

        // Aller vers catégorie
        Button btnAllerVersCategorie = new Button("Aller vers la gestion de catégorie");
        btnAllerVersCategorie.setOnAction(e -> naviguerVersCategorie(primaryStage));

        // Sélection dans la table
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                produitSelectionne = newSel;
                nomField.setText(newSel.getNom());
                uniteField.setText(newSel.getUnite());
                quantiteField.setText(String.valueOf(newSel.getQuantite()));
                categorieComboBox.setValue(newSel.getCategorie());
            }
        });

        VBox vbox = new VBox(10, tableView, nomField, uniteField, quantiteField, categorieComboBox,
                btnAjouter, btnSupprimer, btnMettreAJour, btnAllerVersCategorie);
        Scene scene = new Scene(vbox, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
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
