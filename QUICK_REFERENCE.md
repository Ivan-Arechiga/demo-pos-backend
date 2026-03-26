# Demo POS Backend - Quick Reference Card

## 🚀 QUICK START

### 1. **Clone & Navigate**
```bash
git clone https://github.com/Ivan-Arechiga/demo-pos-backend.git
cd demo-pos-backend
```

### 2. **Run Application**
```bash
# Option A: Using Gradle directly
gradle bootRun

# Option B: Using wrapper script
./run.sh

# Option C: Using pre-built JAR
java -jar build/libs/demo-pos-backend-1.0.0.jar
```

### 3. **Open in Browser**
- 🔗 API Docs: http://localhost:8080/swagger-ui.html
- 📊 Database: http://localhost:8080/h2-console

---

## 📋 API ENDPOINTS (Quick Reference)

| Resource | Method | Endpoint | Status Codes |
|----------|--------|----------|--------------|
| **Customers** | GET | `/api/customers` | 200 |
| | POST | `/api/customers` | 201, 400, 409 |
| | GET | `/api/customers/{id}` | 200, 404 |
| | PUT | `/api/customers/{id}` | 200, 400, 404, 409 |
| | PATCH | `/api/customers/{id}` | 200, 404, 409 |
| | DELETE | `/api/customers/{id}` | 204, 404 |
| **Products** | GET | `/api/products` | 200 |
| | POST | `/api/products` | 201, 400, 409 |
| | GET | `/api/products/{id}` | 200, 404 |
| | PUT | `/api/products/{id}` | 200, 400, 404, 409 |
| | PATCH | `/api/products/{id}` | 200, 404, 409 |
| | DELETE | `/api/products/{id}` | 204, 404 |
| **Sales** | GET | `/api/sales` | 200 |
| | POST | `/api/sales` | 201, 400, 404 |
| | GET | `/api/sales/{id}` | 200, 404 |
| | GET | `/api/sales?customerId=X` | 200, 404 |
| | DELETE | `/api/sales/{id}` | 204, 400, 404 |
| **Cash** | GET | `/api/cash/movements` | 200 |
| | GET | `/api/cash/summary` | 200 |
| | POST | `/api/cash/movements` | 201, 400 |
| **Tests** | GET | `/api/test/slow-endpoint` | 200 (3-5s) |
| | GET | `/api/test/random-error` | 200 or 500 |
| | POST | `/api/test/validation-error` | 422 |

---

## 🔐 ERROR CODES

| Code | Meaning | Typical Cause |
|------|---------|---------------|
| **400** | Bad Request | Invalid data (email format, price<0, etc.) |
| **404** | Not Found | Resource doesn't exist |
| **409** | Conflict | Email/SKU duplicate |
| **422** | Validation Error | Field validation failures (with details) |
| **500** | Server Error | Unexpected error |

---

## 📝 EXAMPLE REQUESTS

### Create Customer
```json
POST /api/customers
{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "555-1234"
}
```

### Create Product
```json
POST /api/products
{
  "name": "Laptop",
  "sku": "LAPTOP-001",
  "price": 999.99,
  "stock": 50
}
```

### Create Sale
```json
POST /api/sales
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

### Add Cash Adjustment
```json
POST /api/cash/movements
{
  "type": "MANUAL_ADJUSTMENT",
  "amount": 100.50,
  "description": "Opening balance"
}
```

---

## 🏗️ PROJECT STRUCTURE

```
demo-pos-backend/
├── src/main/java/com/democlass/pos/
│   ├── entity/              (5 entities: Customer, Product, Sale, SaleItem, CashMovement)
│   ├── dto/                 (14 DTOs: request, response, mapping)
│   ├── controller/          (5 REST controllers)
│   ├── service/             (4 business logic services)
│   ├── repository/          (4 JPA repositories)
│   ├── exception/           (7 exceptions + global handler)
│   └── config/              (OpenAPI configuration)
├── src/main/resources/
│   └── application.properties
├── src/test/java/
│   └── DemoPosBackendApplicationTests.java
├── build.gradle.kts         (Build configuration)
├── Dockerfile               (For containerized deployment)
├── README.md                (Complete documentation)
├── API_TESTING.md           (API examples)
└── run.sh                   (Quick start script)
```

---

## 🛠️ GRADLE COMMANDS

```bash
# Build
gradle clean build

# Run application
gradle bootRun

# Run tests only
gradle test

# View test report
gradle build && open build/reports/tests/test/index.html

# List all tasks
gradle tasks
```

---

## 🧪 TESTING TIPS

### Positive Testing (Happy Path)
```bash
# 1. Create customer
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com","phone":"123"}'

# 2. Create product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","sku":"TEST-1","price":100,"stock":10}'

# 3. Create sale
curl -X POST http://localhost:8080/api/sales \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"items":[{"productId":1,"quantity":2}]}'

# 4. Check cash
curl http://localhost:8080/api/cash/summary
```

### Negative Testing (Error Cases)
```bash
# Duplicate email (409)
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Test2","email":"test@test.com","phone":"123"}'

# Not found (404)
curl http://localhost:8080/api/customers/9999

# Invalid email (400)
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"invalid","phone":"123"}'

# Insufficient stock (400)
# (Create sale with quantity > stock)
```

### Performance Testing
```bash
# Test timeout handling
curl http://localhost:8080/api/test/slow-endpoint

# Test error resilience (run multiple times)
for i in {1..10}; do curl -s -o /dev/null -w "%{http_code}\n" \
  http://localhost:8080/api/test/random-error; done

# Test validation errors
curl -X POST http://localhost:8080/api/test/validation-error
```

---

## 🌐 DEPLOYMENT

### Local Development
```bash
./run.sh
# Open: http://localhost:8080/swagger-ui.html
```

### Docker
```bash
# Build Docker image
docker build -t demo-pos-backend .

# Run container
docker run -p 8080:8080 demo-pos-backend
```

### Render (Cloud)
1. Push to GitHub
2. Create Web Service on Render
3. Configure:
   - Build: `./gradlew clean build`
   - Start: `java -jar build/libs/demo-pos-backend-1.0.0.jar`
4. Deploy!
5. Access: `https://your-app-name.onrender.com`

---

## 🔍 USEFUL LINKS

- 📖 Swagger UI: http://localhost:8080/swagger-ui.html
- 🔗 OpenAPI JSON: http://localhost:8080/api-docs
- 💾 H2 Console: http://localhost:8080/h2-console
- 📚 Full Docs: README.md
- 🧪 API Examples: API_TESTING.md
- 🐳 Docker Build: Dockerfile

---

## ⚙️ CONFIGURATION

### Port Configuration
Edit `src/main/resources/application.properties`:
```properties
server.port=8080
```

Or set environment variable:
```bash
export PORT=9000 && java -jar build/libs/demo-pos-backend-1.0.0.jar
```

### Database
Default: H2 in-memory (jdbc:h2:mem:posdb)
Data is lost on restart.

For persistence, use H2 file:
```properties
spring.datasource.url=jdbc:h2:file:./data/posdb
```

---

## 📞 SUPPORT

- Issues: Check README.md, API_TESTING.md
- Tests: Run `gradle test`
- Errors: Check `build/reports/tests/test/index.html`
- Email: support@democlass.com

---

**Version**: 1.0.0 | **Status**: ✅ Production Ready | **Last Updated**: March 2026
