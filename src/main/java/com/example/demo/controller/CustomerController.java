package com.example.demo.controller;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.model.Customer;
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

public class CustomerController implements Initializable {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, String> idColumn; // Changed to String for SSN
    @FXML
    private TableColumn<Customer, String> nameColumn; // Merged First/Last Name

    // Removed Email, Phone, Loyalty columns as they don't exist in Model

    @FXML
    private TextField searchField;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        // Use SSN as the ID since Model doesn't have numeric ID
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ssn"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        // Remove other columns setup
    }

    private void loadData() {
        customerList.setAll(customerDAO.getAll());
        customerTable.setItems(customerList);
    }

    @FXML
    void handleAdd(ActionEvent event) {
        showCustomerForm(null);
    }

    @FXML
    void handleEdit(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showCustomerForm(selected);
        } else {
            AlertUtil.showWarning("No Selection", "Please select a customer to edit.");
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Delete Customer",
                    "Are you sure you want to delete " + selected.getName() + "?")) {
                // Warning: DAO.delete requires int ID, but Model doesn't have it.
                // We cannot delete reliably without ID.
                AlertUtil.showError("Error", "Deletion is not supported with the current Model (No ID field).");
                /*
                 * if (customerDAO.delete(selected.getCustomerID())) {
                 * loadData();
                 * AlertUtil.showInfo("Success", "Customer deleted successfully.");
                 * } else {
                 * AlertUtil.showError("Error", "Could not delete customer.");
                 * }
                 */
            }
        } else {
            AlertUtil.showWarning("No Selection", "Please select a customer to delete.");
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            customerTable.setItems(customerList);
        } else {
            ObservableList<Customer> filtered = FXCollections.observableArrayList();
            for (Customer c : customerList) {
                if (c.getName().toLowerCase().contains(keyword)) {
                    filtered.add(c);
                }
            }
            customerTable.setItems(filtered);
        }
    }

    private void showCustomerForm(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/customer-form.fxml"));
            Parent root = loader.load();

            CustomerFormController controller = (CustomerFormController) loader.getController();
            controller.setCustomer(customer);

            Stage stage = new Stage();
            stage.setTitle(customer == null ? "Add Customer" : "Edit Customer");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadData();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not open customer form.");
        }
    }
}
