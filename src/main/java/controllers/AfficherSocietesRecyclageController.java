package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.SocieteRecyclage;
import service.SocieteRecyclageService;
import utils.QRCodeTableCell;

public class AfficherSocietesRecyclageController extends BaseController {

    @FXML private TableView<SocieteRecyclage> tableView;
    @FXML private TableColumn<SocieteRecyclage, Integer> idColumn;
    @FXML private TableColumn<SocieteRecyclage, String> nomColumn;
    @FXML private TableColumn<SocieteRecyclage, String> adresseColumn;
    @FXML private TableColumn<SocieteRecyclage, String> emailColumn;
    @FXML private TableColumn<SocieteRecyclage, String> qrCodeColumn; // New column for QR code
    @FXML private TextField searchField; // Search field

    private SocieteRecyclageService service = new SocieteRecyclageService();
    private ObservableList<SocieteRecyclage> societesList = FXCollections.observableArrayList();
    private FilteredList<SocieteRecyclage> filteredList;

    @FXML
    public void initialize() {
        // Set up table columns
       // idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Configure QR code column
        qrCodeColumn.setCellValueFactory(cellData -> {
            SocieteRecyclage societe = cellData.getValue();
            String qrData = String.format(
                    "ID: %d\nNom: %s\nAdresse: %s\nEmail: %s",
                    societe.getId(),
                    societe.getNom(),
                    societe.getAdresse(),
                    societe.getEmail()
            );
            return javafx.beans.binding.Bindings.createStringBinding(() -> qrData);
        });

        qrCodeColumn.setCellFactory(column -> new QRCodeTableCell<>());

        // Load data
        societesList.addAll(service.afficher());

        // Set up filtering
        filteredList = new FilteredList<>(societesList, p -> true);
        tableView.setItems(filteredList);

        // Configure search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(societe -> {
                // If search field is empty, show all records
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Compare fields with filter
                if (societe.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (societe.getAdresse().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (societe.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });
    }
}