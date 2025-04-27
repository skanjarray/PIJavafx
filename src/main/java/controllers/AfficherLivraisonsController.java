package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import models.Livraison;
import service.LivraisonService;
import utils.QRCodeGenerator;
import utils.QRCodeTableCell;

public class AfficherLivraisonsController extends BaseController {

    @FXML private TableView<Livraison> tableView;
    @FXML private TableColumn<Livraison, Integer> idColumn;
    @FXML private TableColumn<Livraison, String> produitColumn;
    @FXML private TableColumn<Livraison, Float> poidsColumn;
    @FXML private TableColumn<Livraison, String> dateColumn;
    @FXML private TableColumn<Livraison, String> societeColumn;
    @FXML private TableColumn<Livraison, String> qrCodeColumn; // New column for QR code

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

        // Configure QR code column
        qrCodeColumn.setCellValueFactory(cellData -> {
            Livraison livraison = cellData.getValue();
            String qrData = String.format(
                    "ID: %d\nProduit: %s\nPoids: %.2f kg\nDate: %s\nSociété: %s",
                    livraison.getId(),
                    livraison.getProduit(),
                    livraison.getPoids(),
                    livraison.getDate().toString(),
                    livraison.getSocieteRecyclage().getNom()
            );
            return javafx.beans.binding.Bindings.createStringBinding(() -> qrData);
        });

        qrCodeColumn.setCellFactory(column -> new QRCodeTableCell<>());

        // Load data
        tableView.getItems().addAll(livraisonService.afficher());
    }
}