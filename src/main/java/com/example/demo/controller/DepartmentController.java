package com.example.demo.controller;

import com.example.demo.dao.DepartmentDAO;
import com.example.demo.model.Department;
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
import java.util.List;
import java.util.ResourceBundle;

public class DepartmentController implements Initializable {

    @FXML
    private TableView<Department> departmentTable;
    @FXML
    private TableColumn<Department, String> nameColumn;
    @FXML
    private TableColumn<Department, String> locationColumn;
    @FXML
    private TableColumn<Department, Double> budgetColumn;
    @FXML
    private TextField searchField;

    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private ObservableList<Department> departmentList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        // No ID column in Model
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        budgetColumn.setCellValueFactory(new PropertyValueFactory<>("budget"));

        locationColumn.setCellValueFactory(cellData -> {
            List<String> locs = cellData.getValue().getLocation();
            if (locs != null && !locs.isEmpty()) {
                return new SimpleStringProperty(locs.get(0));
            }
            return new SimpleStringProperty("N/A");
        });
    }

    private void loadData() {
        departmentList.setAll(departmentDAO.getAll());
        departmentTable.setItems(departmentList);
    }

    @FXML
    void handleAdd(ActionEvent event) {
        showDepartmentForm(null);
    }

    @FXML
    void handleEdit(ActionEvent event) {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showDepartmentForm(selected);
        } else {
            AlertUtil.showWarning("No Selection", "Please select a department to edit.");
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Delete Department",
                    "Are you sure you want to delete " + selected.getName() + "?")) {
                if (departmentDAO.delete(selected.getDepartmentID())) {
                    loadData();
                    AlertUtil.showInfo("Success", "Department deleted successfully.");
                } else {
                    AlertUtil.showError("Error", "Could not delete department.");
                }
            }
        } else {
            AlertUtil.showWarning("No Selection", "Please select a department to delete.");
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            departmentTable.setItems(departmentList);
        } else {
            ObservableList<Department> filtered = FXCollections.observableArrayList();
            for (Department d : departmentList) {
                if (d.getName().toLowerCase().contains(keyword)) {
                    filtered.add(d);
                }
            }
            departmentTable.setItems(filtered);
        }
    }

    private void showDepartmentForm(Department department) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/department-form.fxml"));
            Parent root = loader.load();

            DepartmentFormController controller = (DepartmentFormController) loader.getController();
            controller.setDepartment(department);

            Stage stage = new Stage();
            stage.setTitle(department == null ? "Add Department" : "Edit Department");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadData();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not open department form.");
        }
    }
}
