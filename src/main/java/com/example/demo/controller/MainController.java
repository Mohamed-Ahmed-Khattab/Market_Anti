package com.example.demo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private AnchorPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load dashboard by default
        loadView("dashboard-view.fxml");
    }

    @FXML
    void handleDashboard(ActionEvent event) {
        loadView("dashboard-view.fxml");
    }

    @FXML
    void handleProducts(ActionEvent event) {
        loadView("product-list.fxml");
    }

    @FXML
    void handleEmployees(ActionEvent event) {
        loadView("employee-list.fxml");
    }

    @FXML
    void handleCustomers(ActionEvent event) {
        loadView("customer-list.fxml");
    }

    @FXML
    void handleDepartments(ActionEvent event) {
        loadView("department-list.fxml");
    }

    @FXML
    void handleSuppliers(ActionEvent event) {
        loadView("supplier-list.fxml");
    }

    @FXML
    void handleMakeOrder(ActionEvent event) {
        loadView("cart-view.fxml");
    }

    @FXML
    void handleLowHealthProducts(ActionEvent event) {
        // Pass filter parameter normally or handle via controller initialization
        loadView("product-list.fxml");
    }

    @FXML
    void handleSalesReports(ActionEvent event) {
        // Placeholder for report view
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/" + fxmlFile));
            Parent view = loader.load();

            contentArea.getChildren().clear();

            // Anchor to all sides to fill the content area
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load view: " + fxmlFile);
        }
    }
}
