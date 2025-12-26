package com.example.demo.controller;

import com.example.demo.dao.SupplierDAO;
import com.example.demo.model.Supplier;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.ValidationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class SupplierFormController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField countryField;
    @FXML
    private TextField ratingField;

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private Supplier currentSupplier;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setSupplier(Supplier supplier) {
        this.currentSupplier = supplier;
        if (supplier != null) {
            nameField.setText(supplier.getSupplierName());
            contactField.setText(supplier.getContactPerson());
            emailField.setText(supplier.getEmail());
            phoneField.setText(supplier.getPhoneNumber());
            addressField.setText(supplier.getAddress());
            cityField.setText(supplier.getCity());
            countryField.setText(supplier.getCountry());
            ratingField.setText(supplier.getRating() != null ? supplier.getRating().toString() : "");
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (validateInput()) {
            boolean isNew = (currentSupplier == null);
            if (isNew) {
                currentSupplier = new Supplier();
            }

            currentSupplier.setSupplierName(nameField.getText());
            currentSupplier.setContactPerson(contactField.getText());
            currentSupplier.setEmail(emailField.getText());
            currentSupplier.setPhoneNumber(phoneField.getText());
            currentSupplier.setAddress(addressField.getText());
            currentSupplier.setCity(cityField.getText());
            currentSupplier.setCountry(countryField.getText());

            try {
                if (!ratingField.getText().isEmpty()) {
                    currentSupplier.setRating(new BigDecimal(ratingField.getText()));
                }
            } catch (NumberFormatException e) {
                currentSupplier.setRating(BigDecimal.ZERO);
            }

            boolean success;
            if (isNew) {
                success = supplierDAO.create(currentSupplier);
            } else {
                success = supplierDAO.update(currentSupplier);
            }

            if (success) {
                AlertUtil.showInfo("Success", "Supplier saved successfully.");
                closeWindow();
            } else {
                AlertUtil.showError("Error", "Could not save supplier.");
            }
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private boolean validateInput() {
        if (ValidationUtil.isEmpty(nameField.getText())) {
            AlertUtil.showWarning("Validation", "Supplier Name is required.");
            return false;
        }
        return true;
    }

    private void closeWindow() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
