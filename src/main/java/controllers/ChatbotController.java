package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import utils.ChatbotService;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatbotController extends BaseController implements Initializable {

    @FXML private VBox chatBox;
    @FXML private TextField messageField;
    @FXML private ScrollPane scrollPane;
    @FXML private Button sendButton;
    @FXML private Button showMapButton;

    // Service du chatbot
    private final ChatbotService chatbotService = ChatbotService.getInstance();

    // Indicateur de chargement
    private ProgressIndicator loadingIndicator;
    private HBox loadingContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurer l'interface utilisateur du chat
        scrollPane.setFitToWidth(true);
        chatBox.setSpacing(10);
        chatBox.setPadding(new Insets(10));

        // Créer l'indicateur de chargement
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(24, 24);
        loadingIndicator.setStyle("-fx-progress-color: #2E7D32;");

        loadingContainer = new HBox(loadingIndicator);
        loadingContainer.setAlignment(Pos.CENTER_LEFT);
        loadingContainer.setPadding(new Insets(5, 10, 5, 5));
        loadingContainer.setSpacing(10);

        // Ajouter un avatar au conteneur de chargement
        try {
            ImageView botAvatar = new ImageView(new Image(getClass().getResourceAsStream("C:\\Users\\azizi\\Desktop\\workshopjdbc3a\\image")));
            botAvatar.setFitHeight(30);
            botAvatar.setFitWidth(30);
            botAvatar.setPreserveRatio(true);
            loadingContainer.getChildren().add(0, botAvatar);
        } catch (Exception e) {
            // Si l'image d'avatar n'est pas trouvée, utiliser un placeholder
            VBox avatarPlaceholder = new VBox();
            avatarPlaceholder.setStyle("-fx-background-color: #2E7D32; -fx-background-radius: 15px;");
            avatarPlaceholder.setPrefWidth(30);
            avatarPlaceholder.setPrefHeight(30);
            loadingContainer.getChildren().add(0, avatarPlaceholder);
        }

        // Ajouter un message de bienvenue
        addBotMessage("🌿 Bonjour! Je suis EcoBot, votre assistant pour tout savoir sur le recyclage. Je peux répondre à vos questions sur les matériaux, les processus de recyclage, ou vous donner des conseils pour réduire vos déchets. Essayez de me demander : 'Comment recycler le plastique ?' ou 'Pourquoi recycler est important ?'");

        // Configurer les gestionnaires d'événements
        messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSendMessage();
            }
        });

        sendButton.setOnAction(event -> handleSendMessage());

        // Configurer le bouton de la carte s'il existe
        if (showMapButton != null) {
            showMapButton.setOnAction(this::goToMapView);
        }
    }

    @FXML
    public void goToMapView(ActionEvent event) {
        loadScene("MapView.fxml", event);
    }

    private void handleSendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            // Ajouter le message de l'utilisateur
            addUserMessage(message);
            messageField.clear();

            // Afficher l'indicateur de chargement
            showLoadingIndicator();

            // Traiter le message de l'utilisateur et générer une réponse de manière asynchrone
            chatbotService.getResponseAsync(message)
                    .thenAccept(response -> {
                        // Mettre à jour l'interface utilisateur sur le thread JavaFX
                        Platform.runLater(() -> {
                            // Supprimer l'indicateur de chargement
                            hideLoadingIndicator();
                            // Ajouter la réponse du bot
                            addBotMessage(response);
                        });
                    })
                    .exceptionally(ex -> {
                        // Gérer les erreurs
                        Platform.runLater(() -> {
                            hideLoadingIndicator();
                            addBotMessage("Désolé, j'ai rencontré un problème. Veuillez réessayer.");
                        });
                        return null;
                    });
        }
    }

    private void showLoadingIndicator() {
        if (!chatBox.getChildren().contains(loadingContainer)) {
            chatBox.getChildren().add(loadingContainer);
            scrollPane.setVvalue(1.0); // Défiler vers le bas
        }
    }

    private void hideLoadingIndicator() {
        chatBox.getChildren().remove(loadingContainer);
    }

    private void addUserMessage(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_RIGHT);
        messageContainer.setPadding(new Insets(5, 5, 5, 10));

        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().add("user-message");
        textFlow.setMaxWidth(300); // Limiter la largeur pour forcer le retour à la ligne

        Text text = new Text(message);
        text.setStyle("-fx-fill: white;");
        textFlow.getChildren().add(text);

        messageContainer.getChildren().add(textFlow);
        chatBox.getChildren().add(messageContainer);

        // Défiler automatiquement vers le bas
        scrollPane.setVvalue(1.0);
    }

    private void addBotMessage(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_LEFT);
        messageContainer.setPadding(new Insets(5, 10, 5, 5));

        // Ajouter l'avatar du bot
        try {
            ImageView botAvatar = new ImageView(new Image(getClass().getResourceAsStream("/images/ecobot_avatar.png")));
            botAvatar.setFitHeight(30);
            botAvatar.setFitWidth(30);
            botAvatar.setPreserveRatio(true);
            messageContainer.getChildren().add(botAvatar);
        } catch (Exception e) {
            // Si l'image de l'avatar n'est pas trouvée, utiliser un placeholder
            VBox avatarPlaceholder = new VBox();
            avatarPlaceholder.setStyle("-fx-background-color: #2E7D32; -fx-background-radius: 15px;");
            avatarPlaceholder.setPrefWidth(30);
            avatarPlaceholder.setPrefHeight(30);
            messageContainer.getChildren().add(avatarPlaceholder);
        }

        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().add("bot-message");
        textFlow.setMaxWidth(300); // Limiter la largeur pour forcer le retour à la ligne

        Text text = new Text(message);
        text.setStyle("-fx-fill: #333333;");
        textFlow.getChildren().add(text);

        messageContainer.getChildren().add(textFlow);
        chatBox.getChildren().add(messageContainer);

        // Défiler automatiquement vers le bas
        scrollPane.setVvalue(1.0);
    }
}
