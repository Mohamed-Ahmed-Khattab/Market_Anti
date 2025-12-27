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
        Object controller = loadView("product-list.fxml");
        if (controller instanceof ProductController) {
            ((ProductController) controller).showLowStockOnly();
        }
    }

    @FXML
    void handleSalesReports(ActionEvent event) {
        loadView("report-view.fxml");
    }

    @FXML
    void handleSupply(ActionEvent event) {
        loadView("supplier-order.fxml");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            com.example.demo.util.SessionManager.clearSession();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/demo/login-view.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) contentArea.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object loadView(String fxmlFile) {
        try {
            java.net.URL resource = getClass().getResource("/com/example/demo/" + fxmlFile);
            if (resource == null) {
                throw new IOException("FXML file not found: /com/example/demo/" + fxmlFile);
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            contentArea.getChildren().clear();

            // Anchor to all sides to fill the content area
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            contentArea.getChildren().add(view);
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setHeaderText("Could not load view: " + fxmlFile);
            alert.setContentText(
                    e.getMessage() != null ? e.getMessage() : "Unknown error occurred during FXML loading.");
            alert.showAndWait();
            return null;
        }
    }
}
