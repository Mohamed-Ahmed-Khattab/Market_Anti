package com.example.demo.controller;

import com.example.demo.dao.DepartmentDAO;
import com.example.demo.dao.EmployeeDAO;
import com.example.demo.dao.ManagerDAO;
import com.example.demo.model.Department;
import com.example.demo.model.Employee;
import com.example.demo.model.Manager;
import com.example.demo.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty;
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

public class EmployeeController implements Initializable {

    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, Integer> idColumn;
    @FXML
    private TableColumn<Employee, String> firstNameColumn;
    @FXML
    private TableColumn<Employee, String> lastNameColumn;
    @FXML
    private TableColumn<Employee, String> emailColumn;
    @FXML
    private TableColumn<Employee, String> deptColumn;
    @FXML
    private TableColumn<Employee, String> positionColumn;
    @FXML
    private TextField searchField;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final ManagerDAO managerDAO = new ManagerDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        deptColumn.setCellValueFactory(cellData -> {
            Integer deptId = cellData.getValue().getDepartmentID();
            if (deptId != null) {
                Department dept = departmentDAO.getById(deptId);
                return new SimpleStringProperty(dept != null ? dept.getDepartmentName() : "Unknown");
            }
            return new SimpleStringProperty("-");
        });
    }

    private void loadData() {
        employeeList.setAll(employeeDAO.getAll());
        employeeTable.setItems(employeeList);
    }

    @FXML
    void handleAdd(ActionEvent event) {
        showEmployeeForm(null);
    }

    @FXML
    void handleEdit(ActionEvent event) {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showEmployeeForm(selected);
        } else {
            AlertUtil.showWarning("No Selection", "Please select an employee to edit.");
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Delete Employee",
                    "Are you sure you want to delete " + selected.getFullName() + "?")) {
                if (employeeDAO.delete(selected.getEmployeeID())) {
                    loadData();
                    AlertUtil.showInfo("Success", "Employee deleted successfully.");
                } else {
                    AlertUtil.showError("Error", "Could not delete employee.");
                }
            }
        } else {
            AlertUtil.showWarning("No Selection", "Please select an employee to delete.");
        }
    }

    @FXML
    void handlePromote(ActionEvent event) {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String currentPos = selected.getPosition() != null ? selected.getPosition() : "Entry Level";
            if (AlertUtil.showConfirmation("Promote Employee",
                    "Current Position: " + currentPos + "\nPromote " + selected.getFullName() + "?")) {

                if (com.example.demo.model.CEO.promoteEmployee(selected)) {
                    loadData();
                    AlertUtil.showInfo("Success", "Employee promoted successfully.");
                }
            }
        } else {
            AlertUtil.showWarning("No Selection", "Please select an employee to promote.");
        }
    }

    @FXML
    void handleDemote(ActionEvent event) {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String currentPos = selected.getPosition() != null ? selected.getPosition() : "Entry Level";
            if (AlertUtil.showConfirmation("Demote Employee",
                    "Current Position: " + currentPos + "\nDemote " + selected.getFullName() + "?")) {

                if (com.example.demo.model.CEO.demoteEmployee(selected)) {
                    loadData();
                    AlertUtil.showInfo("Success", "Employee demoted successfully.");
                }
            }
        } else {
            AlertUtil.showWarning("No Selection", "Please select an employee to demote.");
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            employeeTable.setItems(employeeList);
        } else {
            ObservableList<Employee> filtered = FXCollections.observableArrayList();
            for (Employee e : employeeList) {
                if (e.getFirstName().toLowerCase().contains(keyword) ||
                        e.getLastName().toLowerCase().contains(keyword) ||
                        e.getEmail().toLowerCase().contains(keyword)) {
                    filtered.add(e);
                }
            }
            employeeTable.setItems(filtered);
        }
    }

    private void showEmployeeForm(Employee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/employee-form.fxml"));
            Parent root = loader.load();

            EmployeeFormController controller = (EmployeeFormController) loader.getController();
            controller.setEmployee(employee);

            Stage stage = new Stage();
            stage.setTitle(employee == null ? "Add Employee" : "Edit Employee");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadData();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not open employee form.");
        }
    }
}
