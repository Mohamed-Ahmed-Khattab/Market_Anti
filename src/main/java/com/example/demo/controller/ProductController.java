package com.example.demo.controller;

import com.example.demo.dao.ProductDAO;
import com.example.demo.dao.SupplierDAO;
import com.example.demo.model.Product;
import com.example.demo.model.Supplier;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.ValidationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, BigDecimal> priceColumn;
    @FXML
    private TableColumn<Product, Integer> stockColumn;
    @FXML
    private TableColumn<Product, String> supplierColumn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> categoryFilter;

    private final ProductDAO productDAO = new ProductDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        supplierColumn.setCellValueFactory(cellData -> {
            Integer supplierId = cellData.getValue().getSupplierID();
            if (supplierId != null) {
                Supplier supplier = supplierDAO.getById(supplierId);
                return new SimpleStringProperty(supplier != null ? supplier.getSupplierName() : "Unknown");
            }
            return new SimpleStringProperty("-");
        });
    }

    private void loadData() {
        productList.setAll(productDAO.getAll());
        productTable.setItems(productList);
    }

    @FXML
    void handleAdd(ActionEvent event) {
        showProductForm(null);
    }

    @FXML
    void handleEdit(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showProductForm(selected);
        } else {
            AlertUtil.showWarning("No Selection", "Please select a product to edit.");
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Delete Product",
                    "Are you sure you want to delete " + selected.getProductName() + "?")) {
                if (productDAO.delete(selected.getProductID())) {
                    loadData();
                    AlertUtil.showInfo("Success", "Product deleted successfully.");
                } else {
                    AlertUtil.showError("Error", "Could not delete product.");
                }
            }
        } else {
            AlertUtil.showWarning("No Selection", "Please select a product to delete.");
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        // Simple search implementation
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            productTable.setItems(productList);
        } else {
            ObservableList<Product> filtered = FXCollections.observableArrayList();
            for (Product p : productList) {
                if (p.getProductName().toLowerCase().contains(keyword) ||
                        p.getCategory().toLowerCase().contains(keyword) ||
                        p.getSku().toLowerCase().contains(keyword)) {
                    filtered.add(p);
                }
            }
            productTable.setItems(filtered);
        }
    }

    private void showProductForm(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/product-form.fxml"));
            Parent root = loader.load();

            ProductFormController controller = (ProductFormController) loader.getController();
            controller.setProduct(product);

            Stage stage = new Stage();
            stage.setTitle(product == null ? "Add Product" : "Edit Product");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadData(); // Refresh after close

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not open product form.");
        }
    }
}
