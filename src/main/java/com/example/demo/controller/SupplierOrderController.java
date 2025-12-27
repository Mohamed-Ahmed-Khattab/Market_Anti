package com.example.demo.controller;

import com.example.demo.dao.ProductDAO;
import com.example.demo.dao.SupplierDAO;
import com.example.demo.model.Product;
import com.example.demo.model.Supplier;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.CommonMethod;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for Supplier Order feature.
 * Allows placing orders to suppliers to add stock.
 * Uses threads to simulate employees processing tasks.
 */
public class SupplierOrderController implements Initializable {

    @FXML
    private ComboBox<Supplier> supplierComboBox;

    @FXML
    private ComboBox<Product> productComboBox;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSuppliers();
        setupTable();
        setupListeners();
    }

    private void setupTable() {
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        tblSupply.setItems(supplyList);
    }

    private void setupListeners() {
        // When Supplier changes, load their products
        supplierComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadProductsForSupplier(newVal.getSupplierID());
            } else {
                productComboBox.setItems(FXCollections.observableArrayList());
            }
        });

        // When Product changes, auto-fill Cost/Price
        productComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Use Cost as the supply price
                priceField.setText(String.valueOf(newVal.getCost()));
            } else {
                priceField.clear();
            }
        });

        // Setup Product ComboBox display
        productComboBox.setButtonCell(new javafx.scene.control.ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        productComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
    }

    private void loadSuppliers() {
        List<Supplier> suppliers = com.example.demo.model.CEO.checkSuppliers();
        ObservableList<Supplier> observableList = FXCollections.observableArrayList(suppliers);
        supplierComboBox.setItems(observableList);

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

    private void loadProductsForSupplier(int supplierID) {
        List<Product> products = productDAO.getProductsLinkedToSupplier(supplierID);
        productComboBox.setItems(FXCollections.observableArrayList(products));
    }

    @FXML
    void handleAddToList(ActionEvent event) {
        Product selectedProduct = productComboBox.getValue();
        String qtyText = quantityField.getText().trim();
        String priceText = priceField.getText().trim();

        if (selectedProduct == null || qtyText.isEmpty()) {
            AlertUtil.showWarning("Validation", "Please select a product and enter quantity.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyText);
            double price = Double.parseDouble(priceText); // Auto-filled, but parse it safely

            // Add to list
            supplyList.add(new SupplyItem(selectedProduct.getName(), qty, price));

            // Reset selection but keep supplier
            productComboBox.getSelectionModel().clearSelection();
            quantityField.clear();
            priceField.clear();
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Validation", "Invalid quantity format.");
        }
    }

    @FXML
    private javafx.scene.control.Button btnProcess;

    @FXML
    void handlePlaceOrder(ActionEvent event) {
        if (supplyList.isEmpty()) {
            AlertUtil.showWarning("Empty List", "No products added to the supply list.");
            return;
        }

        // Disable button to prevent multiple clicks
        btnProcess.setDisable(true);
        btnProcess.setText("Processing...");

        // Calculate total cost
        double totalCost = supplyList.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();

        // Check Budget
        if (!com.example.demo.model.CEO.approveBudget(totalCost)) {
            AlertUtil.showWarning("Budget Limit", "Not enough money based on last 30 days sales. Order Rejected.");
            btnProcess.setDisable(false);
            btnProcess.setText("Process All Supply Tasks");
            return;
        }

        // Start ONE background thread to process everything sequentially
        Thread mainWorker = new Thread(() -> {
            int successCount = 0;
            int total = supplyList.size();

            for (SupplyItem item : supplyList) {
                final String pName = item.getProductName();
                final int pQty = item.getQuantity();

                System.out.println("Processing: " + pName);

                try {
                    Thread.sleep(800);

                    // Update existing stock
                    Product existing = productDAO.getByName(pName);
                    boolean success = false;
                    if (existing != null) {
                        success = productDAO.updateStock(existing.getProductID(), pQty);
                    } else {
                        System.err.println(
                                "Error: Product " + pName + " not found in DB even though selected from list.");
                    }

                    if (success)
                        successCount++;

                } catch (Exception e) {
                    System.err.println("Error processing " + pName + ": " + e.getMessage());
                }
            }

            final int finalSuccessCount = successCount;
            javafx.application.Platform.runLater(() -> {
                btnProcess.setDisable(false);
                btnProcess.setText("Process All Supply Tasks");
                AlertUtil.showInfo("Process Complete",
                        String.format("Successfully processed %d out of %d items.", finalSuccessCount, total));
                supplyList.clear();
            });
        });

        mainWorker.setName("Supply-Worker-Sequential");
        mainWorker.start();
    }

    @FXML
    void handleClearList(ActionEvent event) {
        supplyList.clear();
    }

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
}
