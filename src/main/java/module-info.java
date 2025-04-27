module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires jbcrypt;
    requires java.mail;
    requires jakarta.mail;
    requires json.simple;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires jdk.httpserver;
    requires kernel;
    requires layout;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.controllers to javafx.fxml;
    opens com.example.demo.model to javafx.base;
    opens com.example.demo.service to javafx.base;
    exports com.example.demo;
    exports com.example.demo.controllers;
    exports com.example.demo.model;
    exports com.example.demo.service;
}