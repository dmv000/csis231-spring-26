# Authentication and Registration Flow

This document explains how authentication and registration were implemented in this project, across both the Spring Boot backend and the JavaFX desktop client.

## Overview

The authentication flow was added in two parts:

1. The Spring Boot API now supports user registration and login.
2. The JavaFX client now starts on an authentication screen and only reveals the management tabs after a successful login.

The implementation is intentionally simple and fits the current architecture of the project:

- Users are stored in the `users` table.
- Registration creates a new user after validation.
- Login checks the submitted credentials against the stored user.
- Passwords are stored as hashes, not plain text.
- The JavaFX app uses the API directly over HTTP.

This implementation provides an application-level authentication flow, but it does not yet enforce authorization on the CRUD endpoints such as employees, departments, or clients.

## Backend Implementation

The backend implementation is inside the `api-springboot` module.

### Main files involved

- [UserController.java](/Users/cbf/dev/edu/csis231-spring-26/api-springboot/src/main/java/com/csis231/springpostgrescrud/controller/UserController.java)
- [UserService.java](/Users/cbf/dev/edu/csis231-spring-26/api-springboot/src/main/java/com/csis231/springpostgrescrud/service/UserService.java)
- [UserServiceImpl.java](/Users/cbf/dev/edu/csis231-spring-26/api-springboot/src/main/java/com/csis231/springpostgrescrud/service/UserServiceImpl.java)
- [UserRepository.java](/Users/cbf/dev/edu/csis231-spring-26/api-springboot/src/main/java/com/csis231/springpostgrescrud/repository/UserRepository.java)
- [UserDto.java](/Users/cbf/dev/edu/csis231-spring-26/api-springboot/src/main/java/com/csis231/springpostgrescrud/dto/UserDto.java)
- [LoginDto.java](/Users/cbf/dev/edu/csis231-spring-26/api-springboot/src/main/java/com/csis231/springpostgrescrud/dto/LoginDto.java)
- [User.java](/Users/cbf/dev/edu/csis231-spring-26/api-springboot/src/main/java/com/csis231/springpostgrescrud/entity/User.java)
- [RestExceptionHandler.java](/Users/cbf/dev/edu/csis231-spring-26/api-springboot/src/main/java/com/csis231/springpostgrescrud/exeption/RestExceptionHandler.java)

### User entity

Users are persisted using the `User` entity:

- `id`
- `username`
- `email`
- `password`

The database already enforces uniqueness for:

- `username`
- `email`

### DTOs used

Two DTOs are used for auth:

- `UserDto`
  - Used for registration requests and user responses
  - Includes `id`, `username`, `email`, `password`
- `LoginDto`
  - Used for login requests
  - Includes `username`, `password`

`UserDto` is annotated so that null fields are not serialized in JSON responses. That is why `password` is not returned after registration or login.

### Endpoints

The authentication endpoints are under:

- `/api/v1/users/register`
- `/api/v1/users/login`

#### Register endpoint

Method:

```http
POST /api/v1/users/register
```

Expected request body:

```json
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "secret123"
}
```

Behavior:

- Validates that username is present
- Validates that email is present
- Validates that password is present
- Validates that password length is at least 6
- Checks that username is not already used
- Checks that email is not already used
- Hashes the password before saving
- Stores the new user in the database
- Returns the created user without the password field

Successful response:

```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com"
}
```

#### Login endpoint

Method:

```http
POST /api/v1/users/login
```

Expected request body:

```json
{
  "username": "alice",
  "password": "secret123"
}
```

Behavior:

- Accepts username or email in the `username` field
- Validates that identifier and password are present
- Looks up the user by username first, then by email
- Compares the submitted password with the stored hashed password
- Returns the authenticated user without the password field

Successful response:

```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com"
}
```

Failed response example:

```json
{
  "status": 400,
  "message": "Invalid username or password.",
  "timestamp": 1711274400000
}
```

### Service-layer logic

The main auth logic is implemented in `UserServiceImpl`.

#### Registration flow in the service

When `registerUser(UserDto userDto)` is called:

1. The input is validated.
2. Username and email are normalized using `trim()`.
3. The repository checks whether username or email already exists.
4. The password is hashed using SHA-256.
5. The user is saved using JPA.
6. The saved entity is mapped back to `UserDto`.

#### Login flow in the service

When `authenticateUser(LoginDto loginDto)` is called:

1. The login payload is validated.
2. The identifier is normalized using `trim()`.
3. The repository tries to find the user by username.
4. If not found, it tries to find the user by email.
5. The submitted password is hashed and compared with the stored value.
6. On success, the service returns the user as `UserDto`.
7. On failure, a `BadRequestException` is thrown.

### Password hashing

Passwords are hashed in `UserServiceImpl` before being stored.

Implementation details:

- Algorithm: `SHA-256`
- Stored format: `sha256:<hex-digest>`

Example stored value:

```text
sha256:fcf730b6d95236ecd3c9fc2d92d7b6b2bb061514961aec041d6c7a7192f592e4
```

There is also backward compatibility logic:

- If an old stored password does not start with `sha256:`, the code compares it as plain text.

That makes the transition safer for previously inserted records.

### Repository support

The repository now includes:

- `existsByUsernameIgnoreCase`
- `existsByEmailIgnoreCase`
- `findByUsernameIgnoreCase`
- `findByEmailIgnoreCase`

These methods support:

- case-insensitive duplicate checks
- login by username
- login by email

### Error handling

Validation and login failures use `BadRequestException`.

The global exception handler converts those exceptions into JSON responses with:

- HTTP status
- error message
- timestamp

This is important for the JavaFX client, because the client reads the `message` field and shows it to the user.

## JavaFX Client Implementation

The desktop-side implementation is inside the `javafx-client` module.

### Main files involved

- [MainController.java](/Users/cbf/dev/edu/csis231-spring-26/javafx-client/src/main/java/com/csis231/javafxclient/controller/MainController.java)
- [main.fxml](/Users/cbf/dev/edu/csis231-spring-26/javafx-client/src/main/resources/fxml/main.fxml)
- [ApiClient.java](/Users/cbf/dev/edu/csis231-spring-26/javafx-client/src/main/java/com/csis231/javafxclient/service/ApiClient.java)
- [UserDto.java](/Users/cbf/dev/edu/csis231-spring-26/javafx-client/src/main/java/com/csis231/javafxclient/model/UserDto.java)
- [LoginDto.java](/Users/cbf/dev/edu/csis231-spring-26/javafx-client/src/main/java/com/csis231/javafxclient/model/LoginDto.java)

### UI behavior

The application now opens with an authentication interface instead of immediately exposing all management tabs.

The main screen contains:

- a Login tab
- a Register tab
- a hidden `TabPane` for the main app

Initial state:

- auth area is visible
- management tabs are hidden
- user is shown as "Not signed in"

After successful login:

- auth area is hidden
- management tabs become visible
- header shows the logged-in user
- logout button appears

After logout:

- auth area is shown again
- management tabs are hidden again
- user display resets

### Login flow in the client

The login flow is handled in `MainController.handleLogin()`.

Steps:

1. Read username/email and password from the form.
2. Validate that the fields are not empty.
3. Create a background `Task<UserDto>`.
4. Call `apiClient.login(new LoginDto(...))`.
5. If successful:
   - show welcome message
   - hide auth pane
   - show main tabs
   - show current user in the top bar
6. If it fails:
   - read the error message
   - display it in the auth status label

The request runs in a background thread so the UI does not freeze.

### Registration flow in the client

The registration flow is handled in `MainController.handleRegister()`.

Steps:

1. Read username, email, password, and confirm password from the form.
2. Validate that required fields are present.
3. Validate that password and confirm password match.
4. Create a background `Task<UserDto>`.
5. Call `apiClient.registerUser(...)`.
6. If successful:
   - show success message
   - prefill the login username field
   - clear the register form
7. If it fails:
   - show the backend validation message in the status label

### Logout flow

Logout is currently client-side only.

When logout is clicked:

- auth screen is shown again
- main tabs are hidden
- current user label is reset
- password field is cleared

There is no token invalidation because the app does not yet use server-issued sessions or JWTs.

### API client changes

`ApiClient` now includes:

- `registerUser(UserDto user)`
- `login(LoginDto loginDto)`

It also includes helper methods for error handling:

- `buildApiException(...)`
- `extractErrorMessage(...)`

These methods allow the client to:

- read JSON error responses from the backend
- extract the `message` field
- show friendly validation messages in the UI

That means the user sees meaningful errors such as:

- `Username is already taken.`
- `Email is already registered.`
- `Invalid username or password.`
- `Password must be at least 6 characters long.`

## End-to-End Flow

### Registration sequence

1. User opens the JavaFX app.
2. User selects the Register tab.
3. User enters username, email, password, and confirmation.
4. JavaFX sends `POST /api/v1/users/register`.
5. Spring validates the request.
6. Spring hashes the password and stores the user.
7. Spring returns the created user without password.
8. JavaFX shows success feedback and prepares the login form.

### Login sequence

1. User enters username or email and password.
2. JavaFX sends `POST /api/v1/users/login`.
3. Spring looks up the matching user.
4. Spring verifies the password.
5. Spring returns the authenticated user without password.
6. JavaFX switches from auth mode to app mode.
7. The user can now access the management tabs.

## Testing

Backend integration tests were added in:

- [UserAuthIntegrationTest.java](/Users/cbf/dev/edu/csis231-spring-26/api-springboot/src/test/java/com/csis231/springpostgrescrud/UserAuthIntegrationTest.java)

Covered scenarios:

- successful registration and login
- duplicate username rejection
- invalid password rejection

Test support file:

- [org.mockito.plugins.MockMaker](/Users/cbf/dev/edu/csis231-spring-26/api-springboot/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker)

This test resource forces Mockito to use the subclass mock maker, which avoids JVM self-attach issues on the current local environment.

### Commands used to verify

Backend:

```bash
cd api-springboot
mvn -o test
```

Frontend:

```bash
cd javafx-client
mvn -q -DskipTests compile
```

## Design Decisions

### Why return `UserDto` from login instead of `boolean`

Returning a user object is more useful than returning only `true` or `false`.

It allows the client to:

- display the logged-in username
- confirm which account is active
- support future features such as profile display or role-based UI

### Why use a separate `LoginDto`

Login and registration are different operations.

Using a dedicated `LoginDto` makes the API cleaner because:

- login only needs identifier and password
- registration needs username, email, and password
- the request contract is easier to understand

### Why hash passwords

Storing plain-text passwords is unsafe.

Hashing improves security because:

- raw passwords are not stored directly
- database exposure is less damaging
- it becomes easier to evolve toward stronger security later

### Why the tabs are hidden in the client

The JavaFX app previously opened directly into the management UI.

Now the tabs are hidden until authentication succeeds so the user experience matches the new auth flow.

## Current Limitations

This implementation is functional, but intentionally simple.

Current limitations:

- No JWT or session token support
- No server-side route protection on employee/department/client endpoints
- No role-based authorization
- No password reset flow
- No email verification
- No persistent login state after restarting the app
- No logout API endpoint

## Suggested Next Improvements

If this project is extended further, the next good steps would be:

1. Protect backend CRUD endpoints using Spring Security.
2. Introduce JWT-based authentication or session-based authentication.
3. Add roles such as `ADMIN` and `USER`.
4. Store the authenticated state more formally in the JavaFX client.
5. Add registration validation for email format and stronger password rules.
6. Add password reset and change-password features.
7. Add audit logging for login attempts.

## Summary

The authentication feature was implemented by combining:

- Spring Boot endpoints for registration and login
- service-layer validation and password hashing
- repository methods for duplicate checks and lookup
- JavaFX login/register UI
- JavaFX HTTP integration with backend error handling
- integration tests for the main auth scenarios

The result is a complete working registration and login flow for the current project structure, with a clear path for future security improvements.
