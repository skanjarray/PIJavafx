package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class app extends Application {

    // Modify the start method to ensure full screen mode is properly set
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Home.fxml"));
        primaryStage.setTitle("Gestion des Livraisons & Sociétés");

        // Set the application to open in full screen
        primaryStage.setMaximized(true);

        // Create a scene with appropriate size that will scale well in full screen
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // Add a listener to ensure full screen state is maintained
        primaryStage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if (!isNowMaximized) {
                primaryStage.setMaximized(true);
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
