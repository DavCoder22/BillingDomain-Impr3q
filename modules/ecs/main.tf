# ECS Task Execution Role
resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${var.project_name}-ecs-task-execution-role-${var.environment}"
  
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })
  
  tags = var.tags
}

# Attach the AmazonECSTaskExecutionRolePolicy to the role
resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# ECS Task Definition
resource "aws_ecs_task_definition" "main" {
  family                   = "${var.name}-${var.environment}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.cpu
  memory                   = var.memory
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_execution_role.arn
  
  container_definitions = jsonencode([
    {
      name         = var.name
      image        = var.container_image
      essential    = true
      portMappings = [
        {
          containerPort = var.container_port
          hostPort      = var.container_port
          protocol      = "tcp"
        }
      ]
      
      environment = [
        {
          name  = "SPRING_DATASOURCE_URL"
          value = "jdbc:postgresql://${var.db_host}:5432/${var.db_name}"
        },
        {
          name  = "SPRING_DATASOURCE_USERNAME"
          value = var.db_username
        },
        {
          name  = "SPRING_DATASOURCE_PASSWORD"
          value = var.db_password
        },
        {
          name  = "SERVER_PORT"
          value = tostring(var.container_port)
        },
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = var.environment
        }
      ]
      
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/${var.name}-${var.environment}"
          awslogs-region        = var.aws_region
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])
  
  tags = merge(
    {
      Name = "${var.name}-task-definition-${var.environment}"
    },
    var.tags
  )
}

# CloudWatch Log Group
resource "aws_cloudwatch_log_group" "main" {
  name              = "/ecs/${var.name}-${var.environment}"
  retention_in_days = 30
  
  tags = merge(
    {
      Name = "${var.name}-log-group-${var.environment}"
    },
    var.tags
  )
}

# ECS Service
resource "aws_ecs_service" "main" {
  name            = "${var.name}-service-${var.environment}"
  cluster         = var.cluster_id
  task_definition = aws_ecs_task_definition.main.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"
  
  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.ecs_security_group_id]
    assign_public_ip = false
  }
  
  load_balancer {
    target_group_arn = var.alb_target_group_arn
    container_name   = var.name
    container_port   = var.container_port
  }
  
  depends_on = [
    aws_iam_role_policy_attachment.ecs_task_execution_role_policy,
    aws_cloudwatch_log_group.main
  ]
  
  tags = var.tags
}



# Outputs
output "ecs_service_name" {
  description = "The name of the ECS service"
  value       = aws_ecs_service.main.name
}

output "task_definition_arn" {
  description = "The ARN of the task definition"
  value       = aws_ecs_task_definition.main.arn
}


