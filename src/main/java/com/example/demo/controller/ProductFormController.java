package com.example.demo.controller;

import com.example.demo.dao.ProductDAO;
import com.example.demo.dao.SupplierDAO;
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

import java.math.BigDecimal;
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
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private Product currentProduct;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSuppliers();
    }

    private void loadSuppliers() {
        supplierCombo.setItems(FXCollections.observableArrayList(supplierDAO.getAll()));
        supplierCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Supplier supplier) {
                return supplier != null ? supplier.getSupplierName() : "";
            }

            @Override
            public Supplier fromString(String string) {
                return null; // Not needed
            }
        });
    }

    public void setProduct(Product product) {
        this.currentProduct = product;
        if (product != null) {
            nameField.setText(product.getProductName());
            descriptionArea.setText(product.getDescription());
            categoryField.setText(product.getCategory());
            priceField.setText(product.getPrice().toString());
            costField.setText(product.getCost() != null ? product.getCost().toString() : "");
            stockField.setText(String.valueOf(product.getStockQuantity()));
            reorderField.setText(String.valueOf(product.getReorderLevel()));
            barcodeField.setText(product.getBarcode());
            skuField.setText(product.getSku());

            if (product.getSupplierID() != null) {
                for (Supplier s : supplierCombo.getItems()) {
                    if (s.getSupplierID().equals(product.getSupplierID())) {
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
            if (isNew) {
                currentProduct = new Product();
            }

            currentProduct.setProductName(nameField.getText());
            currentProduct.setDescription(descriptionArea.getText());
            currentProduct.setCategory(categoryField.getText());
            currentProduct.setPrice(new BigDecimal(priceField.getText()));
            currentProduct.setCost(!costField.getText().isEmpty() ? new BigDecimal(costField.getText()) : null);
            currentProduct.setStockQuantity(Integer.parseInt(stockField.getText()));
            currentProduct.setReorderLevel(Integer.parseInt(reorderField.getText()));
            currentProduct.setBarcode(barcodeField.getText());
            currentProduct.setSku(skuField.getText());

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
                AlertUtil.showError("Error", "Could not save product.");
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
        return true;
    }

    private void closeWindow() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
