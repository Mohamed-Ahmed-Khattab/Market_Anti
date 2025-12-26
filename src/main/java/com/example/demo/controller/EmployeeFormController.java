package com.example.demo.controller;

import com.example.demo.dao.DepartmentDAO;
import com.example.demo.dao.EmployeeDAO;
import com.example.demo.model.Department;
import com.example.demo.model.Employee;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EmployeeFormController implements Initializable {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private DatePicker hireDatePicker;
    @FXML
    private TextField positionField;
    @FXML
    private ComboBox<Department> departmentCombo;
    @FXML
    private CheckBox managerCheckbox;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private Employee currentEmployee;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDepartments();
        hireDatePicker.setValue(LocalDate.now());
    }

    private void loadDepartments() {
        departmentCombo.setItems(FXCollections.observableArrayList(departmentDAO.getAll()));
        departmentCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Department dept) {
                return dept != null ? dept.getDepartmentName() : "";
            }

            @Override
            public Department fromString(String string) {
                return null;
            }
        });
    }

    public void setEmployee(Employee employee) {
        this.currentEmployee = employee;
        if (employee != null) {
            firstNameField.setText(employee.getFirstName());
            lastNameField.setText(employee.getLastName());
            emailField.setText(employee.getEmail());
            phoneField.setText(employee.getPhoneNumber());
            hireDatePicker.setValue(employee.getHireDate());
            positionField.setText(employee.getPosition());
            managerCheckbox.setSelected(Boolean.TRUE.equals(employee.getIsManager()));

            if (employee.getDepartmentID() != null) {
                for (Department d : departmentCombo.getItems()) {
                    if (d.getDepartmentID() == employee.getDepartmentID()) {
                        departmentCombo.setValue(d);
                        break;
                    }
                }
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (validateInput()) {
            boolean isNew = (currentEmployee == null);
            if (isNew) {
                currentEmployee = new Employee();
            }

            currentEmployee.setFirstName(firstNameField.getText());
            currentEmployee.setLastName(lastNameField.getText());
            currentEmployee.setEmail(emailField.getText());
            currentEmployee.setPhoneNumber(phoneField.getText());
            currentEmployee.setHireDate(hireDatePicker.getValue());
            currentEmployee.setPosition(positionField.getText());
            currentEmployee.setIsManager(managerCheckbox.isSelected());

            if (departmentCombo.getValue() != null) {
                currentEmployee.setDepartmentID(departmentCombo.getValue().getDepartmentID());
            } else {
                currentEmployee.setDepartmentID(null);
            }

            boolean success;
            if (isNew) {
                success = employeeDAO.create(currentEmployee);
            } else {
                success = employeeDAO.update(currentEmployee);
            }

            if (success) {
                AlertUtil.showInfo("Success", "Employee saved successfully.");
                closeWindow();
            } else {
                AlertUtil.showError("Error", "Could not save employee.");
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
