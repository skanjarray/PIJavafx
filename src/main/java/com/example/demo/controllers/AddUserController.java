package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AddUserController {

    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Void> actionsColumn;
    @FXML private Button viewButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private UserService userService;
    private ObservableList<User> userList = FXCollections.observableArrayList();

    public void initialize(Connection connection) {
        this.userService = new UserService(connection);
        setupTableColumns();
        loadUserData();
        setupButtonActions();
        setupTableSelection();
    }

    private void setupTableColumns() {
        System.out.println("Setting up table columns...");
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        // Setup actions column with edit and delete buttons
        actionsColumn.setCellFactory(new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                final TableCell<User, Void> cell = new TableCell<User, Void>() {
                    private final Button editBtn = new Button("✎");
                    private final Button deleteBtn = new Button("🗑");
                    private final HBox buttons = new HBox(5);
                    
                    {
                        buttons.setAlignment(Pos.CENTER);
                        editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                        
                        editBtn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            showEditUserDialog(user);
                        });
                        
                        deleteBtn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            deleteUser(user);
                        });
                        
                        // Add hover effects
                        editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: #d35400; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;"));
                        editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;"));
                        
                        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;"));
                        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;"));
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            buttons.getChildren().clear();
                            buttons.getChildren().addAll(editBtn, deleteBtn);
                            setGraphic(buttons);
                        }
                    }
                };
                return cell;
            }
        });

        // Set column resize policies
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Make sure the table is properly initialized
        userTable.setItems(userList);
        
        System.out.println("Table columns setup completed");
    }

    private void loadUserData() {
        try {
            userList.clear();
            List<User> users = userService.getAll();
            System.out.println("Loaded users from database: " + users.size());
            for (User user : users) {
                System.out.println("User: " + user.getId() + " - " + user.getUsername() + " - " + user.getEmail() + " - " + user.getRole());
            }
            userList.addAll(users);
            statusLabel.setText("Loaded " + userList.size() + " users");
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
            showAlert("Database Error", "Failed to load users", e.getMessage(), Alert.AlertType.ERROR);
            statusLabel.setText("Error loading users");
        }
    }

    private void setupButtonActions() {
        addButton.setOnAction(event -> showAddUserDialog());

        viewButton.setOnAction(event -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showUserDetails(selected);
            }
        });

        editButton.setOnAction(event -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditUserDialog(selected);
            }
        });

        deleteButton.setOnAction(event -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                deleteUser(selected);
            }
        });
    }

    private void setupTableSelection() {
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean itemSelected = newSelection != null;
            viewButton.setDisable(!itemSelected);
            editButton.setDisable(!itemSelected);
            deleteButton.setDisable(!itemSelected);
        });
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void showAddUserDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/addUserDialog.fxml"));
            Parent root = loader.load();
            AddUserDialogController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New User");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UNDECORATED);
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            controller.setDialogStage(dialogStage);
            controller.setUserService(userService);
            
            dialogStage.showAndWait();
            
            if (controller.isSaveClicked()) {
                loadUserData();
                showSuccess("Success", "User added successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to open add user dialog", e.getMessage());
        }
    }

    private void showUserDetails(User user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Details");
        alert.setHeaderText("Details for " + user.getUsername());
        alert.setContentText(
                "ID: " + user.getId() + "\n" +
                        "Email: " + user.getEmail() + "\n" +
                        "Role: " + user.getRole()
        );
        alert.showAndWait();
    }

    @FXML
    private void showEditUserDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/editUserDialog.fxml"));
            Parent root = loader.load();
            EditUserDialogController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit User");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UNDECORATED);
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            controller.setDialogStage(dialogStage);
            controller.setUser(user);
            controller.setUserService(userService);
            
            dialogStage.showAndWait();
            
            if (controller.isSaveClicked()) {
                loadUserData();
                showSuccess("Success", "User updated successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to open edit user dialog", e.getMessage());
        }
    }

    @FXML
    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete user: " + user.getUsername() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.delete(user.getId());
                loadUserData();
                showSuccess("Success", "User deleted successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error", "Failed to delete user", e.getMessage());
            }
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    public void handleButtonHover(MouseEvent event) {
        Button button = (Button) event.getSource();
        String currentStyle = button.getStyle();
        
        // Determine the appropriate hover color based on the button's current background color
        if (currentStyle.contains("#2ecc71")) {
            // Green button (Add User)
            button.setStyle(currentStyle.replace("#2ecc71", "#27ae60"));
        } else if (currentStyle.contains("#3498db")) {
            // Blue button (View, Search)
            button.setStyle(currentStyle.replace("#3498db", "#2980b9"));
        } else if (currentStyle.contains("#f39c12")) {
            // Orange button (Edit)
            button.setStyle(currentStyle.replace("#f39c12", "#d35400"));
        } else if (currentStyle.contains("#e74c3c")) {
            // Red button (Delete)
            button.setStyle(currentStyle.replace("#e74c3c", "#c0392b"));
        } else if (currentStyle.contains("transparent")) {
            // Transparent button (Dashboard, Settings)
            button.setStyle(currentStyle.replace("transparent", "#34495e"));
        }
    }
    
    @FXML
    public void handleButtonExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        String currentStyle = button.getStyle();
        
        // Restore the original color based on the button's purpose
        if (currentStyle.contains("#27ae60")) {
            // Green button (Add User)
            button.setStyle(currentStyle.replace("#27ae60", "#2ecc71"));
        } else if (currentStyle.contains("#2980b9")) {
            // Blue button (View, Search)
            button.setStyle(currentStyle.replace("#2980b9", "#3498db"));
        } else if (currentStyle.contains("#d35400")) {
            // Orange button (Edit)
            button.setStyle(currentStyle.replace("#d35400", "#f39c12"));
        } else if (currentStyle.contains("#c0392b")) {
            // Red button (Delete)
            button.setStyle(currentStyle.replace("#c0392b", "#e74c3c"));
        } else if (currentStyle.contains("#34495e")) {
            // Transparent button (Dashboard, Settings)
            button.setStyle(currentStyle.replace("#34495e", "transparent"));
        }
    }

    @FXML
    private void handleEditUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/editUserDialog.fxml"));
                Parent root = loader.load();
                EditUserDialogController controller = loader.getController();
                controller.setUser(selectedUser);
                controller.setUserService(userService);

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Edit User");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initStyle(StageStyle.UNDECORATED);
                dialogStage.setScene(new Scene(root));
                
                dialogStage.setOnCloseRequest(event -> {
                    loadUserData(); // Refresh the table when dialog is closed
                });
                
                dialogStage.showAndWait();
            } catch (IOException e) {
                showError("Error", "Could not load edit user dialog", e.getMessage());
            }
        } else {
            showError("Error", "User Selection Required", "Please select a user to edit");
        }
    }

    @FXML
    private void handleDeleteButton() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            deleteUser(selectedUser);
        } else {
            showAlert("Error", "No User Selected", "Please select a user to delete", Alert.AlertType.WARNING);
        }
    }
}