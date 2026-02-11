output "alb_dns_name" {
  description = "DNS name of the load balancer"
  value       = aws_lb.main.dns_name
}

output "ecr_repository_url" {
  description = "ECR repository URL"
  value       = aws_ecr_repository.app.repository_url
}

output "rds_endpoint" {
  description = "RDS endpoint"
  value       = aws_db_instance.postgres.endpoint
}

output "app_url" {
  description = "Application URL"
  value       = "http://${aws_lb.main.dns_name}"
}

output "secret_arn" {
  description = "ARN of the database secret"
  value       = aws_secretsmanager_secret.db_password.arn
}

output "alb_target_group_arn" {
  description = "ARN of the ALB target group"
  value       = aws_lb_target_group.app.arn
}
