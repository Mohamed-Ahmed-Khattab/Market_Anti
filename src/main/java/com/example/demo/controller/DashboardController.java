package com.example.demo.controller;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.dao.DepartmentDAO;
import com.example.demo.dao.EmployeeDAO;
import com.example.demo.dao.ProductDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label totalDepartmentsLabel;

    @FXML
    private Label totalEmployeesLabel;

    @FXML
    private Label totalCustomersLabel;

    @FXML
    private Label lowStockProductsLabel;

    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshDashboard();
    }

    public void refreshDashboard() {
        // Run in background thread if heavy
        totalDepartmentsLabel.setText(String.valueOf(departmentDAO.getAll().size()));
        totalEmployeesLabel.setText(String.valueOf(employeeDAO.getAll().size()));
        totalCustomersLabel.setText(String.valueOf(customerDAO.getAll().size()));
        lowStockProductsLabel.setText(String.valueOf(productDAO.getLowStock().size()));
    }
}
