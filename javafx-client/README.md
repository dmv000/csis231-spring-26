# JavaFX Client Application

A JavaFX desktop application that connects to the Spring Boot REST API for managing Employees and Departments.

## Features

- **Employee Management**: Create, update, and view employees
- **Department Management**: Create, update, delete, and view departments
- **Relationship Management**: Assign employees to departments
- **Real-time Updates**: Refresh data from the API

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Spring Boot backend running on `http://localhost:8080`

## Running the Application

### Option 1: Using Maven

```bash
cd javafx-client
mvn clean javafx:run
```

### Option 2: Using Java directly

First, compile the project:
```bash
cd javafx-client
mvn clean compile
```

Then run:
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp target/classes:target/dependency/* com.csis231.javafxclient.JavaFXClientApp
```

## Project Structure

```
javafx-client/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/csis231/javafxclient/
│       │       ├── controller/      # FXML controllers
│       │       ├── model/           # DTOs matching backend
│       │       ├── service/          # API client
│       │       └── JavaFXClientApp.java
│       └── resources/
│           └── fxml/                 # FXML view files
└── pom.xml
```

## API Endpoints Used

- `GET /api/v1/employees` - List all employees
- `POST /api/v1/employees` - Create employee
- `PUT /api/v1/employees/{id}` - Update employee
- `GET /api/v1/departments` - List all departments
- `POST /api/v1/departments` - Create department
- `PUT /api/v1/departments/{id}` - Update department
- `DELETE /api/v1/departments/{id}` - Delete department

## Usage

1. **Start the Spring Boot backend** first (on port 8080)
2. **Launch the JavaFX client**
3. Use the **Employees** tab to manage employees
4. Use the **Departments** tab to manage departments
5. Select a department to see its employees
6. Assign employees to departments using the department dropdown in the Employees tab
