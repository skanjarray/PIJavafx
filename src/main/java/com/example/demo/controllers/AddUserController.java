package com.example.demo.controllers;
import com.example.demo.utils.SessionManager;
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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AddUserController {



    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> roleFilterComboBox;
    @FXML private Button addButton;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Void> actionsColumn;

    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;
    @FXML private Button logoutButton;

    private SessionManager sessionManager;

    private UserService userService;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<User> filteredList = FXCollections.observableArrayList();

    public void initialize(Connection connection) {
        sessionManager = SessionManager.getInstance();
        this.userService = new UserService(connection);
        setupTableColumns();
        setupRoleFilter();
        loadUserData();

        logoutButton.setOnAction(event -> handleLogout());
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

    private void setupRoleFilter() {
        roleFilterComboBox.setItems(FXCollections.observableArrayList("All", "ROLE_ADMIN", "ROLE_CLIENT"));
        roleFilterComboBox.setValue("All");
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        String selectedRole = roleFilterComboBox.getValue();
        
        filteredList.clear();
        
        for (User user : userList) {
            boolean matchesSearch = searchText.isEmpty() ||
                    user.getUsername().toLowerCase().contains(searchText) ||
                    user.getEmail().toLowerCase().contains(searchText);
                    
            boolean matchesRole = selectedRole.equals("All") ||
                    user.getRole().equals(selectedRole);
            
            if (matchesSearch && matchesRole) {
                filteredList.add(user);
            }
        }
        
        userTable.setItems(filteredList);
        statusLabel.setText("Found " + filteredList.size() + " users");
    }

    @FXML
    private void handleRoleFilter() {
        handleSearch(); // Reapply search with new role filter
    }

    private void loadUserData() {
        try {
            userList.clear();
            filteredList.clear(); // ✨ ADD THIS LINE ✨

            List<User> users = userService.getAll();
            System.out.println("Loaded users from database: " + users.size());
            for (User user : users) {
                System.out.println("User: " + user.getId() + " - " + user.getUsername() + " - " + user.getEmail() + " - " + user.getRole());
            }
            userList.addAll(users);
            filteredList.addAll(users);
            userTable.setItems(filteredList);
            statusLabel.setText("Loaded " + userList.size() + " users");
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
            showAlert("Database Error", "Failed to load users", e.getMessage(), Alert.AlertType.ERROR);
            statusLabel.setText("Error loading users");
        }
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
    private void handleLogout() {
        try {
            // Clear the session
            sessionManager.clearSession();

            // Close current window
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();

            // Show login window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to logout");
        }
    }

    private void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleExportPDF() {
        try {
            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            // Set default filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            fileChooser.setInitialFileName("users_" + timestamp + ".pdf");
            
            // Show save dialog
            Stage stage = (Stage) userTable.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            
            if (file != null) {
                // Create PDF document
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
                
                // Add title
                Paragraph title = new Paragraph("User List Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold();
                document.add(title);
                
                // Add timestamp
                Paragraph date = new Paragraph("Generated on: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12);
                document.add(date);
                
                // Add empty line
                document.add(new Paragraph("\n"));
                
                // Create table
                Table table = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
                
                // Add table headers
                table.addHeaderCell(new Cell().add(new Paragraph("ID")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("Username")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("Email")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("Role")).setBold());
                table.addHeaderCell(new Cell().add(new Paragraph("Status")).setBold());
                
                // Add user data
                for (User user : userList) {
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(user.getId()))));
                    table.addCell(new Cell().add(new Paragraph(user.getUsername())));
                    table.addCell(new Cell().add(new Paragraph(user.getEmail())));
                    table.addCell(new Cell().add(new Paragraph(user.getRole())));
                    table.addCell(new Cell().add(new Paragraph(user.isActive() ? "Active" : "Inactive")));
                }
                
                document.add(table);
                
                // Add summary
                document.add(new Paragraph("\n"));
                Paragraph summary = new Paragraph("Total Users: " + userList.size())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setItalic();
                document.add(summary);
                
                // Close document
                document.close();
                
                showSuccess("Success", "PDF exported successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to export PDF", e.getMessage());
        }
    }
}