package com.esprit.ecotounsi.Controllers;

import com.esprit.ecotounsi.Models.Produit;
import com.esprit.ecotounsi.Repositories.ProduitDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProduitClientSimpleController implements Initializable {

    @FXML
    private ListView<Produit> listViewProduits;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnCompte;

    @FXML
    private Button btnActualiser;

    private final ProduitDAO produitDAO = new ProduitDAO();
    private List<Produit> produitsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        afficherProduits();
        // Gérer la recherche en temps réel
        searchField.setOnKeyReleased(this::handleSearch);
    }

    private void afficherProduits() {
        produitsList = produitDAO.getAllProduits();

        if (produitsList != null && !produitsList.isEmpty()) {
            listViewProduits.setCellFactory(param -> new ListCell<Produit>() {
                @Override
                protected void updateItem(Produit produit, boolean empty) {
                    super.updateItem(produit, empty);

                    if (empty || produit == null) {
                        setGraphic(null);
                        setText(null);
                    }
                    else {
                        // Sécurité : même si produit n'est pas null ici, on l'a bien vérifié
                        boolean estFavori = Boolean.TRUE.equals(produit.isFavori());

                        // 🌟 Icône étoile
                        Label etoileLabel = new Label(estFavori ? "★" : "☆");
                        etoileLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (estFavori ? "gold" : "white") + ";");

                        // 📝 Bouton favori
                        Button favoriButton = new Button(estFavori ? "Retirer des favoris" : "Ajouter aux favoris");
                        favoriButton.setGraphic(etoileLabel);
                        favoriButton.setContentDisplay(ContentDisplay.LEFT);
                        favoriButton.setStyle(
                                "-fx-background-color: #2E8B57; " +
                                        "-fx-text-fill: white; " +
                                        "-fx-font-size: 12px; " +
                                        "-fx-background-radius: 6px;"
                        );

                        favoriButton.setOnAction(event -> {
                            boolean nouveauEtat = !Boolean.TRUE.equals(produit.isFavori());
                            produit.setFavori(nouveauEtat);
                            produitDAO.updateFavori(produit.getId(), nouveauEtat);

                            // 🔄 Mise à jour dynamique de l'affichage du bouton
                            etoileLabel.setText(nouveauEtat ? "★" : "☆");
                            etoileLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (nouveauEtat ? "gold" : "white") + ";");
                            favoriButton.setText(nouveauEtat ? "Retirer des favoris" : "Ajouter aux favoris");
                        });


                        // 🖼️ Image, nom, description...
                        ImageView imageView = new ImageView();
                        imageView.setImage(new Image(getClass().getResource("/com/esprit/ecotounsi/placeholder.png").toExternalForm()));
                        imageView.setFitWidth(100);
                        imageView.setFitHeight(100);
                        imageView.setPreserveRatio(true);
                        imageView.setStyle("-fx-effect: dropshadow(gaussian, #ccc, 5, 0.3, 2, 2);");

                        Label nomLabel = new Label(produit.getNom());
                        nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;");

                        Label descriptionLabel = new Label(produit.getDescription() != null ? produit.getDescription() : "Aucune description disponible.");
                        descriptionLabel.setWrapText(true);
                        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                        VBox infoBox = new VBox(5, nomLabel, descriptionLabel, favoriButton);
                        infoBox.setPadding(new Insets(5, 0, 0, 10));
                        infoBox.setPrefWidth(420);

                        HBox hBox = new HBox(15, imageView, infoBox);
                        hBox.setPadding(new Insets(15));
                        hBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #DADADA; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, #eee, 4, 0.3, 2, 2);");
                        hBox.setPrefHeight(130);

                        setGraphic(hBox);
                    }
                }

            });

            listViewProduits.getItems().setAll(produitsList);
        } else {
            Label noProductsLabel = new Label("Aucun produit disponible");
            noProductsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
            listViewProduits.setPlaceholder(noProductsLabel);
        }
    }

    @FXML
    private void handleCompte() {
        try {
            // Charger la nouvelle scène (par exemple, Dashboard)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/ecotounsi/produit.fxml"));  // Remplacez par le chemin correct
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage currentStage = (Stage) btnCompte.getScene().getWindow();

            // Changer la scène de la fenêtre actuelle
            currentStage.setScene(new Scene(root));

            // Optionnel : Redimensionner la fenêtre (si nécessaire)
            currentStage.setWidth(800);
            currentStage.setHeight(600);

            // Optionnel : Afficher ou personnaliser d'autres aspects de la fenêtre
            currentStage.setTitle("Mon Compte");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Gérer la recherche
    private void handleSearch(KeyEvent event) {
        String searchText = searchField.getText().toLowerCase();

        // Filtrer les produits en fonction de la recherche
        List<Produit> filteredList = produitsList.stream()
                .filter(produit -> produit.getNom().toLowerCase().contains(searchText) ||
                        (produit.getDescription() != null && produit.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());

        // Mettre à jour la ListView avec les produits filtrés
        listViewProduits.getItems().setAll(filteredList);
    }

    // Action du bouton Actualiser
    @FXML
    private void handleActualiser() {
        searchField.clear();  // Réinitialiser le champ de recherche
        listViewProduits.getItems().setAll(produitsList);  // Réinitialiser la ListView avec tous les produits
    }
}
