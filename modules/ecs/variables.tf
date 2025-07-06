variable "name" {
  description = "The name of the service"
  type        = string
}

variable "environment" {
  description = "The environment (e.g., dev, test, prod)"
  type        = string
}

variable "project_name" {
  description = "The name of the project"
  type        = string
}

variable "vpc_id" {
  description = "The ID of the VPC"
  type        = string
}

variable "cluster_id" {
  description = "The ARN of the ECS cluster"
  type        = string
}

variable "container_image" {
  description = "The Docker image to use for the container"
  type        = string
}

variable "container_port" {
  description = "The port on which the container will receive traffic"
  type        = number
}

variable "cpu" {
  description = "The number of CPU units to reserve for the container"
  type        = number
  default     = 256
}

variable "memory" {
  description = "The amount (in MiB) of memory to reserve for the container"
  type        = number
  default     = 512
}

variable "desired_count" {
  description = "The number of instances of the task definition to place and keep running"
  type        = number
  default     = 1
}

variable "private_subnet_ids" {
  description = "List of private subnet IDs for the ECS service"
  type        = list(string)
}

data "aws_region" "current" {}

variable "aws_region" {
  description = "The AWS region"
  type        = string
  default     = "us-east-1"
}

variable "db_host" {
  description = "The database host"
  type        = string
}

variable "db_name" {
  description = "The database name"
  type        = string
}

variable "db_username" {
  description = "The database username"
  type        = string
}

data "aws_caller_identity" "current" {}

variable "db_password" {
  description = "The database password"
  type        = string
  sensitive   = true
}

variable "alb_target_group_arn" {
  description = "The ARN of the ALB target group"
  type        = string
}

variable "alb_security_group_id" {
  description = "The ID of the ALB security group"
  type        = string
}

variable "ecs_security_group_id" {
  description = "The ID of the ECS security group"
  type        = string
}

variable "tags" {
  description = "A map of tags to add to all resources"
  type        = map(string)
  default     = {}
}
