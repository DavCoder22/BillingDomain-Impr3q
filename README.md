# 3D Printing Quoter – Microservicios

Este repositorio contiene la solución basada en microservicios para el cotizador de impresión 3D.
Cada servicio se implementa en Java y persiste en PostgreSQL, pero usa distintos protocolos de exposición:

| Carpeta | Servicio | Protocolo | Descripción breve |
|---------|----------|-----------|-------------------|
| `quotation-service` | Quotation | REST/HTTP (Spring Boot) | Calcula el precio de una impresión 3D según los parámetros enviados. |
| `payment-service` | Payment | gRPC | Registra el pago de una cotización y devuelve su estado. |
| `invoice-service` | Invoice | SOAP | Genera y entrega la factura oficial a partir de la cotización pagada. |

## Cómo empezar

1. Instalar Java 17 y Docker.
2. Clonar este repositorio.
3. Revisar el `README.md` de cada servicio para instrucciones específicas de construcción y ejecución.
4. Para desarrollo local se proveerá un `docker-compose.yml` con Postgres y los tres servicios.

## Estructura de carpetas

```
BillingDM-Impr3q/
├── quotation-service/
│   └── README.md
├── payment-service/
│   └── README.md
├── invoice-service/
│   └── README.md
└── README.md  <– (este archivo)
```
