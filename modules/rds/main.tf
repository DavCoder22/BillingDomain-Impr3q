# Security Group for RDS
resource "aws_security_group" "rds" {
  name        = "${var.project_name}-rds-sg-${var.environment}"
  description = "Security group for RDS instance"
  vpc_id      = var.vpc_id
  
  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = var.allowed_cidr_blocks
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = merge(
    {
      Name = "${var.project_name}-rds-sg-${var.environment}"
    },
    var.tags
  )
}

# RDS Subnet Group
resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-subnet-group-${var.environment}"
  subnet_ids = var.subnet_ids
  
  tags = merge(
    {
      Name = "${var.project_name}-db-subnet-group-${var.environment}"
    },
    var.tags
  )
}

# RDS Parameter Group
resource "aws_db_parameter_group" "main" {
  name        = "${var.project_name}-pg-${var.environment}"
  family      = "${var.engine}${var.engine_version}"
  description = "Parameter group for ${var.project_name} ${var.environment}"
  
  parameter {
    name  = "log_statement"
    value = "all"
  }
  
  parameter {
    name  = "log_min_duration_statement"
    value = "1000"
  }
  
  tags = merge(
    {
      Name = "${var.project_name}-pg-${var.environment}"
    },
    var.tags
  )
}

# RDS Instance
resource "aws_db_instance" "main" {
  identifier             = "${var.project_name}-db-${var.environment}"
  instance_class         = var.instance_class
  allocated_storage      = var.allocated_storage
  storage_type           = var.storage_type
  engine                 = var.engine
  engine_version         = var.engine_version
  db_name                = var.name
  username               = var.username
  password               = var.password
  
  db_subnet_group_name   = aws_db_subnet_group.main.name
  parameter_group_name    = aws_db_parameter_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  
  multi_az               = var.multi_az
  storage_encrypted      = true
  skip_final_snapshot    = var.environment != "prod"
  
  backup_retention_period = var.backup_retention_period
  backup_window          = var.backup_window
  maintenance_window     = var.maintenance_window
  
  apply_immediately     = true
  
  tags = merge(
    {
      Name = "${var.project_name}-db-${var.environment}"
    },
    var.tags
  )
}

# Outputs
output "db_instance_id" {
  description = "The ID of the RDS instance"
  value       = aws_db_instance.main.id
}

output "db_instance_address" {
  description = "The address of the RDS instance"
  value       = aws_db_instance.main.address
}

output "db_instance_port" {
  description = "The port of the RDS instance"
  value       = aws_db_instance.main.port
}

output "db_instance_username" {
  description = "The username for the RDS instance"
  value       = aws_db_instance.main.username
  sensitive   = true
}

output "db_instance_password" {
  description = "The password for the RDS instance"
  value       = aws_db_instance.main.password
  sensitive   = true
}

output "db_instance_endpoint" {
  description = "The connection endpoint for the RDS instance"
  value       = aws_db_instance.main.endpoint
}
