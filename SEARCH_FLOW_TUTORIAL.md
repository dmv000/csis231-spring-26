# Search Flow Tutorial

This guide explains how search works in this project from the JavaFX front end all the way to the Spring Boot backend.

The clearest example is the employee search flow, because it is fully wired in the UI and backend:

- Front end UI: `javafx-client/src/main/resources/fxml/employee.fxml`
- Front end controller: `javafx-client/src/main/java/com/csis231/javafxclient/controller/EmployeeController.java`
- Front end HTTP client: `javafx-client/src/main/java/com/csis231/javafxclient/service/ApiClient.java`
- Backend controller: `api-springboot/src/main/java/com/csis231/springpostgrescrud/controller/EmployeeController.java`
- Backend service: `api-springboot/src/main/java/com/csis231/springpostgrescrud/service/EmployeeServiceImpl.java`
- Backend repository: `api-springboot/src/main/java/com/csis231/springpostgrescrud/repository/EmployeeRepository.java`

There is also a very similar backend-only search flow for users, which is useful as a second example of the same idea.

## 1. What the user sees in the front end

The employee screen defines all search inputs in `employee.fxml`:

- `searchField` for the search text
- `sortFieldCombo` for sorting by `lastName`, `firstName`, `email`, or `id`
- `sortDirCombo` for ascending or descending order
- `pageSizeCombo` for page size
- `searchButton` to run the search
- `clearSearchButton` to reset the search
- `prevButton` and `nextButton` for pagination
- `pageLabel` to show the current page

So the search UI is not just a text box. It also controls:

- filtering
- sorting
- pagination

## 2. Where the flow starts in JavaFX

When the `EmployeeController` is initialized, it does three important things:

1. `setupSearchControls()` fills the combo boxes with default values.
2. `runEmployeeSearch()` loads the first page immediately.
3. `refreshDepartments()` loads department data for the form.

That means search is the default loading mechanism for employees, not a separate optional feature.

## 3. What happens when the user clicks Search

The `Search` button in FXML is connected to:

```java
onAction="#searchEmployees"
```

Inside `EmployeeController`, `searchEmployees()` does this:

```java
page = 0;
runEmployeeSearch();
```

Resetting `page` to `0` is important. It ensures a new search always starts from the first page instead of staying on whatever page the user was viewing before.

## 4. The main search method on the front end

The real work happens in `runEmployeeSearch()`.

It reads the current values from the UI:

- search text `q`
- sort field
- sort direction
- page size
- current page

Then it creates a JavaFX `Task<PagedResponseDto<EmployeeDto>>`:

```java
Task<PagedResponseDto<EmployeeDto>> task = new Task<>() {
    @Override
    protected PagedResponseDto<EmployeeDto> call() throws Exception {
        return apiClient.searchEmployees(q, page, size, sortField, sortDir);
    }
};
```

This is a nice design choice because network calls should not block the JavaFX UI thread. Instead:

- the request runs in a background thread
- the buttons are disabled while loading
- the table updates only after the request finishes

That makes the UI stay responsive.

## 5. The HTTP request built by the front end

`ApiClient.searchEmployees(...)` constructs a GET request like this:

```text
GET /api/v1/employees/search?q=alice&page=0&size=25&sortField=lastName&sortDir=asc
```

A few details here are worth noticing:

- the search text is URL-encoded with `URLEncoder`
- sort field and sort direction are also encoded
- default values are used if `sortField` or `sortDir` are null
- the endpoint returns a paged wrapper object, not a plain list

The base URL is hardcoded as:

```text
http://localhost:8080/api/v1
```

So the JavaFX client expects the Spring Boot app to be running locally on port `8080`.

## 6. The response shape used by both sides

The backend returns a `PagedResponseDto<T>`, and the JavaFX client has a matching class with the same fields:

- `items`
- `page`
- `size`
- `totalItems`
- `totalPages`
- `sort`

This is what allows the front end to update:

- the employee table with `items`
- the current page with `page`
- the next/previous button state with `totalPages`
- the status message with `totalItems`

This wrapper is a small but important part of the design because pagination metadata travels with the actual records.

## 7. How the JavaFX controller updates the screen

When the background task succeeds:

- `employeeList.setAll(...)` replaces the table contents
- `totalPages = resp.getTotalPages()` updates pagination state
- `page = resp.getPage()` syncs the controller with the backend
- `updatePagingControls()` refreshes button states and the page label
- `statusLabel` shows how many employees were loaded

When it fails:

- the exception message is shown in `statusLabel`
- the buttons are re-enabled

This means the front end keeps the search state local and treats the backend as the source of truth for page results.

## 8. How the backend receives the request

The request lands in:

`/api/v1/employees/search`

inside the Spring `EmployeeController`.

This controller accepts:

- `q`
- `page`
- `size`
- `sortField`
- `sortDir`

Before doing any database work, it validates them:

- `page` must be `>= 0`
- `size` must be between `1` and `100`
- `sortField` must be one of the allowed values
- `sortDir` must be `asc` or `desc`

This is a strong pattern because it stops invalid requests at the controller boundary.

## 9. How sorting and pagination are turned into Spring objects

After validation, the backend creates:

```java
PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, normalizedSortField));
```

This is one of the key handoff moments in the search flow.

The frontend sends plain query parameters.
The backend converts them into a Spring `PageRequest`.
That `PageRequest` is then passed through the service layer into the repository.

So the pagination and sort rules travel all the way down to the database query layer.

## 10. The service layer logic

`EmployeeServiceImpl.searchEmployees(...)` contains the main business rule for search:

- if `q` is null or blank, return `findAll(pageable)`
- otherwise run a filtered search

In other words:

- blank search means "show all employees"
- non-blank search means "search employees by matching text"

That makes the search endpoint useful both for initial page load and for filtered searches.

## 11. The repository query

The employee repository uses a Spring Data derived query method:

```java
findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(...)
```

This means the database search matches if the query text appears in:

- first name
- last name
- email

and it does so case-insensitively.

For example, a search for `ali` could match:

- `Alice`
- `Khalil`
- `ali@example.com`

This is a good beginner-friendly repository design because Spring generates the query from the method name, so there is no manual SQL here.

## 12. How the backend sends the result back

The repository returns a `Page<Employee>`.

The service maps that to `Page<EmployeeDto>`.

The controller then wraps the page into:

```java
new PagedResponseDto<>(
    resultPage.getContent(),
    resultPage.getNumber(),
    resultPage.getSize(),
    resultPage.getTotalElements(),
    resultPage.getTotalPages(),
    normalizedSortField + "," + direction.name().toLowerCase()
)
```

So the API response contains:

- the current page of employee DTOs
- total number of matching rows
- total number of pages
- current sort description

That is exactly the information the JavaFX screen needs to render the current search result set.

## 13. End-to-end summary in one sentence

The employee search flow is:

`FXML controls -> EmployeeController -> background Task -> ApiClient -> /api/v1/employees/search -> EmployeeController (Spring) -> EmployeeServiceImpl -> EmployeeRepository -> database -> paged JSON response -> JavaFX table update`

## 14. Interesting implementation details

### Search doubles as initial loading

The app does not use a separate paged "list employees" endpoint for the screen. Instead, `runEmployeeSearch()` is called during initialization, and a blank query returns all employees. That keeps the UI flow simple.

### The UI is responsive because of `Task`

This is one of the more important frontend decisions in the whole flow. If the HTTP call had been made directly on the JavaFX thread, the window could freeze during slow requests.

### Validation is centralized in the backend controller

The client sends values, but the backend decides what is valid. This is the safer direction because clients can be buggy or manipulated.

### Search supports sorting and paging in one endpoint

Instead of creating separate endpoints for:

- filtering
- sorting
- pagination

the backend combines them in a single search endpoint. That makes the frontend integration simpler.

### The employee search and user search use two different repository styles

Employee search uses a derived Spring Data method name.

User search uses a custom JPQL query:

```java
@Query("""
        select u
        from User u
        where lower(u.username) like lower(concat('%', :q, '%'))
           or lower(u.email) like lower(concat('%', :q, '%'))
        """)
```

That is interesting because the project already shows two valid ways to implement search:

- derived query methods
- explicit JPQL with `@Query`

## 15. User search as a reusable pattern

Even though the JavaFX UI currently highlights employee search, the backend user search follows almost the same structure:

- `UserController.searchUsers(...)`
- `UserServiceImpl.searchUsers(...)`
- `UserRepository.dynamicSearch(...)`

The main difference is the fields being searched:

- employee search: first name, last name, email
- user search: username, email

If you want to add a JavaFX user search screen later, you already have a solid backend example to follow.

## 16. Error handling

The backend uses `RestExceptionHandler` to convert exceptions into JSON error responses using `ErrorResponseDto`:

- `status`
- `message`
- `timestamp`

That means invalid search requests such as:

- negative page numbers
- unsupported sort fields
- invalid sort direction

can be returned in a structured way instead of crashing the server.

On the frontend side, those backend errors are surfaced through the exception message in `statusLabel`.

## 17. Things to keep in mind if you extend this feature

If you want to improve or extend search later, these are the most natural next steps:

- allow pressing Enter in `searchField` to trigger search
- debounce typing for live search
- add department-based filtering
- support searching by department name
- show backend validation errors more cleanly in the UI
- extract the shared paging/search pattern so users and employees can reuse more code

## 18. Quick trace example

Here is a concrete example for one request:

1. A user types `smith` in the employee search field.
2. The user clicks `Search`.
3. `EmployeeController.searchEmployees()` resets the page to `0`.
4. `runEmployeeSearch()` starts a background task.
5. `ApiClient.searchEmployees("smith", 0, 25, "lastName", "asc")` sends the HTTP request.
6. Spring receives `GET /api/v1/employees/search?q=smith&page=0&size=25&sortField=lastName&sortDir=asc`.
7. The backend validates the parameters and creates a `PageRequest`.
8. `EmployeeServiceImpl` trims `smith` and runs the repository search.
9. `EmployeeRepository` finds matching employees by first name, last name, or email.
10. The backend returns a paged JSON response.
11. The JavaFX controller updates the table and page label.

## 19. Final takeaway

The search implementation is clean because each layer has a focused job:

- FXML defines controls
- JavaFX controller manages UI state and async execution
- `ApiClient` handles HTTP
- Spring controller validates request parameters
- service layer decides blank-search vs filtered-search behavior
- repository performs the actual database search
- DTOs carry data safely between layers

That separation makes the flow easier to understand, debug, and extend.
