package com.example.demo.controller;

import com.example.demo.dao.ProductDAO;
import com.example.demo.dao.SupplierDAO;
import com.example.demo.model.Product;
import com.example.demo.model.Supplier;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for Supplier Order feature.
 * Allows placing orders to suppliers to add stock.
 * If product exists, adds quantity to stock.
 * If product doesn't exist, creates new product with the supplier.
 */
public class SupplierOrderController implements Initializable {

    @FXML
    private ComboBox<Supplier> supplierComboBox;

    @FXML
    private TextField productNameField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSuppliers();
    }

    private void loadSuppliers() {
        List<Supplier> suppliers = supplierDAO.getAll();
        ObservableList<Supplier> observableList = FXCollections.observableArrayList(suppliers);
        supplierComboBox.setItems(observableList);

        // Set display to show supplier name
        supplierComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getSupplierName());
            }
        });
        supplierComboBox.setButtonCell(new javafx.scene.control.ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getSupplierName());
            }
        });
    }

    @FXML
    void handlePlaceOrder(ActionEvent event) {
        // Validate inputs
        Supplier selectedSupplier = supplierComboBox.getValue();
        String productName = productNameField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String priceText = priceField.getText().trim();

        if (selectedSupplier == null) {
            AlertUtil.showWarning("Validation", "Please select a supplier.");
            return;
        }

        if (productName.isEmpty()) {
            AlertUtil.showWarning("Validation", "Please enter a product name.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                AlertUtil.showWarning("Validation", "Quantity must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Validation", "Please enter a valid quantity.");
            return;
        }

        // Check if product exists
        Product existingProduct = productDAO.getByName(productName);

        if (existingProduct != null) {
            // Product exists - add quantity to stock
            boolean success = productDAO.updateStock(existingProduct.getProductID(), quantity);
            if (success) {
                AlertUtil.showInfo("Success",
                        "Added " + quantity + " units to existing product '" + productName + "'.\n" +
                                "New stock: " + (existingProduct.getStockQuantity() + quantity));
                clearFields();
            } else {
                AlertUtil.showError("Error", "Could not update stock.");
            }
        } else {
            // Product doesn't exist - create new product with supplier
            double price;
            try {
                price = Double.parseDouble(priceText);
                if (price <= 0) {
                    AlertUtil.showWarning("Validation", "Price must be a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                AlertUtil.showWarning("Validation", "Please enter a valid price for the new product.");
                return;
            }

            Product newProduct = new Product(0, productName, quantity, price);
            boolean success = productDAO.createWithSupplier(newProduct, selectedSupplier.getSupplierID());

            if (success) {
                AlertUtil.showInfo("Success",
                        "Created new product '" + productName + "' with " + quantity + " units.\n" +
                                "Supplier: " + selectedSupplier.getSupplierName());
                clearFields();
            } else {
                AlertUtil.showError("Error", "Could not create new product.");
            }
        }
    }

    private void clearFields() {
        productNameField.clear();
        quantityField.clear();
        priceField.clear();
        supplierComboBox.setValue(null);
    }

    @FXML
    void goBack(ActionEvent event) {
        CommonMethod.goToBack(event, getClass());
    }
}
