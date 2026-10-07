# EDABIP - Users API (Dheepanraj)

Spring Boot + JDBC (JdbcTemplate) + SQL. Layers: Controller -> Service -> Repository.

## Run
    mvn spring-boot:run

App: http://localhost:8080 | Swagger UI: http://localhost:8080/swagger-ui.html | H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:edabip`, user `sa`)

## Day 1 endpoints
| Method | Path | Notes |
|---|---|---|
| GET | /api/users?search=&page=0&size=10 | paginated; searches name / email / user ID |
| GET | /api/users/stats | total, active, inactive, newThisMonth |
| GET | /api/users/{userId} | e.g. USR001 |
| POST | /api/users | body: name, email, role, status (optional, default ACTIVE) |

Roles: USER, ADMINISTRATOR, EDITOR, VIEWER, BUSINESS_ANALYST. Status: ACTIVE, INACTIVE.

Day 2: edit, delete. Day 3: Postman collection, ER diagram, full README.
