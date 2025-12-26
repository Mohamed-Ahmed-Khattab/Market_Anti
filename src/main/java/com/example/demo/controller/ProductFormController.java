package com.example.demo.controller;

import com.example.demo.dao.ProductDAO;
import com.example.demo.model.Product;
import com.example.demo.model.Supplier;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductFormController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField costField;
    @FXML
    private TextField stockField;
    @FXML
    private TextField reorderField;
    @FXML
    private ComboBox<Supplier> supplierCombo;
    @FXML
    private TextField barcodeField;
    @FXML
    private TextField skuField;

    private final ProductDAO productDAO = new ProductDAO();
    private Product currentProduct;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Disable unsupported fields
        descriptionArea.setDisable(true);
        descriptionArea.setPromptText("Not supported by Model");
        categoryField.setDisable(true);
        categoryField.setPromptText("Not supported by Model");
        costField.setDisable(true);
        costField.setPromptText("Not supported by Model");
        reorderField.setDisable(true);
        reorderField.setPromptText("Not supported by Model");
        supplierCombo.setDisable(true);
        supplierCombo.setPromptText("Not supported by Model");
        barcodeField.setDisable(true);
        barcodeField.setPromptText("Not supported by Model");
        skuField.setDisable(true);
        skuField.setPromptText("Not supported by Model");
    }

    public void setProduct(Product product) {
        this.currentProduct = product;
        if (product != null) {
            nameField.setText(product.getName());
            priceField.setText(String.valueOf(product.getPrice()));
            stockField.setText(String.valueOf(product.getStockQuantity()));
            // Other fields ignored
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (validateInput()) {
            boolean isNew = (currentProduct == null);

            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            if (isNew) {
                // Constructor: productID, name, stockQuantity, price
                // ID 0 for new
                currentProduct = new Product(0, name, stock, price);
            } else {
                currentProduct.setName(name);
                currentProduct.setPrice(price);
                currentProduct.setStockQuantity(stock);
            }

            // Note: DAO handles defaults for missing fields like category, barcode etc.

            boolean success;
            if (isNew) {
                success = productDAO.create(currentProduct);
            } else {
                success = productDAO.update(currentProduct);
            }

            if (success) {
                AlertUtil.showInfo("Success", "Product saved successfully.");
                closeWindow();
            } else {
                AlertUtil.showError("Error", "Could not save product (DAO limitation).");
            }
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private boolean validateInput() {
        if (ValidationUtil.isEmpty(nameField.getText())) {
            AlertUtil.showWarning("Validation", "Product Name is required.");
            return false;
        }
        if (ValidationUtil.isEmpty(priceField.getText()) || !ValidationUtil.isPositiveNumber(priceField.getText())) {
            AlertUtil.showWarning("Validation", "Valid Price is required.");
            return false;
        }
        try {
            Integer.parseInt(stockField.getText());
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Validation", "Valid Stock required.");
            return false;
        }
        return true;
    }

    private void closeWindow() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
