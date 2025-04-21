package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import models.SocieteRecyclage;
import service.SocieteRecyclageService;

public class AfficherSocietesRecyclageController {

    @FXML private TableView<SocieteRecyclage> tableView;
    @FXML private TableColumn<SocieteRecyclage, Integer> idColumn;
    @FXML private TableColumn<SocieteRecyclage, String> nomColumn;
    @FXML private TableColumn<SocieteRecyclage, String> adresseColumn;
    @FXML private TableColumn<SocieteRecyclage, String> emailColumn;

    private SocieteRecyclageService service = new SocieteRecyclageService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableView.getItems().addAll(service.afficher());
    }
}
