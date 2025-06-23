# Quotation Service

REST service for calculating 3D printing quotes.

## Technical Stack

* **Framework:** Spring Boot 3 (Java 17)
* **Protocol:** HTTP/JSON (Spring Web)
* **Database:** PostgreSQL (schema `quotation_db`)
* **Build Tool:** Maven

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/quotes` | Calculates and saves a new quotation. |
| GET    | `/quotes/{id}` | Retrieves details of a specific quotation. |

## Local Development

### Prerequisites

* Java 17
* Maven 3.6+
* PostgreSQL

### Running the Service

1. Start the PostgreSQL database
2. Configure the database connection in `src/main/resources/application.yml`
3. Run the application:

```bash
mvn spring-boot:run
```

### Configuration

Database configuration is managed through `src/main/resources/application.yml`. You can override these settings using environment variables.

Example environment variables:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/quotation_db
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
```

## Building the Project

To build the project:

```bash
mvn clean package
```

## Testing

To run tests:

```bash
mvn test
```

## API Documentation

Once the service is running, you can access the API documentation at:

* [Swagger UI](http://localhost:8080/swagger-ui.html)
* [OpenAPI (v3)](http://localhost:8080/v3/api-docs)

## Contributing

Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.
