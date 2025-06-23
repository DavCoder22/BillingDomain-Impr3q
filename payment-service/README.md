# Payment Service

gRPC service for managing payment processing of quotations.

## Technical Stack

* **Framework:** Spring Boot 3 + gRPC starter (`yidongnan/grpc-spring-boot-starter`)
* **Protocol:** gRPC (protobuf)
* **Database:** PostgreSQL (schema `payment_db`)
* **Build Tool:** Maven

## API Definition

The gRPC service is defined in `src/main/proto/payment.proto`.

### Main Services

```protobuf
service PaymentService {
    rpc CreatePayment(CreatePaymentRequest) returns (PaymentResponse);
    rpc GetPaymentStatus(PaymentStatusRequest) returns (PaymentResponse);
}
```

## Local Development

### Prerequisites

* Java 17
* Maven 3.6+
* PostgreSQL
* Protocol Buffers compiler (protoc)

### Running the Service

1. Start the PostgreSQL database
2. Configure the database connection in `src/main/resources/application.yml`
3. Run the application:

```bash
mvn spring-boot:run
```

The service will expose the gRPC port configured in `application.yml` (default: 9090).

### Configuration

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

To run unit tests:

```bash
mvn test
```

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
