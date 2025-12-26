package com.example.demo.controller;

import com.example.demo.dao.CEODAO;
import com.example.demo.model.CEO;
import com.example.demo.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class CEOController implements Initializable {

    @FXML
    private Label ceoNameLabel;
    @FXML
    private TextField bonusField;

    private final CEODAO ceoDAO = new CEODAO();
    private CEO currentCEO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCEO();
    }

    private void loadCEO() {
        currentCEO = ceoDAO.getCEO();
        if (currentCEO != null) {
            ceoNameLabel.setText(currentCEO.getName());
            if (currentCEO.getBonus() != null) {
                bonusField.setText(currentCEO.getBonus().toString());
            }
        }
    }

    @FXML
    public void handleSave() {
        if (currentCEO != null) {
            // Update logic
            // currentCEO.setBonus(...)
            // ceoDAO.update(currentCEO);
            AlertUtil.showInfo("Info", "CEO Update Logic Placeholder");
        }
    }
}
