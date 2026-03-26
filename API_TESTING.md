# Demo POS Backend - API Testing Examples

## Quick Links
- **Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

---

## 1. CUSTOMERS (Clientes)

### Create a customer
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan.perez@example.com",
    "phone": "555-1234"
  }'
```

### List all customers
```bash
curl http://localhost:8080/api/customers
```

### Get a specific customer
```bash
curl http://localhost:8080/api/customers/1
```

### Update customer (PUT - complete replacement)
```bash
curl -X PUT http://localhost:8080/api/customers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Carlos Pérez",
    "email": "juan.carlos@example.com",
    "phone": "555-5678",
    "status": "ACTIVE"
  }'
```

### Partial update (PATCH)
```bash
curl -X PATCH http://localhost:8080/api/customers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newemail@example.com"
  }'
```

### Delete customer (soft delete)
```bash
curl -X DELETE http://localhost:8080/api/customers/1
```

---

## 2. PRODUCTS (Productos)

### Create a product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Professional",
    "sku": "LAPTOP-PRO-001",
    "price": 1499.99,
    "stock": 50
  }'
```

### List all products
```bash
curl http://localhost:8080/api/products
```

### Get a specific product
```bash
curl http://localhost:8080/api/products/1
```

### Update product
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Professional V2",
    "sku": "LAPTOP-PRO-001",
    "price": 1599.99,
    "stock": 45,
    "status": "ACTIVE"
  }'
```

### Delete product (marks as DISCONTINUED)
```bash
curl -X DELETE http://localhost:8080/api/products/1
```

---

## 3. SALES (Ventas)

### Create a sale
```bash
curl -X POST http://localhost:8080/api/sales \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ]
  }'
```

### List all sales
```bash
curl http://localhost:8080/api/sales
```

### List sales for a specific customer
```bash
curl "http://localhost:8080/api/sales?customerId=1"
```

### Get a sale with details
```bash
curl http://localhost:8080/api/sales/1
```

### Cancel a sale
```bash
curl -X DELETE http://localhost:8080/api/sales/1
```

---

## 4. CASH MOVEMENTS (Movimientos de Caja)

### List all cash movements
```bash
curl http://localhost:8080/api/cash/movements
```

### Get cash summary
```bash
curl http://localhost:8080/api/cash/summary
```

### Create manual cash adjustment
```bash
curl -X POST http://localhost:8080/api/cash/movements \
  -H "Content-Type: application/json" \
  -d '{
    "type": "MANUAL_ADJUSTMENT",
    "amount": 100.50,
    "description": "Opening balance for the day"
  }'
```

---

## 5. TEST ENDPOINTS (Para pruebas de QA)

### Test slow endpoint (3-5 seconds delay)
```bash
curl http://localhost:8080/api/test/slow-endpoint
```

### Test random error (50% chance of 500 error)
```bash
curl http://localhost:8080/api/test/random-error
```

### Test validation error (always returns 422)
```bash
curl -X POST http://localhost:8080/api/test/validation-error
```

---

## ERROR CASES (Casos de error para testing)

### Duplicate email (409 Conflict)
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Another User",
    "email": "juan.perez@example.com",
    "phone": "555-9999"
  }'
```

### Invalid email format (400 Bad Request)
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "invalid-email",
    "phone": "555-1234"
  }'
```

### Not found (404)
```bash
curl http://localhost:8080/api/customers/9999
```

### Duplicate SKU (409 Conflict)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Another Laptop",
    "sku": "LAPTOP-PRO-001",
    "price": 999.99,
    "stock": 10
  }'
```

### Insufficient stock (400 Bad Request)
```bash
# Create a product with low stock
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Limited Item",
    "sku": "LIMITED-001",
    "price": 100.00,
    "stock": 2
  }'

# Try to create a sale with more items than available
curl -X POST http://localhost:8080/api/sales \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 3,
        "quantity": 5
      }
    ]
  }'
```

### Negative price (400 Bad Request)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Invalid Product",
    "sku": "INVALID-001",
    "price": -50.00,
    "stock": 10
  }'
```

### Negative stock (400 Bad Request)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Invalid Product",
    "sku": "INVALID-002",
    "price": 50.00,
    "stock": -10
  }'
```

### Zero amount for cash adjustment (400 Bad Request)
```bash
curl -X POST http://localhost:8080/api/cash/movements \
  -H "Content-Type: application/json" \
  -d '{
    "type": "MANUAL_ADJUSTMENT",
    "amount": 0.00,
    "description": "Invalid"
  }'
```

---

## BASH SCRIPT FOR AUTOMATED TESTING

Save this as `test-api.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "Testing Demo POS Backend API"
echo "============================"

# Test 1: Create customer
echo "1. Creating customer..."
CUSTOMER_ID=$(curl -s -X POST $BASE_URL/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","phone":"555-1234"}' | jq '.id')
echo "   Customer created: $CUSTOMER_ID"

# Test 2: Create product
echo "2. Creating product..."
PRODUCT_ID=$(curl -s -X POST $BASE_URL/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","sku":"TEST-001","price":100.00,"stock":10}' | jq '.id')
echo "   Product created: $PRODUCT_ID"

# Test 3: Create sale
echo "3. Creating sale..."
SALE_ID=$(curl -s -X POST $BASE_URL/api/sales \
  -H "Content-Type: application/json" \
  -d "{\"customerId\":$CUSTOMER_ID,\"items\":[{\"productId\":$PRODUCT_ID,\"quantity\":2}]}" | jq '.id')
echo "   Sale created: $SALE_ID"

# Test 4: Get cash summary
echo "4. Getting cash summary..."
curl -s $BASE_URL/api/cash/summary | jq '.'

echo ""
echo "✅ All tests completed!"
```

---

## PERFORMANCE TESTING

### Test timeouts
```bash
# This endpoint will delay 3-5 seconds
time curl http://localhost:8080/api/test/slow-endpoint
```

### Test retry logic
```bash
# Run 10 times to see random successes/failures
for i in {1..10}; do
  echo "Attempt $i:"
  curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/test/random-error
done
```

---

## NOTES

- All requests should use `Content-Type: application/json` for POST/PUT/PATCH
- Responses are in JSON format
- Use `jq` command to format JSON responses prettily
- For testing, use customer ID = 1 and product ID = 1 after initial creation
- H2 database data is volatile (resets on restart)
