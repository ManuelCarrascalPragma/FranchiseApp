#!/bin/bash
set -e

echo "🚀 Iniciando despliegue de FranchiseApp en AWS..."

# Variables
AWS_REGION="us-east-1"
PROJECT_NAME="franchise-app"

echo "Construyendo aplicación..."
cd ..
./gradlew clean build -x test
cp applications/app-service/build/libs/*.jar deployment/

echo "Inicializando Terraform..."
cd terraform
terraform init

echo "Creando infraestructura en AWS..."
terraform apply -auto-approve

ECR_URL=$(terraform output -raw ecr_repository_url)
echo "ECR Repository: $ECR_URL"

echo "Autenticando en ECR..."
aws ecr get-login-password --region $AWS_REGION | podman login --username AWS --password-stdin $ECR_URL

echo "Construyendo imagen Docker..."
cd ../deployment
podman build -t $PROJECT_NAME:latest .

echo "Subiendo imagen a ECR..."
podman tag $PROJECT_NAME:latest $ECR_URL:latest
podman push $ECR_URL:latest

echo "Actualizando servicio ECS..."
aws ecs update-service \
  --cluster ${PROJECT_NAME}-cluster \
  --service ${PROJECT_NAME}-service \
  --force-new-deployment \
  --region $AWS_REGION

echo "Esperando a que el servicio esté listo..."
aws ecs wait services-stable \
  --cluster ${PROJECT_NAME}-cluster \
  --services ${PROJECT_NAME}-service \
  --region $AWS_REGION

cd ../terraform
APP_URL=$(terraform output -raw app_url)

echo ""
echo "¡Despliegue completado!"
echo "URL de la aplicación: $APP_URL"
echo "Health check: $APP_URL/actuator/health"
echo "Swagger UI: $APP_URL/swagger-ui.html"
