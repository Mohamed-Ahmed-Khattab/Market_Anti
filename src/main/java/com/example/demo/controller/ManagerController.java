package com.example.demo.controller;

import com.example.demo.dao.ManagerDAO;
import com.example.demo.model.Manager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagerController implements Initializable {

    @FXML
    private TableView<Manager> managerTable;

    private final ManagerDAO managerDAO = new ManagerDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        managerTable.setItems(FXCollections.observableArrayList(managerDAO.getAll()));
    }
}
