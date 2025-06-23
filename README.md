# 3D Printing Quoter – Microservices

This repository contains a microservices-based solution for a 3D printing quotation system.
Each service is implemented in Java and uses PostgreSQL for persistence, but exposes different protocols:

| Folder | Service | Protocol | Description |
|--------|---------|-----------|--------------|
| `quotation-service` | Quotation | REST/HTTP (Spring Boot) | Calculates 3D printing prices based on provided parameters. |
| `payment-service` | Payment | gRPC | Processes payment for quotations and returns payment status. |
| `invoice-service` | Invoice | SOAP | Generates and delivers official invoices for paid quotations. |

## Getting Started

1. Install Java 17 and Docker.
2. Clone this repository.
3. Check each service's `README.md` for specific build and run instructions.
4. A `docker-compose.yml` file is provided for local development, including PostgreSQL and all three services.

## Project Structure

```text
BillingDM-Impr3q/
├── quotation-service/     # REST API for quotation management
│   └── README.md
├── payment-service/       # gRPC service for payment processing
│   └── README.md
├── invoice-service/       # SOAP service for invoice generation
│   └── README.md
├── .github/              # GitHub Actions workflows
├── docker-compose.yml     # Local development setup
└── README.md             # This file
```

## Development Workflow

### Branching Strategy

- `main`: Production-ready code
- `test`: Staging/QA environment
- `develop`: Active development branch
- `feature/*`: Feature branches

### Code Review Process

1. Create a feature branch from `develop`
2. Make your changes and push to the feature branch
3. Create a Pull Request to `develop`
4. After review and approval, merge to `develop`
5. Create a PR from `develop` to `test` for QA
6. For production releases, create a PR from `test` to `main`

### Required Reviews

- All PRs to `main` require approval from `@JuanGuevara90`
- At least one approval is required for merging into protected branches

## Local Development

### Prerequisites

- Java 17
- Docker and Docker Compose
- Maven

### Running Services

1. Start the database and services:

   ```bash
   docker-compose up -d
   ```

2. Build and run individual services:

   ```bash
   cd service-name
   mvn spring-boot:run
   ```

## CI/CD

This project uses GitHub Actions for CI/CD. The following workflows are configured:

- `deploy-develop.yml`: Runs on push to `develop`
- `deploy-test.yml`: Runs on push to `test`
- `deploy-prod.yml`: Manual deployment to production from `main`

## License

[Specify your license here]

## Contact

For any questions or issues, please contact the repository maintainers.
