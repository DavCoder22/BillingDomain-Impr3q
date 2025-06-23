# Invoice Service

SOAP service for generating and managing invoices for paid quotations.

## Technical Stack

* **Framework:** Spring Boot 3 + Apache CXF (or Spring-WS)
* **Protocol:** SOAP 1.2 / XML
* **Database:** PostgreSQL (schema `invoice_db`)
* **Build Tool:** Maven

## Service Contract (WSDL)

The service contract is defined in `src/main/resources/wsdl/InvoiceService.wsdl`.

### Available Operations

| Operation | Description |
|-----------|-------------|
| `GenerateInvoice` | Generates and returns an invoice (PDF/URL) for a paid quotation. |
| `GetInvoice` | Retrieves data for an existing invoice. |

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

By default, the SOAP service will be available at `http://localhost:8082/ws` (configurable).

### Configuration

Update the following in `src/main/resources/application.yml` as needed:

```yaml
server:
  port: 8082
  servlet:
    context-path: /ws

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/invoice_db
    username: your_username
    password: your_password

# SOAP Service Configuration
cxf:
  path: /invoices
  servlet.init:
    transformWsdlLocations: true
```

### Building the Project

To build the project:

```bash
mvn clean package
```

### Testing

To run unit and integration tests:

```bash
mvn test
```

## SOAP Request Example

Here's an example SOAP request for generating an invoice:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                 xmlns:inv="http://example.com/invoice">
   <soapenv:Header/>
   <soapenv:Body>
      <inv:GenerateInvoiceRequest>
         <quotationId>12345</quotationId>
         <paymentReference>PAY-789</paymentReference>
         <customerEmail>customer@example.com</customerEmail>
      </inv:GenerateInvoiceRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

## WSDL Access

Once the service is running, you can access the WSDL at:

```
http://localhost:8082/ws/invoices?wsdl
```

## Monitoring

The service exposes health check endpoints:

* Health: `http://localhost:8082/actuator/health`
* Info: `http://localhost:8082/actuator/info`

## Contributing

Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.
