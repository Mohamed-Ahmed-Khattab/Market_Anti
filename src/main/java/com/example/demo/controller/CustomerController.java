package com.example.demo.controller;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.model.Customer;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.ValidationUtil;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> idColumn;
    @FXML
    private TableColumn<Customer, String> firstNameColumn;
    @FXML
    private TableColumn<Customer, String> lastNameColumn;
    @FXML
    private TableColumn<Customer, String> emailColumn;
    @FXML
    private TableColumn<Customer, String> phoneColumn;
    @FXML
    private TableColumn<Customer, Integer> loyaltyColumn;
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
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        loyaltyColumn.setCellValueFactory(new PropertyValueFactory<>("loyaltyPoints"));
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
                    "Are you sure you want to delete " + selected.getFullName() + "?")) {
                if (customerDAO.delete(selected.getCustomerID())) {
                    loadData();
                    AlertUtil.showInfo("Success", "Customer deleted successfully.");
                } else {
                    AlertUtil.showError("Error", "Could not delete customer.");
                }
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
                if (c.getFirstName().toLowerCase().contains(keyword) ||
                        c.getLastName().toLowerCase().contains(keyword) ||
                        c.getEmail().toLowerCase().contains(keyword) ||
                        c.getPhoneNumber().contains(keyword)) {
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
