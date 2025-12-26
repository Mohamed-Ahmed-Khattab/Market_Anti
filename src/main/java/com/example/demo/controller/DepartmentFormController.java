package com.example.demo.controller;

import com.example.demo.dao.DepartmentDAO;
import com.example.demo.model.Department;
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

public class DepartmentFormController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField budgetField;

    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private Department currentDepartment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setDepartment(Department department) {
        this.currentDepartment = department;
        if (department != null) {
            nameField.setText(department.getDepartmentName());
            locationField.setText(department.getLocation());
            budgetField.setText(department.getBudget() != null ? department.getBudget().toString() : "");
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (validateInput()) {
            boolean isNew = (currentDepartment == null);
            if (isNew) {
                currentDepartment = new Department();
            }

            currentDepartment.setDepartmentName(nameField.getText());
            currentDepartment.setLocation(locationField.getText());

            try {
                if (!budgetField.getText().isEmpty()) {
                    currentDepartment.setBudget(new BigDecimal(budgetField.getText()));
                }
            } catch (NumberFormatException e) {
                currentDepartment.setBudget(BigDecimal.ZERO);
            }

            boolean success;
            if (isNew) {
                success = departmentDAO.create(currentDepartment);
            } else {
                success = departmentDAO.update(currentDepartment);
            }

            if (success) {
                AlertUtil.showInfo("Success", "Department saved successfully.");
                closeWindow();
            } else {
                AlertUtil.showError("Error", "Could not save department.");
            }
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private boolean validateInput() {
        if (ValidationUtil.isEmpty(nameField.getText())) {
            AlertUtil.showWarning("Validation", "Department Name is required.");
            return false;
        }
        return true;
    }

    private void closeWindow() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
