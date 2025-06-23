# Configure the AWS Provider
provider "aws" {
  region = var.aws_region
  
  # Uncomment and configure if using AWS profiles
  # profile = "default"
  
  # Uncomment and configure if using access keys
  # access_key = var.aws_access_key
  # secret_key = var.aws_secret_key
}

# Create VPC
module "vpc" {
  source = "./modules/network"
  
  vpc_cidr            = var.vpc_cidr
  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
  availability_zones  = var.availability_zones
  environment         = var.environment
}

# Create ECS Cluster
resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-cluster-${var.environment}"
  
  setting {
    name  = "containerInsights"
    value = "enabled"
  }
  
  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

# Create ECR Repositories for each microservice
resource "aws_ecr_repository" "services" {
  for_each = toset(["quotation-service", "payment-service", "invoice-service"])
  
  name                 = "${var.project_name}-${each.key}"
  image_tag_mutability = "MUTABLE"
  
  image_scanning_configuration {
    scan_on_push = true
  }
  
  tags = {
    Environment = var.environment
    Project     = var.project_name
    Service     = each.key
  }
}

# Create RDS Instance for PostgreSQL
module "rds" {
  source = "./modules/rds"
  
  allocated_storage      = 20
  storage_type           = "gp2"
  engine                = "postgres"
  engine_version        = "13.7"
  instance_class        = var.db_instance_class
  name                  = "billingdb"
  username              = var.db_username
  password              = var.db_password
  vpc_id                = module.vpc.vpc_id
  subnet_ids            = module.vpc.private_subnet_ids
  environment           = var.environment
  project_name          = var.project_name
  allowed_cidr_blocks   = [var.vpc_cidr]
}

# Create ECS Services for each microservice
module "ecs_service" {
  source = "./modules/ecs"
  
  for_each = {
    "quotation" = { port = 8080, cpu = 256, memory = 512, desired_count = 2, container_port = 8080 },
    "payment"   = { port = 50051, cpu = 512, memory = 1024, desired_count = 2, container_port = 50051 },
    "invoice"   = { port = 8082, cpu = 512, memory = 1024, desired_count = 2, container_port = 8080 }
  }
  
  name           = "${var.project_name}-${each.key}-service"
  environment    = var.environment
  vpc_id         = module.vpc.vpc_id
  cluster_id     = aws_ecs_cluster.main.id
  container_port = each.value.port
  cpu            = each.value.cpu
  memory         = each.value.memory
  desired_count  = each.value.desired_count
  
  # Database configuration
  db_host     = module.rds.db_instance_address
  db_name     = "billing_${each.key}_db"
  db_username = var.db_username
  db_password = var.db_password
  
  # Network configuration
  public_subnet_ids  = module.vpc.public_subnet_ids
  private_subnet_ids = module.vpc.private_subnet_ids
  
  # Container configuration
  container_image = "${aws_ecr_repository.services["${each.key}-service"].repository_url}:latest"
  container_port  = each.value.container_port
  
  # Load balancer configuration
  alb_target_group_arn = aws_lb_target_group.main[each.key].arn
}

# Application Load Balancer
resource "aws_lb" "main" {
  name               = "${var.project_name}-alb-${var.environment}"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = module.vpc.public_subnet_ids
  
  enable_deletion_protection = false
  
  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

# ALB Target Groups
resource "aws_lb_target_group" "main" {
  for_each = {
    "quotation" = 8080,
    "payment"   = 50051,
    "invoice"   = 8082
  }
  
  name        = "${var.project_name}-${each.key}-tg-${var.environment}"
  port        = each.value
  protocol    = "HTTP"
  vpc_id      = module.vpc.vpc_id
  target_type = "ip"
  
  health_check {
    enabled             = true
    path                = "/actuator/health"
    healthy_threshold   = 3
    unhealthy_threshold = 3
    timeout             = 10
    interval            = 30
    matcher             = "200-399"
  }
  
  tags = {
    Environment = var.environment
    Project     = var.project_name
    Service     = each.key
  }
}

# ALB Listeners
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"
  
  default_action {
    type = "redirect"
    
    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_lb.main.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = aws_acm_certificate.main.arn
  
  default_action {
    type = "fixed-response"
    
    fixed_response {
      content_type = "text/plain"
      message_body = "404: Not Found"
      status_code  = "404"
    }
  }
}

# ALB Listener Rules
resource "aws_lb_listener_rule" "services" {
  for_each = {
    "quotation" = "/api/quotes/*",
    "payment"   = "/api/payments/*",
    "invoice"   = "/api/invoices/*"
  }
  
  listener_arn = aws_lb_listener.https.arn
  priority     = 100 + index(keys(local.services), each.key)
  
  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.main[each.key].arn
  }
  
  condition {
    path_pattern {
      values = [each.value]
    }
  }
}

# ACM Certificate
resource "aws_acm_certificate" "main" {
  domain_name       = var.domain_name
  validation_method = "DNS"
  
  lifecycle {
    create_before_destroy = true
  }
  
  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

# Security Group for ALB
resource "aws_security_group" "alb" {
  name        = "${var.project_name}-alb-sg-${var.environment}"
  description = "Security group for ALB"
  vpc_id      = module.vpc.vpc_id
  
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = {
    Name        = "${var.project_name}-alb-sg-${var.environment}"
    Environment = var.environment
    Project     = var.project_name
  }
}

# Outputs
output "alb_dns_name" {
  description = "The DNS name of the load balancer"
  value       = aws_lb.main.dns_name
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
  value       = module.rds.db_instance_address
}

output "ecs_cluster_name" {
  description = "The name of the ECS cluster"
  value       = aws_ecs_cluster.main.name
}
