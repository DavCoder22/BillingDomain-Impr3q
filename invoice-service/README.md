# Invoice Service

Servicio SOAP que genera la factura correspondiente a una cotización pagada.

* **Framework:** Spring Boot 3 + Apache CXF (o Spring-WS)
* **Protocolo:** SOAP 1.2 / XML
* **Base de datos:** PostgreSQL (schema `invoice_db`)
* **Build:** Maven

## WSDL
El contrato se define en `src/main/resources/wsdl/InvoiceService.wsdl`.

Operaciones previstas:
| Operación | Descripción |
|-----------|-------------|
| `GenerateInvoice` | Genera y devuelve la factura (PDF/URL) para una cotización y su pago asociados. |
| `GetInvoice` | Recupera los datos de una factura existente. |

## Ejecución local
```bash
mvn spring-boot:run
```

Por defecto el servicio SOAP se expondrá en `http://localhost:8082/ws` (configurable).
