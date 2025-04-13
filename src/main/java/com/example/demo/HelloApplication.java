package com.example.demo;

import com.example.demo.controllers.AddUserController;
import com.example.demo.utils.DbConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addUser.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        
        // Get the controller and initialize it with database connection
        AddUserController controller = fxmlLoader.getController();
        Connection connection = DbConnection.getInstance().getCnx();
        controller.initialize(connection);
        
        stage.setTitle("User Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}