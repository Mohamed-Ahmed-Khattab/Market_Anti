package com.example.demo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.example.demo.util.CommonMethod;

public class ReportController {

    @FXML
    void goBack(ActionEvent event) {

        CommonMethod.goToBack(event,getClass());

    }
}
