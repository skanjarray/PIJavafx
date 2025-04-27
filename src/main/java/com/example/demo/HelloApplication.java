package com.example.demo;

import com.example.demo.controllers.LoginController;
import com.example.demo.utils.DbConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database connection
        Connection connection = DbConnection.getInstance().getCnx();

        // Load login screen
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 800);
        
        // Get the controller and initialize it with database connection
        LoginController controller = fxmlLoader.getController();
        
        stage.setTitle("Sign In");
        stage.setScene(scene);
        
        // Add window close handler
        stage.setOnCloseRequest((WindowEvent event) -> {
            controller.cleanup();
        });
        
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}