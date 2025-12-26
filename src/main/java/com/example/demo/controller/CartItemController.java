package com.example.demo.controller;

import com.example.demo.dao.CartItemDAO;
import com.example.demo.model.CartItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;

public class CartItemController implements Initializable {

    @FXML
    private TableView<CartItem> cartItemTable;

    private final CartItemDAO cartItemDAO = new CartItemDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load data if applicable
    }
}
