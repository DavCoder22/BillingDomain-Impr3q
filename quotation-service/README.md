# Quotation Service

Servicio REST que calcula el precio de una impresión 3D.

* **Framework:** Spring Boot 3 (Java 17)
* **Protocolo:** HTTP/JSON (Spring Web)
* **Base de datos:** PostgreSQL (schema `quotation_db`)
* **Build:** Maven

## Endpoints iniciales
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/quotes` | Calcula y guarda una nueva cotización. |
| GET  | `/quotes/{id}` | Devuelve los detalles de una cotización. |

## Ejecución local
```bash
mvn spring-boot:run
```

La configuración de base de datos está en `src/main/resources/application.yml` y se aplica mediante variables de entorno.
