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
        // Init logic if needed
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        if (customer != null) {
            firstNameField.setText(customer.getFirstName());
            lastNameField.setText(customer.getLastName());
            emailField.setText(customer.getEmail());
            phoneField.setText(customer.getPhoneNumber());
            addressField.setText(customer.getAddress());
            cityField.setText(customer.getCity());
            stateField.setText(customer.getState());
            countryField.setText(customer.getCountry());
            zipField.setText(customer.getZipCode());
            loyaltyField.setText(String.valueOf(customer.getLoyaltyPoints()));
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (validateInput()) {
            boolean isNew = (currentCustomer == null);
            if (isNew) {
                currentCustomer = new Customer();
            }

            currentCustomer.setFirstName(firstNameField.getText());
            currentCustomer.setLastName(lastNameField.getText());
            currentCustomer.setEmail(emailField.getText());
            currentCustomer.setPhoneNumber(phoneField.getText());
            currentCustomer.setAddress(addressField.getText());
            currentCustomer.setCity(cityField.getText());
            currentCustomer.setState(stateField.getText());
            currentCustomer.setCountry(countryField.getText());
            currentCustomer.setZipCode(zipField.getText());

            try {
                int points = Integer.parseInt(loyaltyField.getText());
                currentCustomer.setLoyaltyPoints(points);
            } catch (NumberFormatException e) {
                currentCustomer.setLoyaltyPoints(0);
            }

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
                AlertUtil.showError("Error", "Could not save customer.");
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
        if (!ValidationUtil.isValidEmail(emailField.getText())) {
            AlertUtil.showWarning("Validation", "Valid Email is required.");
            return false;
        }
        return true;
    }

    private void closeWindow() {
        ((Stage) firstNameField.getScene().getWindow()).close();
    }
}
