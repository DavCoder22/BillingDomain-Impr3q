# Payment Service

gRPC and REST service for managing payment processing of quotations with comprehensive test coverage and API documentation.

## 🚀 Features

- Process payments through gRPC and REST endpoints
- Comprehensive test coverage with JUnit 5 and Mockito
- Containerized with Docker
- Infrastructure as Code with Terraform
- CI/CD pipeline with GitHub Actions
- Interactive API documentation with Swagger UI
- OpenAPI 3.0 specification
- Request/Response validation
- Detailed error responses

## 🛠 Technical Stack

* **Framework:** Spring Boot 3 + gRPC starter (`yidongnan/grpc-spring-boot-starter`)
* **Protocol:** gRPC (protobuf) with REST API documentation
* **Database:** PostgreSQL (schema `payment_db`)
* **Build Tool:** Maven
* **Testing:** JUnit 5, Mockito, JaCoCo for coverage
* **Containerization:** Docker
* **Orchestration:** Docker Compose
* **Infrastructure as Code:** Terraform
* **CI/CD:** GitHub Actions
* **API Documentation:** SpringDoc OpenAPI

## API Definition

The gRPC service is defined in `src/main/proto/payment.proto`.

### Main Services

```protobuf
service PaymentService {
    rpc CreatePayment(CreatePaymentRequest) returns (PaymentResponse);
    rpc GetPaymentStatus(PaymentStatusRequest) returns (PaymentResponse);
}
```

## 📚 API Documentation

### gRPC API

The gRPC service is defined in `src/main/proto/payment.proto` and is available on port 9090 by default.

### REST API Documentation

The service provides a RESTful API that's automatically documented with Swagger UI and OpenAPI 3.0.

#### Accessing Documentation

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **OpenAPI YAML:** http://localhost:8080/v3/api-docs.yaml

#### Available Endpoints

##### 1. Payment Operations

- **POST /api/v1/payments**
  - **Description:** Process a new payment
  - **Request Body:** `CreatePaymentRequest`
  - **Response:** `PaymentResponse`
  - **Example Request:**
    ```json
    {
      "amount": 100.50,
      "currency": "USD",
      "customerId": "cust-12345",
      "orderId": "order-67890",
      "paymentMethod": "CREDIT_CARD"
    }
    ```

- **GET /api/v1/payments/{paymentId}**
  - **Description:** Get payment status by ID
  - **Path Parameters:** 
    - `paymentId` (required): The ID of the payment
  - **Response:** `PaymentResponse`

#### Error Responses

The API returns standard HTTP status codes along with detailed error messages:

- **400 Bad Request**: Invalid request parameters
- **404 Not Found**: Requested resource not found
- **500 Internal Server Error**: Server-side error

Example error response:
```json
{
  "timestamp": "2023-11-15T10:30:45.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid payment ID",
  "path": "/api/v1/payments/0"
}
```

### gRPC Service Definition

```protobuf
service PaymentService {
    rpc CreatePayment(CreatePaymentRequest) returns (PaymentResponse);
    rpc GetPaymentStatus(PaymentStatusRequest) returns (PaymentResponse);
}
```

## 🚀 Local Development

### Prerequisites

* Java 21
* Maven 3.9.5+
* Docker & Docker Compose
* Protocol Buffers compiler (protoc)
* Terraform (for infrastructure provisioning)

### 🐳 Running with Docker Compose

The easiest way to run the service locally is using Docker Compose, which will set up all required dependencies:

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database
- Redis cache
- RabbitMQ message broker
- Payment Service (on port 8080)

gRPC service will be available on port 9090

### 🛠 Building and Running Locally

1. **Build the project:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Access Swagger UI:**
   Open http://localhost:8080/swagger-ui.html in your browser

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PaymentGrpcServiceTest

# Run with coverage report
mvn jacoco:report
```

### Test Coverage

Test coverage reports are generated in `target/site/jacoco/` after running tests with coverage.

## 🔄 CI/CD Pipeline

The project includes a GitHub Actions workflow (`.github/workflows/ci-cd.yml`) that:
- Builds the application
- Runs tests
- Generates code coverage reports
- Builds and pushes Docker images
- Deploys infrastructure using Terraform

## 📦 Infrastructure

Infrastructure is managed with Terraform. See the `terraform/` directory for details.

### Deploying Infrastructure

```bash
cd terraform/
terraform init
terraform plan
terraform apply
```
- Payment Service (on port 9090)

### Manual Setup

1. Start the required services (PostgreSQL, Redis, RabbitMQ)
2. Configure the connections in `src/main/resources/application.yml`
3. Run the application:

```bash
mvn spring-boot:run
```

The service will expose the gRPC port configured in `application.yml` (default: 9090).

## CI/CD Pipeline

The project includes a GitHub Actions workflow (`.github/workflows/ci-cd.yml`) that automates:

1. **On every push/PR to main/develop branches:**
   - Build and test the application
   - Run code quality checks
   - Generate code coverage reports
   - Build and push Docker image to registry

2. **On release tags:**
   - Deploy to production environment
   - Apply infrastructure changes with Terraform

## Infrastructure as Code

The `infra/` directory contains Terraform configurations for provisioning the required infrastructure:

```
infra/
├── main.tf           # Main Terraform configuration
├── variables.tf      # Variable definitions
└── modules/          # Reusable modules
    ├── vpc/          # VPC and networking
    ├── ecs/          # ECS cluster and services
    └── rds/          # Database configuration
```

To apply infrastructure changes:

```bash
cd infra
terraform init
terraform plan
terraform apply
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SPRING_DATASOURCE_URL | Database connection URL | jdbc:postgresql://postgres:5432/payment_db |
| SPRING_DATASOURCE_USERNAME | Database username | print3d |
| SPRING_DATASOURCE_PASSWORD | Database password | print3d |
| SPRING_RABBITMQ_HOST | RabbitMQ host | rabbitmq |
| SPRING_REDIS_HOST | Redis host | redis |
| SERVER_PORT | gRPC server port | 9090 |

### Application Configuration

Database and gRPC settings can be configured in `src/main/resources/application.yml`:

```yaml
grpc:
  server:
    port: 9090

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/payment_db
    username: your_username
    password: your_password
```

### Building the Project

To build the project and generate gRPC stubs:

```bash
mvn clean compile
```

### Testing

### Unit Tests

Run the unit tests with:

```bash
mvn test
```

### Integration Tests

Integration tests require the test containers to be running:

```bash
mvn verify -Pintegration-tests
```

### Test Coverage

Code coverage reports are generated using JaCoCo and can be found in:
- HTML: `target/site/jacoco/index.html`
- XML: `target/site/jacoco/jacoco.xml`

## Docker

The service is containerized using a multi-stage Dockerfile that:
1. Uses a build stage with Maven to compile the application
2. Creates a minimal runtime image with JRE 17
3. Includes health checks and proper signal handling

### Building the Docker Image

```bash
docker build -t payment-service .
```

### Running the Container

```bash
docker run -p 9090:9090 payment-service
```

## Monitoring

The service includes Spring Boot Actuator endpoints for monitoring:
- Health: `/actuator/health`
- Metrics: `/actuator/metrics`
- Info: `/actuator/info`

## Troubleshooting

### Common Issues

1. **Port Conflicts**: Ensure ports 9090 (gRPC) and 8080 (Actuator) are available
2. **Database Connection**: Verify PostgreSQL is running and accessible
3. **gRPC Client**: Ensure the client is using the correct protobuf definitions

### Logs

Logs can be found in:
- Console output when running locally
- Docker logs when running in a container
- CloudWatch Logs when deployed to AWS

## gRPC Client Example

Here's how to create a gRPC client to interact with the service:

```java
ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
    .usePlaintext()
    .build();
PaymentServiceGrpc.PaymentServiceBlockingStub stub = 
    PaymentServiceGrpc.newBlockingStub(channel);

// Create payment request
CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
    .setQuotationId("123")
    .setAmount("100.00")
    .setCurrency("USD")
    .build();

// Call the service
PaymentResponse response = stub.createPayment(request);
```

## Monitoring

The service exposes gRPC health checks on the same port. You can use any gRPC health checking client to verify the service status.

## Contributing

Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.
