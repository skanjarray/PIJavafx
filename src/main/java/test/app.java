package test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.ChatbotService;

public class app extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger la vue principale
        Parent root = FXMLLoader.load(getClass().getResource("/Home.fxml"));
        primaryStage.setTitle("EcoTounsi - Système de Gestion de Recyclage");

        // Configurer le mode plein écran
        primaryStage.setMaximized(true);

        // Créer une scène avec une taille appropriée qui s'adaptera bien en plein écran
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // Ajouter un écouteur pour s'assurer que l'état plein écran est maintenu
        primaryStage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if (!isNowMaximized) {
                primaryStage.setMaximized(true);
            }
        });

        // Configurer la fermeture propre de l'application
        primaryStage.setOnCloseRequest(event -> {
            // Arrêter les services en arrière-plan
            ChatbotService.getInstance().shutdown();
            Platform.exit();
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
