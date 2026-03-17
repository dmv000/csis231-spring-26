package com.csis231.javafxclient.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    @FXML
    private BorderPane rootPane;
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

    private EmployeeController employeeController;
    private DepartmentController departmentController;
    private OrderController orderController;
    private ClientController clientController;

    @FXML
    public void initialize() {
        mainTabPane.setVisible(false);
        try {
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            BorderPane loginPane = loginLoader.load();
            rootPane.setCenter(loginPane);
            LoginController loginController = loginLoader.getController();

            loginController.setOnLoginSuccess(() -> loadMainTabs());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMainTabs() {
        rootPane.setCenter(mainTabPane);
        mainTabPane.setVisible(true);

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

        try {
            FXMLLoader orderLoader = new FXMLLoader(getClass().getResource("/fxml/order-view.fxml"));
            ordersTab.setContent(orderLoader.load());
            orderController = orderLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FXMLLoader clientLoader = new FXMLLoader(getClass().getResource("/fxml/client-view.fxml"));
            clientsTab.setContent(clientLoader.load());
            clientController = clientLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}