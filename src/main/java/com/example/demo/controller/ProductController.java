package com.example.demo.controller;

import com.example.demo.dao.ProductDAO;
import com.example.demo.model.Product;
import com.example.demo.util.AlertUtil;
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
import java.net.URL;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Integer> stockColumn;

    // Removed Category, Supplier columns not in Model

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> categoryFilter; // Kept but might be unused if we can't filter

    private final ProductDAO productDAO = new ProductDAO();
    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
    }

    private void loadData() {
        productList.setAll(productDAO.getAll());
        productTable.setItems(productList);
    }

    public void showLowStockOnly() {
        productList.setAll(productDAO.getLowStock());
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
                    "Are you sure you want to delete " + selected.getName() + "?")) {
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
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            productTable.setItems(productList);
        } else {
            ObservableList<Product> filtered = FXCollections.observableArrayList();
            for (Product p : productList) {
                if (p.getName().toLowerCase().contains(keyword)) {
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
