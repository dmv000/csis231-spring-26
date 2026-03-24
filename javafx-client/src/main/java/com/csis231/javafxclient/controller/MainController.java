package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.LoginDto;
import com.csis231.javafxclient.model.UserDto;
import com.csis231.javafxclient.service.ApiClient;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
    @FXML
    private VBox authPane;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab employeesTab;
    @FXML
    private Tab departmentsTab;
    @FXML
    private Tab ordersTab;
    @FXML
    private Tab clientsTab;
    @FXML
    private TextField loginUsernameField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private TextField registerUsernameField;
    @FXML
    private TextField registerEmailField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private PasswordField registerConfirmPasswordField;
    @FXML
    private Label authStatusLabel;
    @FXML
    private Label userInfoLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    private EmployeeController employeeController;
    private DepartmentController departmentController;
    private ClientController clientController;
    private final ApiClient apiClient = new ApiClient();
    private boolean tabsLoaded;

    @FXML
    public void initialize() {
        showAuthenticationState();
    }

    private void loadMainTabs() {
        if (tabsLoaded) {
            return;
        }

        try {
            FXMLLoader employeeLoader = new FXMLLoader(getClass().getResource("/fxml/employee.fxml"));
            employeesTab.setContent(employeeLoader.load());
            employeeController = employeeLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FXMLLoader departmentLoader = new FXMLLoader(getClass().getResource("/fxml/department.fxml"));
            departmentsTab.setContent(departmentLoader.load());
            departmentController = departmentLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Label ordersPlaceholder = new Label("Orders view is not available yet.");
        ordersTab.setContent(new StackPane(ordersPlaceholder));

        try {
            FXMLLoader clientLoader = new FXMLLoader(getClass().getResource("/fxml/client-view.fxml"));
            clientsTab.setContent(clientLoader.load());
            clientController = clientLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tabsLoaded = true;
    }

    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText() == null ? "" : loginUsernameField.getText().trim();
        String password = loginPasswordField.getText();

        if (username.isEmpty() || password == null || password.isBlank()) {
            authStatusLabel.setText("Enter your username or email and password.");
            return;
        }

        runAuthTask(new Task<>() {
            @Override
            protected UserDto call() throws Exception {
                return apiClient.login(new LoginDto(username, password));
            }
        }, user -> {
            authStatusLabel.setText("Welcome back, " + user.getUsername() + ".");
            loginPasswordField.clear();
            showMainApp(user);
        });
    }

    @FXML
    private void handleRegister() {
        String username = valueOf(registerUsernameField);
        String email = valueOf(registerEmailField);
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password == null || password.isBlank()) {
            authStatusLabel.setText("Complete username, email, and password to create an account.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            authStatusLabel.setText("Passwords do not match.");
            return;
        }

        runAuthTask(new Task<>() {
            @Override
            protected UserDto call() throws Exception {
                return apiClient.registerUser(new UserDto(null, username, email, password));
            }
        }, user -> {
            authStatusLabel.setText("Registration successful. You can now sign in as " + user.getUsername() + ".");
            loginUsernameField.setText(user.getUsername());
            loginPasswordField.clear();
            registerUsernameField.clear();
            registerEmailField.clear();
            registerPasswordField.clear();
            registerConfirmPasswordField.clear();
        });
    }

    @FXML
    private void handleLogout() {
        showAuthenticationState();
        authStatusLabel.setText("You have been logged out.");
        loginPasswordField.clear();
    }

    private void showMainApp(UserDto user) {
        loadMainTabs();
        authPane.setManaged(false);
        authPane.setVisible(false);
        mainTabPane.setManaged(true);
        mainTabPane.setVisible(true);
        userInfoLabel.setText("Signed in as " + user.getUsername() + " (" + user.getEmail() + ")");
        logoutButton.setVisible(true);
        logoutButton.setManaged(true);
    }

    private void showAuthenticationState() {
        authPane.setManaged(true);
        authPane.setVisible(true);
        mainTabPane.setManaged(false);
        mainTabPane.setVisible(false);
        userInfoLabel.setText("Not signed in");
        logoutButton.setVisible(false);
        logoutButton.setManaged(false);
    }

    private void runAuthTask(Task<UserDto> task, java.util.function.Consumer<UserDto> onSuccess) {
        setAuthLoading(true);
        authStatusLabel.setText("");
        task.setOnSucceeded(event -> {
            setAuthLoading(false);
            onSuccess.accept(task.getValue());
        });
        task.setOnFailed(event -> {
            setAuthLoading(false);
            Throwable error = task.getException();
            authStatusLabel.setText(error == null ? "Authentication request failed." : error.getMessage());
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void setAuthLoading(boolean loading) {
        loginButton.setDisable(loading);
        registerButton.setDisable(loading);
        logoutButton.setDisable(loading);
    }

    private String valueOf(TextField textField) {
        return textField.getText() == null ? "" : textField.getText().trim();
    }
}
