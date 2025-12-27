package com.example.demo.controller;

import com.example.demo.dao.ProductDAO;
import com.example.demo.model.Product;
import com.example.demo.model.Supplier;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.ValidationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
        loadSuppliers();
    }

    private void loadSuppliers() {
        com.example.demo.dao.SupplierDAO supplierDAO = new com.example.demo.dao.SupplierDAO();
        supplierCombo.setItems(javafx.collections.FXCollections.observableArrayList(supplierDAO.getAll()));
    }

    public void setProduct(Product product) {
        this.currentProduct = product;
        if (product != null) {
            nameField.setText(product.getName());
            descriptionArea.setText(product.getDescription());
            categoryField.setText(product.getCategory());
            priceField.setText(String.valueOf(product.getPrice()));
            costField.setText(String.valueOf(product.getCost()));
            stockField.setText(String.valueOf(product.getStockQuantity()));
            reorderField.setText(String.valueOf(product.getReorderLevel()));
            barcodeField.setText(product.getBarcode());
            skuField.setText(product.getSku());

            if (product.getSupplierID() > 0) {
                for (Supplier s : supplierCombo.getItems()) {
                    if (s.getSupplierID() == product.getSupplierID()) {
                        supplierCombo.setValue(s);
                        break;
                    }
                }
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (validateInput()) {
            boolean isNew = (currentProduct == null);

            String name = nameField.getText();
            String description = descriptionArea.getText();
            String category = categoryField.getText();
            double price = Double.parseDouble(priceField.getText());
            double cost = ValidationUtil.isEmpty(costField.getText()) ? 0 : Double.parseDouble(costField.getText());
            int stock = Integer.parseInt(stockField.getText());
            int reorder = ValidationUtil.isEmpty(reorderField.getText()) ? 10
                    : Integer.parseInt(reorderField.getText());
            String barcode = barcodeField.getText();
            String sku = skuField.getText();

            if (isNew) {
                currentProduct = new Product();
            }

            currentProduct.setName(name);
            currentProduct.setDescription(description);
            currentProduct.setCategory(category);
            currentProduct.setPrice(price);
            currentProduct.setCost(cost);
            currentProduct.setStockQuantity(stock);
            currentProduct.setReorderLevel(reorder);
            currentProduct.setBarcode(barcode);
            currentProduct.setSku(sku);

            if (supplierCombo.getValue() != null) {
                currentProduct.setSupplierID(supplierCombo.getValue().getSupplierID());
            }

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
