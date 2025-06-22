# Payment Service

Servicio gRPC que gestiona los pagos de las cotizaciones.

* **Framework:** Spring Boot 3 + gRPC starter (`yidongnan/grpc-spring-boot-starter`)
* **Protocolo:** gRPC (protobuf)
* **Base de datos:** PostgreSQL (schema `payment_db`)
* **Build:** Maven

## API
Definida en `src/main/proto/payment.proto`.

Servicios principales:
- `rpc CreatePayment(CreatePaymentRequest) returns (PaymentResponse)`
- `rpc GetPaymentStatus(PaymentStatusRequest) returns (PaymentResponse)`

## Ejecución local
```bash
mvn spring-boot:run
```

La app expondrá el puerto gRPC configurado en `application.yml` (por defecto 9090).
