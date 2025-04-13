module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.controllers to javafx.fxml;
    opens com.example.demo.model to javafx.base;
    exports com.example.demo;
    exports com.example.demo.controllers;
    exports com.example.demo.model;
}