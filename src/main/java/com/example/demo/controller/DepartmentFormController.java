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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
            nameField.setText(department.getName());

            if (department.getLocation() != null && !department.getLocation().isEmpty()) {
                locationField.setText(department.getLocation().get(0));
            }

            budgetField.setText(String.valueOf(department.getBudget()));
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (validateInput()) {
            boolean isNew = (currentDepartment == null);

            String name = nameField.getText();
            String location = locationField.getText();
            double budget = 0.0;
            try {
                budget = Double.parseDouble(budgetField.getText());
            } catch (NumberFormatException e) {
                // Ignore
            }

            if (isNew) {
                // Constructor: double budget, String name, String status
                currentDepartment = new Department(budget, name, "Active");
                List<String> locs = new ArrayList<>();
                locs.add(location);
                currentDepartment.setLocation(locs);
            } else {
                // Update existing department using setters
                currentDepartment.setName(name);
                currentDepartment.setBudget(budget);
                List<String> locs = new ArrayList<>();
                locs.add(location);
                currentDepartment.setLocation(locs);
                // departmentID is already set from the loaded department
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
