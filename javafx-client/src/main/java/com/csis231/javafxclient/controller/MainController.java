package com.csis231.javafxclient.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class MainController {
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab employeesTab;
    @FXML
    private Tab departmentsTab;

    private EmployeeController employeeController;
    private DepartmentController departmentController;

    @FXML
    public void initialize() {
        // Load employee tab content
        try {
            FXMLLoader employeeLoader = new FXMLLoader(getClass().getResource("/fxml/employee.fxml"));
            employeesTab.setContent(employeeLoader.load());
            employeeController = employeeLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load department tab content
        try {
            FXMLLoader departmentLoader = new FXMLLoader(getClass().getResource("/fxml/department.fxml"));
            departmentsTab.setContent(departmentLoader.load());
            departmentController = departmentLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
