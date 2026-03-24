package com.csis231.javafxclient.service;

import com.csis231.javafxclient.model.DepartmentDto;
import com.csis231.javafxclient.model.EmployeeDto;
import com.csis231.javafxclient.model.ClientDto;
import com.csis231.javafxclient.model.LoginDto;
import com.csis231.javafxclient.model.PagedResponseDto;
import com.csis231.javafxclient.model.UserDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/v1";
    private final HttpClient httpClient;
    private final Gson gson;
    private String authToken;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public UserDto registerUser(UserDto user) throws IOException, InterruptedException {
        String json = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return gson.fromJson(response.body(), UserDto.class);
        }
        throw buildApiException("Failed to register user", response);
    }

    public UserDto login(LoginDto loginDto) throws IOException, InterruptedException {
        String json = gson.toJson(loginDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), UserDto.class);
        }
        throw buildApiException("Failed to login", response);
    }

    // Employee endpoints
    public List<EmployeeDto> getAllEmployees() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/employees"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<EmployeeDto>>() {}.getType());
        }
        throw new RuntimeException("Failed to fetch employees: " + response.statusCode());
    }

    public EmployeeDto getEmployeeById(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/employees/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), EmployeeDto.class);
        }
        throw new RuntimeException("Failed to fetch employee: " + response.statusCode());
    }

    public EmployeeDto createEmployee(EmployeeDto employee) throws IOException, InterruptedException {
        String json = gson.toJson(employee);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/employees"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return gson.fromJson(response.body(), EmployeeDto.class);
        }
        throw new RuntimeException("Failed to create employee: " + response.statusCode() + " - " + response.body());
    }

    public EmployeeDto updateEmployee(Long id, EmployeeDto employee) throws IOException, InterruptedException {
        String json = gson.toJson(employee);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/employees/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), EmployeeDto.class);
        }
        throw new RuntimeException("Failed to update employee: " + response.statusCode());
    }

    public PagedResponseDto<EmployeeDto> searchEmployees(String q, int page, int size, String sortField, String sortDir)
            throws IOException, InterruptedException {
        String encodedQ = q == null ? "" : URLEncoder.encode(q, StandardCharsets.UTF_8);
        String encodedSortField = sortField == null ? "lastName" : URLEncoder.encode(sortField, StandardCharsets.UTF_8);
        String encodedSortDir = sortDir == null ? "asc" : URLEncoder.encode(sortDir, StandardCharsets.UTF_8);

        String url = BASE_URL + "/employees/search"
                + "?q=" + encodedQ
                + "&page=" + page
                + "&size=" + size
                + "&sortField=" + encodedSortField
                + "&sortDir=" + encodedSortDir;

        System.out.println(url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<PagedResponseDto<EmployeeDto>>() {}.getType());
        }
        throw new RuntimeException("Failed to search employees: " + response.statusCode() + " - " + response.body());
    }

    // Department endpoints
    public List<DepartmentDto> getAllDepartments() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/departments"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<DepartmentDto>>() {}.getType());
        }
        throw new RuntimeException("Failed to fetch departments: " + response.statusCode());
    }

    public DepartmentDto getDepartmentById(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/departments/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), DepartmentDto.class);
        }
        throw new RuntimeException("Failed to fetch department: " + response.statusCode());
    }

    public DepartmentDto createDepartment(DepartmentDto department) throws IOException, InterruptedException {
        String json = gson.toJson(department);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/departments"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return gson.fromJson(response.body(), DepartmentDto.class);
        }
        throw new RuntimeException("Failed to create department: " + response.statusCode() + " - " + response.body());
    }

    public DepartmentDto updateDepartment(Long id, DepartmentDto department) throws IOException, InterruptedException {
        String json = gson.toJson(department);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/departments/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), DepartmentDto.class);
        }
        throw new RuntimeException("Failed to update department: " + response.statusCode());
    }

    public void deleteDepartment(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/departments/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) {
            throw new RuntimeException("Failed to delete department: " + response.statusCode());
        }
    }

        // Client endpoints
        public List<ClientDto> getAllClients() throws IOException, InterruptedException {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/clients"))
                    .GET()
                    .build();
    
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<ClientDto>>() {}.getType());
            }
            throw new RuntimeException("Failed to fetch clients: " + response.statusCode());
        }
    
        public ClientDto getClientById(Long id) throws IOException, InterruptedException {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/clients/" + id))
                    .GET()
                    .build();
    
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), ClientDto.class);
            }
            throw new RuntimeException("Failed to fetch client: " + response.statusCode());
        }
    
        public ClientDto createClient(ClientDto client) throws IOException, InterruptedException {
            String json = gson.toJson(client);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/clients"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
    
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                return gson.fromJson(response.body(), ClientDto.class);
            }
            throw new RuntimeException("Failed to create client: " + response.statusCode() + " - " + response.body());
        }
    
        public ClientDto updateClient(Long id, ClientDto client) throws IOException, InterruptedException {
            String json = gson.toJson(client);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/clients/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
    
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), ClientDto.class);
            }
            throw new RuntimeException("Failed to update client: " + response.statusCode() + " - " + response.body());
        }
    
        public void deleteClient(Long id) throws IOException, InterruptedException {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/clients/" + id))
                    .DELETE()
                    .build();
    
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to delete client: " + response.statusCode() + " - " + response.body());
        }
        }

    private RuntimeException buildApiException(String defaultMessage, HttpResponse<String> response) {
        String message = extractErrorMessage(response.body());
        if (message != null && !message.isBlank()) {
            return new RuntimeException(message);
        }
        return new RuntimeException(defaultMessage + ": " + response.statusCode());
    }

    private String extractErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }
        try {
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                return jsonObject.get("message").getAsString();
            }
        } catch (Exception ignored) {
            return responseBody;
        }
        return responseBody;
    }
}
