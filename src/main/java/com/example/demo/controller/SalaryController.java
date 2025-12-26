package com.example.demo.controller;

import com.example.demo.dao.SalaryDAO;
import com.example.demo.model.Salary;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;

public class SalaryController implements Initializable {

    @FXML
    private TableView<Salary> salaryTable;

    private final SalaryDAO salaryDAO = new SalaryDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        salaryTable.setItems(FXCollections.observableArrayList(salaryDAO.getAll()));
    }
}
