module com.example.demo {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive java.sql;
    requires transitive javafx.graphics;
    requires javafx.base;
    requires static lombok;

    opens com.example.demo to javafx.fxml;

    exports com.example.demo;
    exports com.example.demo.controller;

    opens com.example.demo.controller to javafx.fxml;

    exports com.example.demo.model;
    exports com.example.demo.dto;
    exports com.example.demo.tm;
    exports com.example.demo.util;
    exports com.example.demo.dao;
    exports com.example.demo.db;
    exports com.example.demo.exception;

    opens com.example.demo.model to javafx.base;
    opens com.example.demo.dto to javafx.base;
    opens com.example.demo.tm to javafx.base;
}
