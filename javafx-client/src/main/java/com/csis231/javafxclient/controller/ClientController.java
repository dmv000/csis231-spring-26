package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.ClientDto;
import com.csis231.javafxclient.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ClientController {

    @FXML
    private TableView<ClientDto> clientTable;

    @FXML
    private TableColumn<ClientDto, Long> idColumn;

    @FXML
    private TableColumn<ClientDto, String> firstNameColumn;

    @FXML
    private TableColumn<ClientDto, String> lastNameColumn;

    @FXML
    private TableColumn<ClientDto, String> emailColumn;

    @FXML
    private TableColumn<ClientDto, String> phoneColumn;

    @FXML
    private TableColumn<ClientDto, String> addressColumn;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private Label statusLabel;

    private final ApiClient apiClient = new ApiClient();
    private final ObservableList<ClientDto> clientList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        loadClients();

        clientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selectedClient) -> {
            if (selectedClient != null) {
                firstNameField.setText(selectedClient.getFirstName());
                lastNameField.setText(selectedClient.getLastName());
                emailField.setText(selectedClient.getEmail());
                phoneField.setText(selectedClient.getPhone());
                addressField.setText(selectedClient.getAddress());
            }
        });
    }

    @FXML
    private void loadClients() {
        try {
            List<ClientDto> clients = apiClient.getAllClients();
            clientList.setAll(clients);
            clientTable.setItems(clientList);
            statusLabel.setText("Clients loaded successfully.");
        } catch (Exception e) {
            statusLabel.setText("Error loading clients: " + e.getMessage());
        }
    }

    @FXML
    private void createClient() {
        try {
            ClientDto client = new ClientDto();
            client.setFirstName(firstNameField.getText());
            client.setLastName(lastNameField.getText());
            client.setEmail(emailField.getText());
            client.setPhone(phoneField.getText());
            client.setAddress(addressField.getText());

            apiClient.createClient(client);
            loadClients();
            clearForm();
            statusLabel.setText("Client created successfully.");
        } catch (Exception e) {
            statusLabel.setText("Error creating client: " + e.getMessage());
        }
    }

    @FXML
    private void updateClient() {
        try {
            ClientDto selectedClient = clientTable.getSelectionModel().getSelectedItem();
            if (selectedClient == null) {
                statusLabel.setText("Select a client first.");
                return;
            }

            selectedClient.setFirstName(firstNameField.getText());
            selectedClient.setLastName(lastNameField.getText());
            selectedClient.setEmail(emailField.getText());
            selectedClient.setPhone(phoneField.getText());
            selectedClient.setAddress(addressField.getText());

            apiClient.updateClient(selectedClient.getId(), selectedClient);
            loadClients();
            clearForm();
            statusLabel.setText("Client updated successfully.");
        } catch (Exception e) {
            statusLabel.setText("Error updating client: " + e.getMessage());
        }
    }

    @FXML
    private void deleteClient() {
        try {
            ClientDto selectedClient = clientTable.getSelectionModel().getSelectedItem();
            if (selectedClient == null) {
                statusLabel.setText("Select a client first.");
                return;
            }

            apiClient.deleteClient(selectedClient.getId());
            loadClients();
            clearForm();
            statusLabel.setText("Client deleted successfully.");
        } catch (Exception e) {
            statusLabel.setText("Error deleting client: " + e.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        clientTable.getSelectionModel().clearSelection();
    }
}