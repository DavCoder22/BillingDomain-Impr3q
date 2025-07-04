# Outputs - Additional outputs not already defined in main.tf

output "vpc_id" {
  description = "The ID of the VPC"
  value       = module.vpc.vpc_id
}

output "public_subnet_ids" {
  description = "List of public subnet IDs"
  value       = module.vpc.public_subnet_ids
}

output "private_subnet_ids" {
  description = "List of private subnet IDs"
  value       = module.vpc.private_subnet_ids
}

output "ecs_cluster_name" {
  description = "The name of the ECS cluster"
  value       = aws_ecs_cluster.main.name
}

output "ecs_cluster_arn" {
  description = "The ARN of the ECS cluster"
  value       = aws_ecs_cluster.main.arn
}

output "ecr_repository_urls" {
  description = "The URLs of the ECR repositories"
  value = {
    for service, repo in aws_ecr_repository.services :
    service => repo.repository_url
  }
}

output "rds_endpoint" {
  description = "The connection endpoint for the RDS instance"
  value       = module.rds.db_instance_endpoint
}

output "alb_dns_name" {
  description = "The DNS name of the load balancer"
  value       = aws_lb.main.dns_name
}

output "alb_zone_id" {
  description = "The zone ID of the load balancer"
  value       = aws_lb.main.zone_id
}

output "alb_arn" {
  description = "The ARN of the load balancer"
  value       = aws_lb.main.arn
}

output "alb_https_listener_arn" {
  description = "The ARN of the HTTPS listener"
  value       = aws_lb_listener.https.arn
}

output "ecs_service_names" {
  description = "The names of the ECS services"
  value = {
    for service in ["quotation", "payment", "invoice"] :
    service => module.ecs_service[service].ecs_service_name
  }
}

output "cloudwatch_log_groups" {
  description = "The CloudWatch log groups for the ECS services"
  value = {
    for service in ["quotation", "payment", "invoice"] :
    service => "/ecs/${var.project_name}-${service}-service-${var.environment}"
  }
}

output "acm_certificate_arn" {
  description = "The ARN of the ACM certificate"
  value       = aws_acm_certificate.main.arn
}

output "acm_certificate_validation" {
  description = "The validation information for the ACM certificate"
  value       = aws_acm_certificate.main.domain_validation_options
}
