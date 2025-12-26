package com.example.demo.controller;

import com.example.demo.model.CartItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;

public class CartItemController implements Initializable {

    @FXML
    private TableView<CartItem> cartItemTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load data if applicable
    }
}
