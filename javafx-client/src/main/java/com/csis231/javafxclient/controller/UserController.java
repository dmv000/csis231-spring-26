package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.UserDTO;
import com.csis231.javafxclient.service.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UserController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;
    @FXML
    private Button loginButton;
    @FXML
    private Button clearButton;

    @FXML
    private Label statusLabel;

    private final ApiClient apiClient;

    public UserController() {
        this.apiClient = new ApiClient();
    }

    @FXML
    public void initialize() {
        statusLabel.setText("Please enter your details.");
    }

    @FXML
    private void registerUser() {
        try {
            UserDTO user = new UserDTO();
            user.setUsername(usernameField.getText());
            user.setEmail(emailField.getText());
            user.setPassword(passwordField.getText());

            apiClient.registerUser(user);

            statusLabel.setText("User registered successfully!");
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void loginUser() {
    }

    @FXML
    private void clearForm() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
    }
}