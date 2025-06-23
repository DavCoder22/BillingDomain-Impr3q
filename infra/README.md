# Infrastructure as Code with Terraform

This directory contains the Terraform configuration for deploying the BillingDM microservices to AWS.

## Prerequisites

1. Install [Terraform](https://www.terraform.io/downloads.html) (>= 1.0.0)
2. Install [AWS CLI](https://aws.amazon.com/cli/)
3. Configure AWS credentials with appropriate permissions

## Directory Structure

```
infra/
├── modules/           # Reusable Terraform modules
│   ├── ecs/          # ECS service configuration
│   ├── network/      # VPC, subnets, and networking
│   └── rds/          # PostgreSQL database configuration
├── main.tf           # Main Terraform configuration
├── variables.tf       # Variable declarations
├── outputs.tf         # Output values
└── terraform.tfvars   # Variable values (gitignored)
```

## Getting Started

1. **Copy the example variables file**

   ```bash
   cp terraform.tfvars.example terraform.tfvars
   ```

2. **Update the variables** in `terraform.tfvars` with your specific values.

3. **Initialize Terraform**

   ```bash
   terraform init
   ```

4. **Review the execution plan**

   ```bash
   terraform plan
   ```

5. **Apply the configuration**

   ```bash
   terraform apply
   ```

## Infrastructure Overview

This Terraform configuration will create the following AWS resources:

- **VPC** with public and private subnets across multiple availability zones
- **RDS PostgreSQL** database instance
- **ECS Fargate** cluster with services for each microservice
- **Application Load Balancer** to route traffic to services
- **CloudWatch Logs** for container logging
- **Security Groups** with least-privilege access rules
- **IAM Roles and Policies** for ECS task execution

## Deploying Microservices

After the infrastructure is created, you can deploy your microservices to ECR:

1. **Build and push Docker images**

   For each microservice (quotation, payment, invoice):

   ```bash
   # Build the Docker image
   docker build -t <account-id>.dkr.ecr.<region>.amazonaws.com/billingdm-<service>-service:latest ./<service>-service
   
   # Login to ECR
   aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account-id>.dkr.ecr.<region>.amazonaws.com
   
   # Push the image
   docker push <account-id>.dkr.ecr.<region>.amazonaws.com/billingdm-<service>-service:latest
   ```

2. **Update the ECS service** (if needed)

   ```bash
   aws ecs update-service --cluster billingdm-cluster-dev --service billingdm-<service>-service --force-new-deployment
   ```

## Accessing Services

- **Quotation Service**: `http://<alb-dns>/api/quotes`
- **Payment Service**: `http://<alb-dns>/api/payments`
- **Invoice Service**: `http://<alb-dns>/api/invoices`
- **Database**: Available at the RDS endpoint (see outputs)

## Cleaning Up

To destroy all created resources:

```bash
terraform destroy
```

## Notes

- The database password is stored in the Terraform state file. Consider using AWS Secrets Manager or Parameter Store for production.
- For production, enable backup retention and monitoring.
- Consider using remote state storage (S3 + DynamoDB) for team collaboration.
