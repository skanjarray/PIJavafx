package com.esprit.ecotounsi.Controllers;

import com.esprit.ecotounsi.Models.Categorie;
import com.esprit.ecotounsi.Models.Produit;
import com.esprit.ecotounsi.Repositories.ProduitDAO;
import com.esprit.ecotounsi.Config.MailSender;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;


import java.io.IOException;
import java.util.List;

public class ProduitController {

    @FXML private TableView<Produit> tableView;
    @FXML private TableColumn<Produit, String> nomCol;
    @FXML private TableColumn<Produit, String> uniteCol;
    @FXML private TableColumn<Produit, Integer> quantiteCol;
    @FXML private TableColumn<Produit, String> categorieCol;
    @FXML private TextField nomField;
    @FXML private TextField uniteField;
    @FXML private TextField quantiteField;
    @FXML private ComboBox<Categorie> categorieComboBox;
    @FXML private TextField emailField;

    private ProduitDAO produitDAO = new ProduitDAO();
    private Produit produitSelectionne = null;
    private MailSender mailSender = new MailSender();

    @FXML
    public void initialize() {
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        uniteCol.setCellValueFactory(new PropertyValueFactory<>("unite"));
        quantiteCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        categorieCol.setCellValueFactory(cellData -> {
            Categorie categorie = cellData.getValue().getCategorie();
            return new SimpleStringProperty(categorie != null ? categorie.getNom() : "Aucune catégorie");
        });

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        refreshTable();

        categorieComboBox.setItems(FXCollections.observableArrayList(produitDAO.getAllCategories()));

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                produitSelectionne = newSel;
                nomField.setText(newSel.getNom());
                uniteField.setText(newSel.getUnite());
                quantiteField.setText(String.valueOf(newSel.getQuantite()));
                categorieComboBox.setValue(newSel.getCategorie());
            }
        });
    }

    @FXML
    private void ajouterProduit() {
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
                this.envoyerEmail();
                refreshTable();
                clearForm();
            } else {
                showErrorDialog("Erreur lors de l'ajout.");
            }
        } catch (NumberFormatException ex) {
            showErrorDialog("Quantité invalide.");
        }
    }

    @FXML
    private void supprimerProduit() {
        Produit selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null && produitDAO.deleteProduit(selected.getId())) {
            refreshTable();
        } else {
            showErrorDialog("Sélection invalide ou erreur de suppression.");
        }
    }

    @FXML
    private void mettreAJourProduit() {
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
                    clearForm();
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
    }

    @FXML
    private void allerVersCategorie() {
        try {
            Stage stage = (Stage) tableView.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/ecotounsi/categorie.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            System.err.println("Erreur lors du chargement de l'interface catégorie : " + ex.getMessage());
        }
    }

    private void refreshTable() {
        List<Produit> produits = produitDAO.getAllProduits();
        ObservableList<Produit> observableList = FXCollections.observableArrayList(produits);
        tableView.setItems(observableList);
    }

    private void clearForm() {
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

    @FXML
    private void envoyerEmail() {
        String email = emailField.getText();
        if (email.isEmpty()) {
            showErrorDialog("Veuillez saisir une adresse e-mail.");
            return;
        }

        try {
            String subject = "Confirmation d'ajout de produit";
            String body = "Votre produit a été ajouté avec succès à notre base de données.";

            mailSender.sendEmail(email, subject, body);
            showInfoDialog("Email envoyé avec succès à " + email);

            emailField.clear(); // Vide le champ après envoi
        } catch (Exception e) {
            showErrorDialog("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    // Méthode pour rediriger vers le front-office du client
    @FXML
    private void allerVersFrontOffice() {
        try {
            // Obtenir la fenêtre (stage) actuelle
            Stage stage = (Stage) tableView.getScene().getWindow();  // Directement récupérer le Stage

            // Charger le fichier FXML de l'interface Front-Office
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/ecotounsi/ProduitClientSimple.fxml"));

            // Appliquer la nouvelle scène
            stage.setScene(new Scene(root));
            stage.show();  // Afficher la nouvelle scène
        } catch (IOException ex) {
            System.err.println("Erreur lors du chargement de l'interface Front-Office : " + ex.getMessage());
        }
    }

    public void handleHover(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: #2C6B2F; -fx-text-fill: white;");
    }

    public void handleExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white;");
    }


}
