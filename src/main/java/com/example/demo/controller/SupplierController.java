package com.example.demo.controller;

import com.example.demo.dao.SupplierDAO;
import com.example.demo.model.Supplier;
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
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class SupplierController implements Initializable {

    @FXML
    private TableView<Supplier> supplierTable;
    @FXML
    private TableColumn<Supplier, Integer> idColumn;
    @FXML
    private TableColumn<Supplier, String> nameColumn;
    @FXML
    private TableColumn<Supplier, String> contactColumn;
    @FXML
    private TableColumn<Supplier, String> emailColumn;
    @FXML
    private TableColumn<Supplier, String> phoneColumn;
    @FXML
    private TableColumn<Supplier, BigDecimal> ratingColumn;
    @FXML
    private TextField searchField;

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("supplierID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
    }

    private void loadData() {
        supplierList.setAll(supplierDAO.getAll());
        supplierTable.setItems(supplierList);
    }

    @FXML
    void handleAdd(ActionEvent event) {
        showSupplierForm(null);
    }

    @FXML
    void handleEdit(ActionEvent event) {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showSupplierForm(selected);
        } else {
            AlertUtil.showWarning("No Selection", "Please select a supplier to edit.");
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Delete Supplier",
                    "Are you sure you want to delete " + selected.getSupplierName() + "?")) {
                if (supplierDAO.delete(selected.getSupplierID())) {
                    loadData();
                    AlertUtil.showInfo("Success", "Supplier deleted successfully.");
                } else {
                    AlertUtil.showError("Error", "Could not delete supplier.");
                }
            }
        } else {
            AlertUtil.showWarning("No Selection", "Please select a supplier to delete.");
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            supplierTable.setItems(supplierList);
        } else {
            ObservableList<Supplier> filtered = FXCollections.observableArrayList();
            for (Supplier s : supplierList) {
                if (s.getSupplierName().toLowerCase().contains(keyword) ||
                        s.getContactPerson().toLowerCase().contains(keyword) ||
                        s.getEmail().toLowerCase().contains(keyword)) {
                    filtered.add(s);
                }
            }
            supplierTable.setItems(filtered);
        }
    }

    private void showSupplierForm(Supplier supplier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/supplier-form.fxml"));
            Parent root = loader.load();

            SupplierFormController controller = (SupplierFormController) loader.getController();
            controller.setSupplier(supplier);

            Stage stage = new Stage();
            stage.setTitle(supplier == null ? "Add Supplier" : "Edit Supplier");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadData();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not open supplier form.");
        }
    }
}
