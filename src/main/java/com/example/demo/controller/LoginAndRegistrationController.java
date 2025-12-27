package com.example.demo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.example.demo.exception.UserEmailExsist;
import com.example.demo.exception.UserNotFound;
import com.example.demo.model.UserModel;
import com.example.demo.tm.User;
import com.example.demo.util.CommonMethod;
import com.example.demo.util.CustomAlertType;
import com.example.demo.util.SessionManager;
import com.example.demo.util.UserType;
import com.example.demo.dao.CustomerDAO;
import com.example.demo.model.Customer;

import java.io.IOException;

public class LoginAndRegistrationController {

    @FXML
    private Button backToLoginButton;

    @FXML
    private Button goToRegistrationButton;

    @FXML
    private AnchorPane imagePane;

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane loginPane;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField regFirstNameField1;

    @FXML
    private TextField regLastNameField11;

    @FXML
    private PasswordField regPassword;

    @FXML
    private PasswordField regReEnterPassword;

    @FXML
    private TextField regUsernameField;

    @FXML
    private AnchorPane registrationPane;

    @FXML
    private ComboBox<UserType> roleBox;

    @FXML
    private TextField usernameField;

    @FXML
    public void initialize() {

        goToRegistrationButton.setOnAction(event -> {
            loginPane.setVisible(false);
            registrationPane.setVisible(true);
        });

        backToLoginButton.setOnAction(event -> {
            registrationPane.setVisible(false);
            loginPane.setVisible(true);
        });

    }

    @FXML
    void login(ActionEvent event) {

        String getUsername = usernameField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();

        if (getUsername.isEmpty() || password.isEmpty()) {
            CommonMethod.showAlert("Login Error", "Please Enter User Name and Password", CustomAlertType.ERROR);
            return;
        }
        if (!getUsername.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            CommonMethod.showAlert("Invalid Email", "Please enter a valid email address.", CustomAlertType.WARNING);
            return;
        }
        try {
            User user = User.builder()
                    .email(getUsername).password(password).build();
            if (UserModel.login(user)) {
                SessionManager.setCurrentUser(UserModel.getUserCurrentUser());
                loadDashboard();
            }

        } catch (UserNotFound e) {
            CommonMethod.showAlert("Error", e.getMessage(), CustomAlertType.WARNING);
        }

    }

    private void loadDashboard() {
        com.example.demo.tm.User currentUser = SessionManager.getCurrentUser();
        String fxmlPath = "/com/example/demo/main-view.fxml";
        String title = "Market Management System";

        if (currentUser != null && currentUser.getUserType() == com.example.demo.util.UserType.CUSTOMER) {
            fxmlPath = "/com/example/demo/customer-view.fxml";
            title = "Customer Dashboard";
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

        try {
            Parent parent = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            CommonMethod.showAlert("Load Error", e.getMessage(), CustomAlertType.ERROR);
        }
    }

    @FXML
    void register(ActionEvent event) {

        String email = regUsernameField.getText();
        String password = regPassword.getText();
        String reEnterPassword = regReEnterPassword.getText();
        String firstName = regFirstNameField1.getText();
        String lastName = regLastNameField11.getText();

        if (email.isEmpty() || password.isEmpty() || reEnterPassword.isEmpty() ||
                firstName.isEmpty() || lastName.isEmpty()) {
            CommonMethod.showAlert("Form Error", "Please fill all fields.", CustomAlertType.WARNING);
            return;
        }
        if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            CommonMethod.showAlert("Invalid Email", "Please enter a valid email address.", CustomAlertType.ERROR);
            return;
        }
        if (!password.equals(reEnterPassword)) {
            CommonMethod.showAlert("Password Error", "Passwords do not match.", CustomAlertType.ERROR);
            return;
        }

        User user = com.example.demo.tm.Customer.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .userType(UserType.CUSTOMER)
                .build();
        System.out.println(user);
        try {
            UserModel.registerUser(user);

            // Create profile in Customer table
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customerModel = new Customer(
                    firstName + " " + lastName,
                    "Unknown",
                    "N/A", // Address placeholder
                    null,
                    false,
                    0.0,
                    password);
            customerDAO.create(customerModel);

            CommonMethod.showAlert("Success", "Registration Successful!", CustomAlertType.INFORMATION);

            // Auto login and redirect
            SessionManager.setCurrentUser(user);
            loadDashboard();
        } catch (UserEmailExsist e) {
            CommonMethod.showAlert("Email Error", e.getMessage(), CustomAlertType.ERROR);
        }

    }

    private void clearFields() {
        regUsernameField.clear();
        regPassword.clear();
        regReEnterPassword.clear();
        regFirstNameField1.clear();
        regLastNameField11.clear();
    }

    @FXML
    void showHidePassword(ActionEvent event) {
        if (((CheckBox) event.getSource()).isSelected()) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordTextField.setVisible(false);
        }
    }

}
