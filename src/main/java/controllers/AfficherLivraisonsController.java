package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Livraison;
import service.LivraisonService;

public class AfficherLivraisonsController {

    @FXML private TableView<Livraison> tableView;
    @FXML private TableColumn<Livraison, Integer> idColumn;
    @FXML private TableColumn<Livraison, String> produitColumn;
    @FXML private TableColumn<Livraison, Float> poidsColumn;
    @FXML private TableColumn<Livraison, String> dateColumn;
    @FXML private TableColumn<Livraison, String> societeColumn;

    private LivraisonService livraisonService = new LivraisonService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        produitColumn.setCellValueFactory(new PropertyValueFactory<>("produit"));
        poidsColumn.setCellValueFactory(new PropertyValueFactory<>("poids"));
        dateColumn.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getDate().toString()));
        societeColumn.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() ->
                cell.getValue().getSocieteRecyclage().getNom()));

        tableView.getItems().addAll(livraisonService.afficher());
    }
}
