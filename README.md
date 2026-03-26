# Demo POS Backend

Backend de demostración para un sistema de punto de venta (POS) específicamente diseñado para pruebas automatizadas de UI, API y performance.

## 🎯 Descripción General

Este proyecto es un backend **listo para usar en pruebas automatizadas** que implementa un sistema completo de gestión de POS (Punto de Venta) con:

- ✅ Gestión completa de **clientes** (CRUD)
- ✅ Gestión de **productos** con control de inventario
- ✅ Registro y consulta de **ventas** con detalles de líneas
- ✅ **Movimientos de caja** con soporte para ventas, reembolsos y ajustes manuales
- ✅ Endpoints especiales para **pruebas de QA** (timeouts, errores aleatorios, validaciones)
- ✅ **Documentación OpenAPI/Swagger** completamente integrada
- ✅ **Manejo global de errores** con respuestas estructuradas
- ✅ **Validaciones de negocio** implementadas para probar casos negativos

## 🛠️ Stack Tecnológico

| Componente | Versión |
|-----------|---------|
| **Java** | 17+ |
| **Spring Boot** | 3.2.4 (LTS) |
| **Gradle** | 8.5 |
| **Base de Datos** | H2 (en memoria o archivo) |
| **ORM** | JPA / Hibernate |
| **Documentación** | Springdoc OpenAPI 2.1.0 |
| **Testing** | JUnit 5, Spring Boot Test |

### Dependencias Principales

```gradle
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- springdoc-openapi-starter-webmvc-ui
- h2
```

## 📊 Modelo de Dominio

### Entidades

```
┌─────────────┐
│   Customer  │
├─────────────┤
│ id (PK)     │
│ name        │
│ email (UK)  │
│ phone       │
│ status      │
│ createdAt   │
└─────────────┘

┌─────────────┐
│   Product   │
├─────────────┤
│ id (PK)     │
│ name        │
│ sku (UK)    │
│ price       │
│ stock       │
│ status      │
│ createdAt   │
└─────────────┘

┌─────────────┐         ┌───────────────┐
│    Sale     │ 1 ---* │    SaleItem   │
├─────────────┤         ├───────────────┤
│ id (PK)     │         │ id (PK)       │
│ customerId  │         │ saleId (FK)   │
│ date        │         │ productId     │
│ totalAmount │         │ quantity      │
│ status      │         │ unitPrice     │
│ createdAt   │         │ lineTotal     │
└─────────────┘         └───────────────┘

┌───────────────────┐
│  CashMovement     │
├───────────────────┤
│ id (PK)           │
│ type (SALE,       │
│   REFUND, ADJ)    │
│ amount            │
│ description       │
│ date              │
│ referenceId       │
│ createdAt         │
└───────────────────┘
```

### Enumeraciones

**Customer.status**: `ACTIVE`, `INACTIVE`

**Product.status**: `ACTIVE`, `DISCONTINUED`, `OUT_OF_STOCK`

**Sale.status**: `COMPLETED`, `CANCELLED`, `REFUNDED`

**CashMovement.type**: `SALE`, `REFUND`, `MANUAL_ADJUSTMENT`

## 🔌 API REST - Endpoints

### Clientes (`/api/customers`)

| Método | Path | Descripción | Status |
|--------|------|-------------|--------|
| GET | `/api/customers` | Listar todos los clientes | 200 |
| GET | `/api/customers/{id}` | Obtener cliente por ID | 200, 404 |
| POST | `/api/customers` | Crear nuevo cliente | 201, 400, 409 |
| PUT | `/api/customers/{id}` | Reemplazar cliente completo | 200, 400, 404, 409 |
| PATCH | `/api/customers/{id}` | Actualizar parcialmente | 200, 404, 409 |
| DELETE | `/api/customers/{id}` | Eliminar (soft delete) | 204, 404 |

**Validaciones y errores:**
- Email inválido → **400 Bad Request**
- Email duplicado → **409 Conflict**
- ID no existe → **404 Not Found**

### Productos (`/api/products`)

| Método | Path | Descripción | Status |
|--------|------|-------------|--------|
| GET | `/api/products` | Listar todos los productos | 200 |
| GET | `/api/products/{id}` | Obtener producto por ID | 200, 404 |
| POST | `/api/products` | Crear nuevo producto | 201, 400, 409 |
| PUT | `/api/products/{id}` | Reemplazar producto completo | 200, 400, 404, 409 |
| PATCH | `/api/products/{id}` | Actualizar parcialmente | 200, 404, 409 |
| DELETE | `/api/products/{id}` | Eliminar (marca DISCONTINUED) | 204, 404 |

**Validaciones y errores:**
- Precio negativo o cero → **400 Bad Request**
- Stock negativo → **400 Bad Request**
- SKU duplicado → **409 Conflict**
- ID no existe → **404 Not Found**

### Ventas (`/api/sales`)

| Método | Path | Descripción | Status |
|--------|------|-------------|--------|
| GET | `/api/sales` | Listar todas las ventas | 200 |
| GET | `/api/sales?customerId={id}` | Listar ventas de un cliente | 200, 404 |
| GET | `/api/sales/{id}` | Obtener venta con detalles | 200, 404 |
| POST | `/api/sales` | Crear venta (deduce stock, registra caja) | 201, 400, 404 |
| DELETE | `/api/sales/{id}` | Cancelar venta (restaura stock) | 204, 400, 404 |

**Reglas de negocio:**
- Valida que cliente existe
- Valida que todos los productos existen
- Si producto está `OUT_OF_STOCK` → **400 Bad Request**
- Si stock insuficiente → **400 Bad Request**
- Recalcula `totalAmount` automáticamente
- Deduce stock en base de datos
- Registra movimiento de caja automáticamente

**Errores:**
- Cliente no existe → **404 Not Found**
- Producto no existe → **404 Not Found**
- Stock insuficiente → **400 Bad Request**
- Producto fuera de stock → **400 Bad Request**
- Venta ya cancelada → **400 Bad Request**

### Caja (`/api/cash`)

| Método | Path | Descripción | Status |
|--------|------|-------------|--------|
| GET | `/api/cash/movements` | Listar movimientos de caja | 200 |
| GET | `/api/cash/summary` | Obtener resumen (totales, balance) | 200 |
| POST | `/api/cash/movements` | Crear ajuste manual | 201, 400 |

**Resumen incluye:**
- `totalSales`: suma de todas las ventas
- `totalRefunds`: suma de reembolsos (valor absoluto)
- `balance`: resultado neto

**Validaciones:**
- Amount = 0 → **400 Bad Request**
- Type inválido → **400 Bad Request**
- Solo `MANUAL_ADJUSTMENT` puede crearse manualmente

### Endpoints de Prueba (`/api/test`)

Estos endpoints están diseñados **EXCLUSIVAMENTE para pruebas de QA** y **NO representan** funcionalidad real.

| Método | Path | Descripción | Comportamiento |
|--------|------|-------------|-----------------|
| GET | `/api/test/slow-endpoint` | Simula latencia | Responde después de 3-5 segundos con 200 |
| GET | `/api/test/random-error` | Error aleatorio | 200 o 500 (50/50) |
| POST | `/api/test/validation-error` | Error de validación | Siempre 422 con ejemplo de errores |

**Casos de uso:**
- `slow-endpoint`: Pruebas de timeout, performance, test de reintentos
- `random-error`: Pruebas de resiliencia, manejo de fallos intermitentes
- `validation-error`: Validación de parseo de errores de validación

## 🚀 Ejecución Local

### Requisitos Previos

- **Java 17+** instalado
- **Gradle** (incluido en el proyecto con wrapper)

### Comandos Principales

```bash
# Descargar el repositorio
git clone https://github.com/Ivan-Arechiga/demo-pos-backend.git
cd demo-pos-backend

# Compilar el proyecto
./gradlew clean build

# Ejecutar la aplicación en desarrollo
./gradlew bootRun

# Ejecutar tests
./gradlew test

# Ver tasks disponibles
./gradlew tasks
```

### URLs Locales

Una vez que la aplicación esté corriendo en `http://localhost:8080`:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **H2 Console** (desarrollo): http://localhost:8080/h2-console
  - Default: JDBC URL: `jdbc:h2:mem:posdb`
  - Usuario: `sa`
  - Contraseña: (vacía)

### Ejemplo de uso con curl

```bash
# Crear cliente
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "phone": "555-1234"
  }'

# Crear producto
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "sku": "LAPTOP-001",
    "price": 999.99,
    "stock": 50
  }'

# Crear venta
curl -X POST http://localhost:8080/api/sales \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ]
  }'

# Obtener resumen de caja
curl http://localhost:8080/api/cash/summary

# Probar endpoint lento (30 segundos de timeout)
curl --max-time 30 http://localhost:8080/api/test/slow-endpoint
```

## 📦 Despliegue en Render (Gratuito)

### Opción 1: Despliegue Automático con render.yaml ⭐ (RECOMENDADO)

#### 1. Archivo de configuración incluido

El proyecto incluye **`render.yaml`** en la raíz con toda la configuración:

```yaml
services:
  - type: web
    name: demo-pos-backend
    runtime: java
    buildCommand: ./gradlew clean build --no-daemon -x test
    startCommand: java -jar build/libs/demo-pos-backend-1.0.0.jar
    envVars:
      - key: PORT
        value: 8080
```

#### 2. Despliegue

1. Push del proyecto a GitHub:
```bash
git add .
git commit -m "Add render.yaml configuration"
git push origin main
```

2. Ve a [render.com](https://render.com) → Dashboard
3. Haz clic en **"New" → "Web Service"**
4. Conecta tu repositorio `demo-pos-backend`
5. Render detectará automáticamente `render.yaml` y aplicará la configuración
6. ¡Listo! El despliegue se inicia automáticamente

**URL generada:** `https://demo-pos-backend.onrender.com`

---

### Opción 2: Despliegue Manual (sin render.yaml)

Si prefieres configurar manualmente en el dashboard:

1. Ve a [render.com](https://render.com)
2. Click en **"New" → "Web Service"**
3. Conecta tu repositorio GitHub (`demo-pos-backend`)
4. Rellena la configuración:

| Campo | Valor |
|-------|-------|
| **Name** | `demo-pos-backend` |
| **Environment** | `Java` |
| **Region** | Elige la más cercana |
| **Branch** | `main` |
| **Build Command** | `./gradlew clean build --no-daemon -x test` |
| **Start Command** | `java -jar build/libs/demo-pos-backend-1.0.0.jar` |

5. Variables de entorno:
   - `PORT`: `8080`
   - `SPRING_PROFILES_ACTIVE`: `default`

6. Click en **"Create Web Service"** → Deploy automático

---

### URLs en Render (ambas opciones)

- **Swagger UI**: `https://demo-pos-backend.onrender.com/swagger-ui.html`
- **OpenAPI JSON**: `https://demo-pos-backend.onrender.com/api-docs`
- **API Clientes**: `https://demo-pos-backend.onrender.com/api/customers`
- **Resumen Caja**: `https://demo-pos-backend.onrender.com/api/cash/summary`

### Notas importantes sobre Render

- ✅ **Database**: H2 en memoria (suficiente para demo/testing)
  - Se perderá si la app se reinicia
  - Para persistencia: añade Postgres free en Render o SQLite con ruta de disco
- ✅ **Plan gratuito**:
  - 750 horas/mes de ejecución
  - Puede dormirse tras 15 minutos sin actividad
  - Se reactiva cuando recibe una solicitud
- ✅ **Build**: ~2-3 minutos en Render
- ✅ **Autoredeploy**: Se activa con cada push a main

### Dockerfile (opcional, si quieres más control)

```dockerfile
FROM gradle:8.5-jdk17 as builder
WORKDIR /app
COPY . .
RUN gradle clean build

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/build/libs/demo-pos-backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 🧪 Ideas para Pruebas QA

### Pruebas Positivas (Happy Path)

```python
def test_customer_workflow():
    # Crear cliente
    customer = create_customer("Juan", "juan@test.com")
    assert customer.id > 0
    
    # Actualizar cliente
    update_customer(customer.id, name="Juan Updated")
    
    # Obtener cliente
    updated = get_customer(customer.id)
    assert updated.name == "Juan Updated"
    
    # Listar clientes
    customers = list_customers()
    assert len(customers) >= 1
    
    # Borrar cliente
    delete_customer(customer.id)
```

### Pruebas Negativas (Casos de Error)

```python
def test_duplicate_email():
    create_customer("Test", "test@example.com")
    response = create_customer("Another", "test@example.com")
    assert response.status == 409  # Conflict

def test_invalid_email():
    response = create_customer("Test", "invalid-email")
    assert response.status == 400  # Bad Request

def test_customer_not_found():
    response = get_customer(9999)
    assert response.status == 404  # Not Found

def test_insufficient_stock():
    product = create_product("Test", "SKU-1", price=10, stock=5)
    customer = create_customer("Test", "test@test.com")
    
    response = create_sale(customer.id, [
        {"productId": product.id, "quantity": 10}
    ])
    assert response.status == 400  # Bad Request
    assert "insufficient" in response.message.lower()

def test_product_out_of_stock():
    product = create_product("Test", "SKU-2", price=10, stock=0)
    product.status = "OUT_OF_STOCK"
    update_product(product.id, status="OUT_OF_STOCK")
    
    customer = create_customer("Test", "test@test.com")
    response = create_sale(customer.id, [
        {"productId": product.id, "quantity": 1}
    ])
    assert response.status == 400
    assert "out of stock" in response.message.lower()
```

### Pruebas de Performance

```python
def test_slow_endpoint_timeout():
    import time
    start = time.time()
    response = get("/api/test/slow-endpoint", timeout=2)
    elapsed = time.time() - start
    assert response.status == 408  # Request Timeout (handled by test framework)

def test_random_error_resilience():
    successes = 0
    failures = 0
    
    for i in range(10):
        response = get("/api/test/random-error")
        if response.status == 200:
            successes += 1
        elif response.status == 500:
            failures += 1
    
    assert successes > 0 and failures > 0  # Ambos deben ocurrir
```

### Pruebas de Validación

```python
def test_validation_errors():
    response = post("/api/test/validation-error")
    assert response.status == 422
    assert response.validationErrors is not None
    assert len(response.validationErrors) > 0
    
    # Verificar estructura
    for error in response.validationErrors:
        assert "field" in error
        assert "message" in error
        assert "rejectedValue" in error
```

## 📋 Configuración

### application.properties

```properties
# Puerto (recibe valor de env var PORT o usa 8080)
server.port=${PORT:8080}

# Base de datos H2
spring.datasource.url=jdbc:h2:mem:posdb
spring.jpa.hibernate.ddl-auto=create-drop

# H2 Console en desarrollo
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# OpenAPI
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
```

### Perfiles (profiles)

**Desarrollo (default)**
```bash
./gradlew bootRun
```

**Testing**
```bash
./gradlew test
```

## 🐛 Troubleshooting

### Puerto en uso

```bash
# Cambiar puerto localmente
./gradlew bootRun --args='--server.port=8081'
```

### H2 Console no accessible

Asegúrate en `application.properties`:
```properties
spring.h2.console.enabled=true
```

### Gradle no encontrado

Usa el wrapper:
```bash
chmod +x ./gradlew  # En Linux/Mac
./gradlew clean build
```

### Tests fallando

Ejecuta con más verbosidad:
```bash
./gradlew test -i
```

## 📚 Referencias

- [Spring Boot Official](https://spring.io/projects/spring-boot/)
- [Springdoc OpenAPI](https://springdoc.org/)
- [H2 Database](http://www.h2database.com/)
- [Render Docs](https://render.com/docs)
- [Gradle Documentation](https://docs.gradle.org/)

## 📝 Licencia

Apache 2.0 - Ver `LICENSE` file

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📧 Contacto

Para preguntas o soporte relacionado con pruebas, contacta a: **support@democlass.com**

---

**Última actualización**: Marzo 2026  
**Versión**: 1.0.0  
**Estado**: Listo para producción y pruebas QA