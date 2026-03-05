package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.DepartmentDto;
import com.csis231.javafxclient.model.EmployeeDto;
import com.csis231.javafxclient.service.ApiClient;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class DepartmentController {
    @FXML
    private TableView<DepartmentDto> departmentTable;
    @FXML
    private TableColumn<DepartmentDto, Long> idColumn;
    @FXML
    private TableColumn<DepartmentDto, String> nameColumn;
    @FXML
    private TableColumn<DepartmentDto, String> locationColumn;
    @FXML
    private TableColumn<DepartmentDto, Integer> employeeCountColumn;

    @FXML
    private TableView<EmployeeDto> employeeTable;
    @FXML
    private TableColumn<EmployeeDto, String> empFirstNameColumn;
    @FXML
    private TableColumn<EmployeeDto, String> empLastNameColumn;
    @FXML
    private TableColumn<EmployeeDto, String> empEmailColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;

    @FXML
    private Button createButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Label statusLabel;

    private ApiClient apiClient;
    private ObservableList<DepartmentDto> departmentList;
    private ObservableList<EmployeeDto> employeeList;

    public DepartmentController() {
        this.apiClient = new ApiClient();
        this.departmentList = FXCollections.observableArrayList();
        this.employeeList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Setup department table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        employeeCountColumn.setCellValueFactory(cellData -> {
            DepartmentDto dept = cellData.getValue();
            int count = dept.getEmployees() != null ? dept.getEmployees().size() : 0;
            return new SimpleIntegerProperty(count).asObject();
        });

        // Setup employee table columns
        empFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        empLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        empEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        departmentTable.setItems(departmentList);
        employeeTable.setItems(employeeList);

        // Department selection listener
        departmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadDepartmentToForm(newSelection);
                loadDepartmentEmployees(newSelection);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                clearForm();
                employeeList.clear();
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        // Load data
        refreshDepartments();
    }

    @FXML
    private void createDepartment() {
        try {
            DepartmentDto department = new DepartmentDto();
            department.setName(nameField.getText());
            department.setLocation(locationField.getText());

            apiClient.createDepartment(department);
            statusLabel.setText("Department created successfully!");
            refreshDepartments();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void updateDepartment() {
        DepartmentDto selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select a department to update");
            return;
        }

        try {
            selected.setName(nameField.getText());
            selected.setLocation(locationField.getText());

            apiClient.updateDepartment(selected.getId(), selected);
            statusLabel.setText("Department updated successfully!");
            refreshDepartments();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteDepartment() {
        DepartmentDto selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select a department to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Department");
        alert.setContentText("Are you sure you want to delete department: " + selected.getName() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                apiClient.deleteDepartment(selected.getId());
                statusLabel.setText("Department deleted successfully!");
                refreshDepartments();
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void refreshDepartments() {
        try {
            List<DepartmentDto> departments = apiClient.getAllDepartments();
            departmentList.clear();
            departmentList.addAll(departments);
            statusLabel.setText("Departments loaded: " + departments.size());
        } catch (Exception e) {
            statusLabel.setText("Error loading departments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDepartmentToForm(DepartmentDto department) {
        nameField.setText(department.getName());
        locationField.setText(department.getLocation());
    }

    private void loadDepartmentEmployees(DepartmentDto department) {
        employeeList.clear();
        if (department.getEmployees() != null) {
            employeeList.addAll(department.getEmployees());
        }
    }

    private void clearForm() {
        nameField.clear();
        locationField.clear();
        departmentTable.getSelectionModel().clearSelection();
        employeeList.clear();
    }
}
