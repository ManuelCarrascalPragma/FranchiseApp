# Franchise Management System - Clean Architecture

Sistema de gestión de franquicias implementado con Clean Architecture, Spring Boot WebFlux y desplegado en AWS.

## 🏗️ Arquitectura

Este proyecto implementa Clean Architecture con las siguientes capas:

- **Domain** - Entidades y reglas de negocio
- **Use Cases** - Lógica de aplicación
- **Infrastructure** - Adaptadores externos (DB, API REST)
- **Application** - Configuración y punto de entrada

### Estructura del Proyecto

```
├── domain/
│   ├── model/          # Entidades de dominio
│   └── usecase/        # Casos de uso
├── infrastructure/
│   ├── driven-adapters/    # Adaptadores de salida (DB, APIs)
│   └── entry-points/       # Adaptadores de entrada (REST)
└── applications/
    └── app-service/    # Configuración y bootstrap
```

## 🚀 Despliegue en AWS

**URL de producción:** http://franchise-app-alb-1415245703.us-east-1.elb.amazonaws.com

### Infraestructura
- **ECS Fargate** - Contenedores serverless
- **Application Load Balancer** - Distribución de tráfico
- **RDS PostgreSQL 15** - Base de datos
- **ECR** - Registro de imágenes Docker
- **CloudWatch** - Logs y monitoreo
- **Secrets Manager** - Gestión de credenciales

### Despliegue Rápido
```bash
cd terraform
./deploy.sh
```

### Verificación
```bash
./verify-deployment.sh
```

## 🏃 Ejecución Local

```bash
docker-compose up -d
./gradlew bootRun
```

**URLs locales:**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

## 📡 API Endpoints

### Franchises
- `POST /api/franchises` - Crear franquicia
- `PATCH /api/franchises/{id}` - Actualizar franquicia

### Branches
- `POST /api/franchises/{franchiseId}/branches` - Agregar sucursal
- `PATCH /api/branches/{branchId}` - Actualizar sucursal

### Products
- `POST /api/branches/{branchId}/products` - Agregar producto
- `PATCH /api/products/{productId}` - Actualizar nombre de producto
- `PATCH /api/branches/{branchId}/products/{productId}/stock` - Actualizar stock
- `DELETE /api/branches/{branchId}/products/{productId}` - Eliminar producto

### Reports
- `GET /api/franchises/{franchiseId}/max-stock` - Producto con mayor stock por sucursal

## 🧪 Testing

```bash
# Ejecutar tests
./gradlew test

# Verificar despliegue en AWS
./verify-deployment.sh
```

## 🛠️ Stack Tecnológico

- **Java 21 LTS**
- **Spring Boot 3.x** (WebFlux)
- **R2DBC** (PostgreSQL)
- **Gradle**
- **Docker**
- **Terraform**
- **AWS** (ECS, RDS, ALB, ECR)

## 📚 Documentación Adicional

- [Terraform README](terraform/README.md) - Documentación de infraestructura
- [Clean Architecture](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a) - Artículo de referencia

## 🔧 Configuración

### Variables de Entorno (Producción)
```bash
SPRING_PROFILES_ACTIVE=prod
DB_HOST=<rds-endpoint>
DB_PORT=5432
DB_NAME=franchise_db
DB_USERNAME=postgres
DB_PASSWORD=<from-secrets-manager>
```

### Base de Datos
- **Host:** franchise-app-db.c21k2q0sytbz.us-east-1.rds.amazonaws.com
- **Port:** 5432
- **Database:** franchise_db
- **Schema:** franchise_management

## 📝 Notas

- El schema SQL se ejecuta automáticamente al iniciar la aplicación
- Los health checks están configurados en `/actuator/health`
- La aplicación usa graceful shutdown para despliegues sin downtime
