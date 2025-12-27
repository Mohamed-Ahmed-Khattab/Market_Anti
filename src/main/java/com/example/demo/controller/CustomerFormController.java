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
        // All fields enabled
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        if (customer != null) {
            String[] parts = customer.getName().split(" ", 2);
            firstNameField.setText(parts.length > 0 ? parts[0] : "");
            lastNameField.setText(parts.length > 1 ? parts[1] : "");

            emailField.setText(customer.getEmail());
            phoneField.setText(customer.getPhoneNumber());
            addressField.setText(customer.getAddress());
            cityField.setText(customer.getCity());
            stateField.setText(customer.getState());
            zipField.setText(customer.getZipCode());
            countryField.setText(customer.getCountry());
            loyaltyField.setText(String.valueOf(customer.getLoyaltyPoints()));
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (validateInput()) {
            boolean isNew = (currentCustomer == null);

            String name = firstNameField.getText() + " " + lastNameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();
            String city = cityField.getText();
            String state = stateField.getText();
            String zip = zipField.getText();
            String country = countryField.getText();
            int loyalty = ValidationUtil.isEmpty(loyaltyField.getText()) ? 0 : Integer.parseInt(loyaltyField.getText());

            if (isNew) {
                currentCustomer = new Customer(name);
                currentCustomer.setPassword("customer123"); // Default password for new manual additions
            }

            currentCustomer.setName(name);
            currentCustomer.setEmail(email);
            currentCustomer.setPhoneNumber(phone);
            currentCustomer.setAddress(address);
            currentCustomer.setCity(city);
            currentCustomer.setState(state);
            currentCustomer.setZipCode(zip);
            currentCustomer.setCountry(country);
            currentCustomer.setLoyaltyPoints(loyalty);

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
