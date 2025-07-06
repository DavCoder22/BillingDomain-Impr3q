terraform {
  required_version = ">= 1.0.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = {
      Environment = var.environment
      Project     = "billing-system"
      ManagedBy   = "terraform"
    }
  }
}

# VPC Configuration
module "vpc" {
  source = "./modules/vpc"
  
  environment = var.environment
  vpc_cidr   = var.vpc_cidr
  azs        = var.availability_zones
}

# ECS Cluster
module "ecs" {
  source = "./modules/ecs"
  
  environment = var.environment
  vpc_id     = module.vpc.vpc_id
  subnets    = module.vpc.private_subnets
}

# RDS Database
module "database" {
  source = "./modules/rds"
  
  environment       = var.environment
  vpc_id            = module.vpc.vpc_id
  db_subnet_ids     = module.vpc.database_subnets
  db_name           = "paymentdb"
  db_username       = var.db_username
  db_password       = var.db_password
  db_instance_class = var.db_instance_class
}

# ECR Repositories
resource "aws_ecr_repository" "payment_service" {
  name                 = "${var.environment}-payment-service"
  image_tag_mutability = "MUTABLE"
  
  image_scanning_configuration {
    scan_on_push = true
  }
}

# Outputs
output "ecr_repository_url" {
  value = aws_ecr_repository.payment_service.repository_url
}

output "rds_endpoint" {
  value = module.database.rds_endpoint
}

output "ecs_cluster_name" {
  value = module.ecs.cluster_name
}
