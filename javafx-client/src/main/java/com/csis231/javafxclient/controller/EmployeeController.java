package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.DepartmentDto;
import com.csis231.javafxclient.model.EmployeeDto;
import com.csis231.javafxclient.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeController {
    @FXML
    private TableView<EmployeeDto> employeeTable;
    @FXML
    private TableColumn<EmployeeDto, Long> idColumn;
    @FXML
    private TableColumn<EmployeeDto, String> firstNameColumn;
    @FXML
    private TableColumn<EmployeeDto, String> lastNameColumn;
    @FXML
    private TableColumn<EmployeeDto, String> emailColumn;
    @FXML
    private TableColumn<EmployeeDto, String> departmentColumn;

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<DepartmentDto> departmentComboBox;

    @FXML
    private Button createButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Label statusLabel;

    private ApiClient apiClient;
    private ObservableList<EmployeeDto> employeeList;
    private ObservableList<DepartmentDto> departmentList;

    public EmployeeController() {
        this.apiClient = new ApiClient();
        this.employeeList = FXCollections.observableArrayList();
        this.departmentList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Setup table columns
        // data binding
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));

        employeeTable.setItems(employeeList);
        departmentComboBox.setItems(departmentList);

        // Setup cell factory for department combo box
        departmentComboBox.setCellFactory(param -> new ListCell<DepartmentDto>() {
            @Override
            protected void updateItem(DepartmentDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        departmentComboBox.setButtonCell(new ListCell<DepartmentDto>() {
            @Override
            protected void updateItem(DepartmentDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        // Load data
        refreshEmployees();
        refreshDepartments();

        // Table selection listener
        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadEmployeeToForm(newSelection);
                updateButton.setDisable(false);
            } else {
                clearForm();
                updateButton.setDisable(true);
            }
        });
    }

    @FXML
    private void createEmployee() {
        try {
            EmployeeDto employee = new EmployeeDto();
            employee.setFirstName(firstNameField.getText());
            employee.setLastName(lastNameField.getText());
            employee.setEmail(emailField.getText());
            
            DepartmentDto selectedDept = departmentComboBox.getSelectionModel().getSelectedItem();
            if (selectedDept != null) {
                employee.setDepartmentId(selectedDept.getId());
            }

            apiClient.createEmployee(employee);
            statusLabel.setText("Employee created successfully!");
            refreshEmployees();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void updateEmployee() {
        EmployeeDto selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select an employee to update");
            return;
        }

        try {
            selected.setFirstName(firstNameField.getText());
            selected.setLastName(lastNameField.getText());
            selected.setEmail(emailField.getText());
            
            DepartmentDto selectedDept = departmentComboBox.getSelectionModel().getSelectedItem();
            if (selectedDept != null) {
                selected.setDepartmentId(selectedDept.getId());
            } else {
                selected.setDepartmentId(null);
            }

            apiClient.updateEmployee(selected.getId(), selected);
            statusLabel.setText("Employee updated successfully!");
            refreshEmployees();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshEmployees() {
        try {
            List<EmployeeDto> employees = apiClient.getAllEmployees();
            employeeList.clear();
            employeeList.addAll(employees);
            statusLabel.setText("Employees loaded: " + employees.size());
        } catch (Exception e) {
            statusLabel.setText("Error loading employees: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshDepartments() {
        try {
            List<DepartmentDto> departments = apiClient.getAllDepartments();
            departmentList.clear();
            departmentList.addAll(departments);
        } catch (Exception e) {
            statusLabel.setText("Error loading departments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadEmployeeToForm(EmployeeDto employee) {
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        emailField.setText(employee.getEmail());
        
        if (employee.getDepartmentId() != null) {
            DepartmentDto dept = departmentList.stream()
                    .filter(d -> d.getId().equals(employee.getDepartmentId()))
                    .findFirst()
                    .orElse(null);
            departmentComboBox.getSelectionModel().select(dept);
        } else {
            departmentComboBox.getSelectionModel().clearSelection();
        }
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        departmentComboBox.getSelectionModel().clearSelection();
        employeeTable.getSelectionModel().clearSelection();
    }
}
