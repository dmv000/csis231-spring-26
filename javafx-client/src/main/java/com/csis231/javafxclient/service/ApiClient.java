package com.csis231.javafxclient.service;

import com.csis231.javafxclient.model.DepartmentDto;
import com.csis231.javafxclient.model.EmployeeDto;
import com.csis231.javafxclient.model.PagedResponseDto;
import com.csis231.javafxclient.model.LoginDto;
import com.google.gson.Gson;
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

    // User endpoints
    public boolean authenticate(LoginDto login) throws IOException, InterruptedException {
        String json = gson.toJson(login);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Boolean.class);
        }
        if (response.statusCode() == 401) {
            return false;
        }
        throw new RuntimeException("Failed to authenticate: " + response.statusCode() + " - " + response.body());
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
}
