package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.DepartmentDto;
import com.csis231.javafxclient.model.EmployeeDto;
import com.csis231.javafxclient.model.PagedResponseDto;
import com.csis231.javafxclient.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

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
    private TextField searchField;
    @FXML
    private ComboBox<String> sortFieldCombo;
    @FXML
    private ComboBox<String> sortDirCombo;
    @FXML
    private ComboBox<Integer> pageSizeCombo;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearSearchButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label pageLabel;

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

    private int page = 0;
    private int totalPages = 0;

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
        setupSearchControls();
        runEmployeeSearch();
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
        clearSearch();
    }

    @FXML
    private void searchEmployees() {
        page = 0;
        runEmployeeSearch();
    }

    @FXML
    private void clearSearch() {
        if (searchField != null) {
            searchField.clear();
        }
        if (sortFieldCombo != null) {
            sortFieldCombo.getSelectionModel().select("lastName");
        }
        if (sortDirCombo != null) {
            sortDirCombo.getSelectionModel().select("asc");
        }
        if (pageSizeCombo != null) {
            pageSizeCombo.getSelectionModel().select(Integer.valueOf(25));
        }
        page = 0;
        runEmployeeSearch();
    }

    @FXML
    private void prevPage() {
        if (page > 0) {
            page--;
            runEmployeeSearch();
        }
    }

    @FXML
    private void nextPage() {
        if (page + 1 < totalPages) {
            page++;
            runEmployeeSearch();
        }
    }

    private void setupSearchControls() {
        if (sortFieldCombo != null) {
            sortFieldCombo.setItems(FXCollections.observableArrayList("lastName", "firstName", "email", "id"));
            sortFieldCombo.getSelectionModel().select("lastName");
        }
        if (sortDirCombo != null) {
            sortDirCombo.setItems(FXCollections.observableArrayList("asc", "desc"));
            sortDirCombo.getSelectionModel().select("asc");
        }
        if (pageSizeCombo != null) {
            pageSizeCombo.setItems(FXCollections.observableArrayList(10, 25, 50, 100));
            pageSizeCombo.getSelectionModel().select(Integer.valueOf(25));
        }
        updatePagingControls();
    }

    private void setSearchLoading(boolean loading) {
        if (searchButton != null) searchButton.setDisable(loading);
        if (clearSearchButton != null) clearSearchButton.setDisable(loading);
        if (prevButton != null) prevButton.setDisable(loading || page <= 0);
        if (nextButton != null) nextButton.setDisable(loading || page + 1 >= totalPages);
    }

    private void updatePagingControls() {
        String label = totalPages == 0 ? "Page 0 of 0" : "Page " + (page + 1) + " of " + totalPages;
        if (pageLabel != null) pageLabel.setText(label);
        if (prevButton != null) prevButton.setDisable(page <= 0);
        if (nextButton != null) nextButton.setDisable(page + 1 >= totalPages);
    }

    private void runEmployeeSearch() {
        String q = searchField == null ? "" : searchField.getText();
        String sortField = sortFieldCombo == null ? "lastName" : sortFieldCombo.getValue();
        String sortDir = sortDirCombo == null ? "asc" : sortDirCombo.getValue();
        Integer size = pageSizeCombo == null ? 25 : pageSizeCombo.getValue();

        Task<PagedResponseDto<EmployeeDto>> task = new Task<>() {
            @Override
            protected PagedResponseDto<EmployeeDto> call() throws Exception {
                return apiClient.searchEmployees(q, page, size, sortField, sortDir);
            }
        };

        setSearchLoading(true);
        statusLabel.setText("Loading employees...");

        task.setOnSucceeded(evt -> {
            PagedResponseDto<EmployeeDto> resp = task.getValue();
            List<EmployeeDto> newItems = resp.getItems();
            employeeList.setAll(newItems == null ? List.of() : newItems);

            totalPages = resp.getTotalPages();
            page = resp.getPage();
            updatePagingControls();
            statusLabel.setText("Employees loaded: " + employeeList.size() + " (total " + resp.getTotalItems() + ")");
            setSearchLoading(false);
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            statusLabel.setText("Error loading employees: " + (ex == null ? "unknown error" : ex.getMessage()));
            setSearchLoading(false);
        });

        Thread thread = new Thread(task, "employees-search");
        thread.setDaemon(true);
        thread.start();
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
