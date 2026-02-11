# Terraform Infrastructure - FranchiseApp

Infraestructura como código para desplegar FranchiseApp en AWS usando ECS Fargate.

## 📁 Estructura

```
terraform/
├── providers.tf    # Configuración de Terraform y AWS provider
├── variables.tf    # Variables de entrada
├── main.tf         # Todos los recursos de infraestructura
├── outputs.tf      # Outputs (URLs, endpoints)
├── deploy.sh       # Script de despliegue automatizado
└── destroy.sh      # Script de destrucción
```

## 🏗️ Recursos Desplegados

- **VPC** - Red virtual con 2 subnets públicas
- **Security Groups** - Para ALB, ECS y RDS
- **Application Load Balancer** - Distribución de tráfico
- **ECS Fargate** - Cluster, task definition y service
- **ECR** - Repositorio de imágenes Docker
- **RDS PostgreSQL 15** - Base de datos
- **Secrets Manager** - Gestión de credenciales
- **CloudWatch** - Logs y monitoreo
- **IAM Roles** - Permisos para ECS

## 📋 Prerequisitos

1. **AWS CLI configurado**
   ```bash
   aws configure
   # Ingresa tu Access Key ID, Secret Access Key y región (us-east-1)
   ```

2. **Terraform instalado** (versión >= 1.0)
   ```bash
   terraform --version
   ```

3. **Podman instalado** (ya lo tienes)
   ```bash
   podman --version
   ```

4. **Gradle** (para build de la app)
   ```bash
   ./gradlew --version
   ```

---

## 🚀 Despliegue Rápido

### Opción 1: Script Automatizado (Recomendado)

```bash
cd terraform
./deploy.sh
```

Este script hace TODO automáticamente:
- ✅ Build de la aplicación
- ✅ Crea infraestructura en AWS
- ✅ Construye y sube imagen Docker a ECR
- ✅ Despliega en ECS
- ✅ Te da la URL final

**Tiempo estimado:** 10-15 minutos

---

### Opción 2: Paso a Paso Manual

#### 1. Build de la aplicación
```bash
./gradlew clean build -x test
cp applications/app-service/build/libs/*.jar deployment/
```

#### 2. Crear infraestructura con Terraform
```bash
cd terraform
terraform init
terraform plan
terraform apply
```

Terraform creará:
- VPC con subnets públicas y privadas
- RDS PostgreSQL
- ECS Cluster + Service
- Application Load Balancer
- ECR Repository
- Secret Manager con password de DB
- Security Groups
- IAM Roles

#### 3. Subir imagen Docker a ECR

```bash
# Obtener URL del ECR
ECR_URL=$(terraform output -raw ecr_repository_url)

# Login en ECR
aws ecr get-login-password --region us-east-1 | \
  podman login --username AWS --password-stdin $ECR_URL

# Build de la imagen
cd ../deployment
podman build -t franchise-app:latest .

# Tag y push
podman tag franchise-app:latest $ECR_URL:latest
podman push $ECR_URL:latest
```

#### 4. Actualizar servicio ECS

```bash
aws ecs update-service \
  --cluster franchise-app-cluster \
  --service franchise-app-service \
  --force-new-deployment \
  --region us-east-1
```

#### 5. Esperar a que esté listo

```bash
aws ecs wait services-stable \
  --cluster franchise-app-cluster \
  --services franchise-app-service \
  --region us-east-1
```

#### 6. Obtener URL de la aplicación

```bash
cd ../terraform
terraform output app_url
```

---

## 🧪 Verificación

Una vez desplegado, verifica que todo funcione:

```bash
# Health check
curl http://<ALB-URL>/actuator/health

# Liveness probe
curl http://<ALB-URL>/actuator/health/liveness

# Readiness probe
curl http://<ALB-URL>/actuator/health/readiness

# Swagger UI (desde el navegador)
http://<ALB-URL>/swagger-ui.html
```

---

## 📊 Monitoreo

### CloudWatch Logs
```bash
aws logs tail /ecs/franchise-app --follow --region us-east-1
```

### Estado del servicio ECS
```bash
aws ecs describe-services \
  --cluster franchise-app-cluster \
  --services franchise-app-service \
  --region us-east-1
```

### Health checks del ALB
```bash
aws elbv2 describe-target-health \
  --target-group-arn <TARGET-GROUP-ARN> \
  --region us-east-1
```

---

## 🔄 Actualizar la aplicación

Después de hacer cambios en el código:

```bash
# 1. Build
./gradlew clean build -x test
cp applications/app-service/build/libs/*.jar deployment/

# 2. Build y push de nueva imagen
cd deployment
ECR_URL=$(cd ../terraform && terraform output -raw ecr_repository_url)
podman build -t franchise-app:latest .
podman tag franchise-app:latest $ECR_URL:latest
podman push $ECR_URL:latest

# 3. Forzar nuevo despliegue
aws ecs update-service \
  --cluster franchise-app-cluster \
  --service franchise-app-service \
  --force-new-deployment \
  --region us-east-1
```

---

## 🗑️ Destruir infraestructura

```bash
cd terraform
./destroy.sh
```

O manualmente:
```bash
terraform destroy
```

**⚠️ IMPORTANTE:** Esto eliminará TODOS los recursos y la base de datos.

---

## 💰 Costos Estimados

| Recurso | Costo mensual (aprox.) |
|---------|------------------------|
| RDS db.t4g.micro | $15 |
| ECS Fargate (0.25 vCPU, 512 MB) | $10 |
| Application Load Balancer | $16 |
| Secrets Manager | $0.40 |
| CloudWatch Logs | $2 |
| **TOTAL** | **~$43/mes** |

---

## 🔧 Configuración

### Variables de Terraform

Puedes modificar `terraform/variables.tf` o crear un archivo `terraform.tfvars`:

```hcl
aws_region   = "us-east-1"
project_name = "franchise-app"
environment  = "dev"
db_password  = "tu-password-segura"
db_username  = "postgres"
db_name      = "franchise_db"
```

### Perfiles de Spring Boot

- **Local:** `application.yaml` (usa docker-compose)
- **Producción:** `application-prod.yaml` (usa RDS y Secrets Manager)

El perfil se activa automáticamente con la variable `SPRING_PROFILES_ACTIVE=prod` en ECS.

---

## 🐛 Troubleshooting

### La aplicación no inicia en ECS

1. Revisar logs:
   ```bash
   aws logs tail /ecs/franchise-app --follow --region us-east-1
   ```

2. Verificar que el secret esté disponible:
   ```bash
   aws secretsmanager get-secret-value \
     --secret-id franchise-app-db-password \
     --region us-east-1
   ```

3. Verificar conectividad a RDS desde ECS:
   - Los security groups deben permitir tráfico del ECS al RDS en puerto 5432

### Health checks fallan

1. Verificar que el endpoint responda:
   ```bash
   # Desde dentro del contenedor
   wget --spider http://localhost:8080/actuator/health
   ```

2. Revisar configuración del target group en AWS Console

### No puedo hacer push a ECR

1. Verificar autenticación:
   ```bash
   aws ecr get-login-password --region us-east-1 | \
     podman login --username AWS --password-stdin <ECR-URL>
   ```

2. Verificar permisos IAM de tu usuario

---

## 📝 Notas

- La base de datos se crea automáticamente con el schema `franchise_management`
- El script SQL (`schema.sql`) NO se ejecuta en producción (está deshabilitado en `application-prod.yaml`)
- Debes ejecutar el schema manualmente la primera vez conectándote a RDS
- El password de RDS está en Secrets Manager y se inyecta automáticamente en ECS

---

## 🔐 Seguridad

- ✅ Contenedor corre como usuario no-root
- ✅ RDS en subnet privada (no accesible desde internet)
- ✅ Password en Secrets Manager (no hardcodeado)
- ✅ Security groups con mínimo privilegio
- ✅ HTTPS en ALB (puedes agregar certificado SSL después)

---

## 📞 Soporte

Si tienes problemas, revisa:
1. CloudWatch Logs: `/ecs/franchise-app`
2. ECS Service Events en AWS Console
3. Target Health en el ALB
