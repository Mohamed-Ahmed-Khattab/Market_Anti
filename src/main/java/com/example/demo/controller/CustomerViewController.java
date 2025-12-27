package com.example.demo.controller;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.model.Customer;
import com.example.demo.tm.User;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomerViewController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getFirstName() + " " + user.getLastName() + "!");
        }
    }

    @FXML
    void handleEditProfile(ActionEvent event) {
        User user = SessionManager.getCurrentUser();
        if (user == null)
            return;

        try {
            // Need to get the actual Customer model from database using User email
            CustomerDAO customerDAO = new CustomerDAO();
            // Since User has email, we can find the customer (assuming names are unique for
            // now or adding a findByEmail to CustomerDAO)
            // Let's add a findByEmail to CustomerDAO if it doesn't exist.
            Customer customer = null;
            for (Customer c : customerDAO.getAll()) {
                if (c.getEmail() != null && c.getEmail().equalsIgnoreCase(user.getEmail())) {
                    customer = c;
                    break;
                }
            }

            if (customer == null) {
                AlertUtil.showError("Error", "Could not find customer profile for " + user.getEmail());
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/customer-form.fxml"));
            Parent root = loader.load();

            CustomerFormController controller = loader.getController();
            controller.setCustomer(customer);

            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh welcome message if name changed
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser != null) {
                welcomeLabel.setText("Welcome, " + currentUser.getFirstName() + " " + currentUser.getLastName() + "!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not load profile form.");
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            SessionManager.clearSession();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/demo/login-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
