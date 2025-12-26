package com.example.demo.controller;

import com.example.demo.dao.EndorsementDAO;
import com.example.demo.model.Endorsement;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;

public class EndorsementController implements Initializable {

    @FXML
    private TableView<Endorsement> endorsementTable;

    private final EndorsementDAO endorsementDAO = new EndorsementDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        endorsementTable.setItems(FXCollections.observableArrayList(endorsementDAO.getAll()));
    }
}
