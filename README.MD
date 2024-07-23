# GitHub API Consumer

## Description
This is a Spring Boot application that interacts with the GitHub API to list all non-fork repositories of a user along with their branches and last commit SHA.

## Requirements
- Java 21
- Spring Boot 3

## How to Run
1. Clone the repository
2. Navigate to the project directory
3. Run `./run_app.sh`

## Endpoints
- `GET /api/v1/users/{username}/repos`: Lists all non-fork repositories of the given GitHub username.

## Error Handling
If the GitHub user does not exist, the API returns a 404 response with a JSON body:
```json
{
    "status": 404,
    "message": "{what_happened}"
}
```

## Dependencies
- Spring Web
- Spring Webflux
- Spring Boot DevTools (optional)
- Lombok
- Spring Boot Starter Test
- Mock Web Server