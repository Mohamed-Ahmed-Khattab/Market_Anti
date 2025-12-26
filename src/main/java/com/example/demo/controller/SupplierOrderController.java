package com.example.demo.controller;

import com.example.demo.dao.ProductDAO;
import com.example.demo.dao.SupplierDAO;
import com.example.demo.model.Product;
import com.example.demo.model.Supplier;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.CommonMethod;
<<<<<<< HEAD
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
=======
>>>>>>> bdef750f8e7c2fa3a4db403d7c8aa264e9ab3cb6
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
<<<<<<< HEAD
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
=======
import javafx.scene.control.TextField;
>>>>>>> bdef750f8e7c2fa3a4db403d7c8aa264e9ab3cb6

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for Supplier Order feature.
 * Allows placing orders to suppliers to add stock.
<<<<<<< HEAD
 * Uses threads to simulate employees processing tasks.
=======
 * If product exists, adds quantity to stock.
 * If product doesn't exist, creates new product with the supplier.
>>>>>>> bdef750f8e7c2fa3a4db403d7c8aa264e9ab3cb6
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

<<<<<<< HEAD
    @FXML
    private TableView<SupplyItem> tblSupply;

    @FXML
    private TableColumn<SupplyItem, String> colProduct;

    @FXML
    private TableColumn<SupplyItem, Integer> colQuantity;

    @FXML
    private TableColumn<SupplyItem, Double> colPrice;

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final ObservableList<SupplyItem> supplyList = FXCollections.observableArrayList();
=======
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final ProductDAO productDAO = new ProductDAO();
>>>>>>> bdef750f8e7c2fa3a4db403d7c8aa264e9ab3cb6

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSuppliers();
<<<<<<< HEAD
        setupTable();
    }

    private void setupTable() {
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        tblSupply.setItems(supplyList);
=======
>>>>>>> bdef750f8e7c2fa3a4db403d7c8aa264e9ab3cb6
    }

    private void loadSuppliers() {
        List<Supplier> suppliers = supplierDAO.getAll();
        ObservableList<Supplier> observableList = FXCollections.observableArrayList(suppliers);
        supplierComboBox.setItems(observableList);

<<<<<<< HEAD
=======
        // Set display to show supplier name
>>>>>>> bdef750f8e7c2fa3a4db403d7c8aa264e9ab3cb6
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
<<<<<<< HEAD
    void handleAddToList(ActionEvent event) {
        String name = productNameField.getText().trim();
        String qtyText = quantityField.getText().trim();
        String priceText = priceField.getText().trim();

        if (name.isEmpty() || qtyText.isEmpty()) {
            AlertUtil.showWarning("Validation", "Please enter product name and quantity.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyText);
            double price = priceText.isEmpty() ? 0.0 : Double.parseDouble(priceText);
            supplyList.add(new SupplyItem(name, qty, price));

            productNameField.clear();
            quantityField.clear();
            priceField.clear();
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Validation", "Invalid quantity or price format.");
        }
    }

    @FXML
    void handlePlaceOrder(ActionEvent event) {
        Supplier selectedSupplier = supplierComboBox.getValue();
        if (selectedSupplier == null) {
            AlertUtil.showWarning("Validation", "Please select a supplier first.");
            return;
        }

        if (supplyList.isEmpty()) {
            AlertUtil.showWarning("Empty List", "No products added to the supply list.");
            return;
        }

        // Manager assigns task to employees (threads)
        for (SupplyItem item : supplyList) {
            final String pName = item.getProductName();
            final int pQty = item.getQuantity();
            final double pPrice = item.getPrice();
            final int sId = selectedSupplier.getSupplierID();

            Thread employeeThread = new Thread(() -> {
                System.out.println("Employee Thread " + Thread.currentThread().getName() + " processing " + pName);

                try {
                    // Simulate processing time (employees work on tasks)
                    Thread.sleep(1500);

                    Product existing = productDAO.getByName(pName);
                    boolean success;
                    if (existing != null) {
                        success = productDAO.updateStock(existing.getProductID(), pQty);
                    } else {
                        Product newP = new Product(0, pName, pQty, pPrice);
                        success = productDAO.createWithSupplier(newP, sId);
                    }

                    if (success) {
                        System.out.println("Successfully processed supply for: " + pName);

                        // Update the supplier's multivalued attribute string in DB
                        Supplier current = supplierDAO.getById(sId);
                        if (current != null) {
                            String currentProds = current.getProductsAttribute();
                            if (currentProds == null || currentProds.isEmpty()) {
                                currentProds = pName;
                            } else if (!currentProds.contains(pName)) {
                                currentProds += ", " + pName;
                            }
                            current.setProductsAttribute(currentProds);
                            supplierDAO.update(current);
                        }

                        javafx.application.Platform.runLater(() -> {
                            AlertUtil.showInfo("Task Completed", "Employee " + Thread.currentThread().getName() +
                                    " has finished supplying " + pName);
                        });
                    } else {
                        System.err.println("Failed to process supply for: " + pName);
                    }
                } catch (InterruptedException e) {
                    System.err.println("Employee thread interrupted: " + e.getMessage());
                }
            });

            employeeThread.setName("Employee-" + pName.replace(" ", ""));
            employeeThread.start();
        }

        AlertUtil.showInfo("Manager Assignment",
                "The manager has assigned " + supplyList.size()
                        + " tasks to employees.\nEmployees are now working in parallel.");
        handleClearList(event);
    }

    @FXML
    void handleClearList(ActionEvent event) {
        supplyList.clear();
=======
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
>>>>>>> bdef750f8e7c2fa3a4db403d7c8aa264e9ab3cb6
    }

    @FXML
    void goBack(ActionEvent event) {
        CommonMethod.goToBack(event, getClass());
    }
<<<<<<< HEAD

    public static class SupplyItem {
        private final SimpleStringProperty productName;
        private final SimpleIntegerProperty quantity;
        private final SimpleDoubleProperty price;

        public SupplyItem(String productName, int quantity, double price) {
            this.productName = new SimpleStringProperty(productName);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.price = new SimpleDoubleProperty(price);
        }

        public String getProductName() {
            return productName.get();
        }

        public int getQuantity() {
            return quantity.get();
        }

        public double getPrice() {
            return price.get();
        }
    }
=======
>>>>>>> bdef750f8e7c2fa3a4db403d7c8aa264e9ab3cb6
}
