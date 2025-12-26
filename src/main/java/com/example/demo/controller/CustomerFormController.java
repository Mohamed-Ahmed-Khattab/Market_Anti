package com.example.demo.controller;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.model.Customer;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.ValidationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerFormController implements Initializable {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField stateField;
    @FXML
    private TextField countryField;
    @FXML
    private TextField zipField;
    @FXML
    private TextField loyaltyField;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private Customer currentCustomer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Disable fields not supported by the simplified Model
        emailField.setDisable(true);
        emailField.setPromptText("Not supported by Model");
        phoneField.setDisable(true);
        phoneField.setPromptText("Not supported by Model");
        cityField.setDisable(true);
        cityField.setPromptText("Not supported by Model");
        stateField.setDisable(true);
        stateField.setPromptText("Not supported by Model");
        countryField.setDisable(true);
        countryField.setPromptText("Not supported by Model");
        zipField.setDisable(true);
        zipField.setPromptText("Not supported by Model");
        loyaltyField.setDisable(true);
        loyaltyField.setPromptText("Not supported by Model");
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        if (customer != null) {
            // Split name into first/last for display
            String[] parts = customer.getName().split(" ", 2);
            firstNameField.setText(parts.length > 0 ? parts[0] : "");
            lastNameField.setText(parts.length > 1 ? parts[1] : "");

            addressField.setText(customer.getAddress());

            // Other fields remain empty/disabled
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (validateInput()) {
            boolean isNew = (currentCustomer == null);

            String name = firstNameField.getText() + " " + lastNameField.getText();
            String address = addressField.getText();

            if (isNew) {
                // Constructor: Name, Gender, Address, DOB, isPremium, Balance
                currentCustomer = new Customer(name, "Unknown", address, null, false, 0.0);
            } else {
                currentCustomer.setName(name);
                currentCustomer.setAddress(address);
            }

            // Note: email/phone etc are ignored as Model doesn't store them.

            boolean success;
            if (isNew) {
                success = customerDAO.create(currentCustomer);
            } else {
                success = customerDAO.update(currentCustomer);
            }

            if (success) {
                AlertUtil.showInfo("Success", "Customer saved successfully.");
                closeWindow();
            } else {
                AlertUtil.showError("Error", "Could not save customer (DAO implementation limitation).");
            }
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private boolean validateInput() {
        if (ValidationUtil.isEmpty(firstNameField.getText()) || ValidationUtil.isEmpty(lastNameField.getText())) {
            AlertUtil.showWarning("Validation", "Name is required.");
            return false;
        }
        return true;
    }

    private void closeWindow() {
        ((Stage) firstNameField.getScene().getWindow()).close();
    }
}
