# General Variables
variable "project_name" {
  description = "The name of the project"
  type        = string
  default     = "billingdm"
}

variable "environment" {
  description = "The environment (e.g., dev, test, prod)"
  type        = string
  default     = "dev"
}

variable "aws_region" {
  description = "The AWS region to deploy to"
  type        = string
  default     = "us-east-1"
}

# VPC Variables
variable "vpc_cidr" {
  description = "The CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "List of public subnet CIDR blocks"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "List of private subnet CIDR blocks"
  type        = list(string)
  default     = ["10.0.3.0/24", "10.0.4.0/24"]
}

variable "availability_zones" {
  description = "List of availability zones"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

# Database Variables
variable "db_instance_class" {
  description = "The instance class for the RDS instance"
  type        = string
  default     = "db.t3.micro"
}

variable "db_username" {
  description = "The username for the database"
  type        = string
  default     = "admin"
}

variable "db_password" {
  description = "The password for the database"
  type        = string
  sensitive   = true
}

# ECS Variables
variable "ecs_task_execution_role_name" {
  description = "ECS task execution role name"
  type        = string
  default     = "billingdm-ecs-task-execution-role"
}

# Domain Name
variable "domain_name" {
  description = "The domain name for the application"
  type        = string
  default     = "billingdm.example.com"
}

# Tags
variable "tags" {
  description = "A map of tags to add to all resources"
  type        = map(string)
  default     = {
    Terraform   = "true"
    Environment = "dev"
  }
}

# Optional: Uncomment and configure if using AWS access keys
# variable "aws_access_key" {
#   description = "AWS access key"
#   type        = string
#   sensitive   = true
# }


# variable "aws_secret_key" {
#   description = "AWS secret key"
#   type        = string
#   sensitive   = true
# }
