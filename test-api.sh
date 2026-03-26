#!/bin/bash

# API Base URL - CAMBIAR AQUI si usas diferente servidor
API_URL="https://demo-pos-backend-mnxr.onrender.com"

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir títulos
print_title() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

# Función para imprimir éxito
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Función para imprimir error
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Función para imprimir info
print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     BATERIA DE PRUEBAS - DEMO POS     ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo -e "\n${YELLOW}Base URL: ${API_URL}${NC}\n"

# ==================== TEST 1: HEALTH CHECK ====================
print_title "1️⃣  HEALTH CHECK"

HEALTH=$(curl -s "$API_URL/swagger-ui.html" -o /dev/null -w "%{http_code}")
if [ "$HEALTH" = "200" ]; then
    print_success "API está viva (HTTP $HEALTH)"
else
    print_error "API no responde (HTTP $HEALTH)"
fi

# ==================== TEST 2: CREAR CLIENTE ====================
print_title "2️⃣  CREAR CLIENTE"

CUSTOMER_RESPONSE=$(curl -s -X POST "$API_URL/api/customers" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan.perez@example.com",
    "phone": "5551234567"
  }')

CUSTOMER_ID=$(echo "$CUSTOMER_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

if [ ! -z "$CUSTOMER_ID" ]; then
    print_success "Cliente creado con ID: $CUSTOMER_ID"
    print_info "Nombre: Juan Pérez | Email: juan.perez@example.com"
else
    print_error "Fallo al crear cliente"
    echo "$CUSTOMER_RESPONSE"
fi

# ==================== TEST 3: OBTENER CLIENTES ====================
print_title "3️⃣  LISTAR CLIENTES"

CUSTOMERS=$(curl -s "$API_URL/api/customers")
CUSTOMER_COUNT=$(echo "$CUSTOMERS" | grep -o '"id"' | wc -l)

print_success "Total de clientes: $CUSTOMER_COUNT"

# ==================== TEST 4: CREAR PRODUCTO ====================
print_title "4️⃣  CREAR PRODUCTO"

PRODUCT_RESPONSE=$(curl -s -X POST "$API_URL/api/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell",
    "sku": "DELL-XPS-001",
    "price": 1299.99,
    "stock": 10
  }')

PRODUCT_ID=$(echo "$PRODUCT_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

if [ ! -z "$PRODUCT_ID" ]; then
    print_success "Producto creado con ID: $PRODUCT_ID"
    print_info "Nombre: Laptop Dell | Precio: \$1299.99 | Stock: 10"
else
    print_error "Fallo al crear producto"
    echo "$PRODUCT_RESPONSE"
fi

# ==================== TEST 5: LISTAR PRODUCTOS ====================
print_title "5️⃣  LISTAR PRODUCTOS"

PRODUCTS=$(curl -s "$API_URL/api/products")
PRODUCT_COUNT=$(echo "$PRODUCTS" | grep -o '"id"' | wc -l)

print_success "Total de productos: $PRODUCT_COUNT"

# ==================== TEST 6: CREAR VENTA ====================
print_title "6️⃣  CREAR VENTA"

if [ ! -z "$CUSTOMER_ID" ] && [ ! -z "$PRODUCT_ID" ]; then
    SALE_RESPONSE=$(curl -s -X POST "$API_URL/api/sales" \
      -H "Content-Type: application/json" \
      -d "{
        \"customerId\": $CUSTOMER_ID,
        \"items\": [
          {
            \"productId\": $PRODUCT_ID,
            \"quantity\": 2,
            \"unitPrice\": 1299.99
          }
        ]
      }")

    SALE_ID=$(echo "$SALE_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
    
    if [ ! -z "$SALE_ID" ]; then
        print_success "Venta creada con ID: $SALE_ID"
        TOTAL=$(echo "$SALE_RESPONSE" | grep -o '"totalAmount":[0-9.]*' | cut -d':' -f2)
        print_info "Total: \$$TOTAL | Cantidad: 2 unidades"
    else
        print_error "Fallo al crear venta"
        echo "$SALE_RESPONSE"
    fi
else
    print_error "No hay cliente o producto para crear venta"
fi

# ==================== TEST 7: LISTAR VENTAS ====================
print_title "7️⃣  LISTAR VENTAS"

SALES=$(curl -s "$API_URL/api/sales")
SALES_COUNT=$(echo "$SALES" | grep -o '"id"' | wc -l)

print_success "Total de ventas: $SALES_COUNT"

# ==================== TEST 8: RESUMEN DE CAJA ====================
print_title "8️⃣  RESUMEN DE CAJA"

CASH=$(curl -s "$API_URL/api/cash/summary")
TOTAL_SALES=$(echo "$CASH" | grep -o '"totalSales":[0-9.]*' | cut -d':' -f2)
BALANCE=$(echo "$CASH" | grep -o '"balance":[0-9.]*' | cut -d':' -f2)

print_success "Total Ventas: \$$TOTAL_SALES"
print_info "Balance: \$$BALANCE"

# ==================== TEST 9: MOVIMIENTOS DE CAJA ====================
print_title "9️⃣  MOVIMIENTOS DE CAJA"

MOVEMENTS=$(curl -s "$API_URL/api/cash/movements")
MOVEMENTS_COUNT=$(echo "$MOVEMENTS" | grep -o '"id"' | wc -l)

print_success "Total movimientos: $MOVEMENTS_COUNT"

# ==================== TEST 10: TEST ENDPOINTS ====================
print_title "🔟 ENDPOINTS DE PRUEBA"

print_info "Probando endpoint lento (3-5s)..."
TIME_START=$(date +%s%N | cut -b1-13)
SLOW=$(curl -s "$API_URL/api/test/slow-endpoint" -o /dev/null -w "%{http_code}")
TIME_END=$(date +%s%N | cut -b1-13)
TIME_DIFF=$((($TIME_END - $TIME_START) / 1000))

if [ "$SLOW" = "200" ]; then
    print_success "Slow endpoint respondió en ${TIME_DIFF}ms (HTTP $SLOW)"
else
    print_error "Slow endpoint falló (HTTP $SLOW)"
fi

print_info "Probando endpoint con validación..."
VALIDATION=$(curl -s -X POST "$API_URL/api/test/validation-error" \
  -H "Content-Type: application/json" \
  -d '{}' -w "\n%{http_code}")

HTTP_CODE=$(echo "$VALIDATION" | tail -1)
if [ "$HTTP_CODE" = "422" ]; then
    print_success "Validation error respondió correctamente (HTTP $HTTP_CODE)"
else
    print_error "Validation error falló (HTTP $HTTP_CODE)"
fi

# ==================== TEST 11: DOCUMENTACION ====================
print_title "📚 DOCUMENTACIÓN"

SWAGGER=$(curl -s "$API_URL/swagger-ui.html" -o /dev/null -w "%{http_code}")
API_DOCS=$(curl -s "$API_URL/api-docs" -o /dev/null -w "%{http_code}")

if [ "$SWAGGER" = "200" ]; then
    print_success "Swagger UI disponible: $API_URL/swagger-ui.html"
else
    print_error "Swagger UI no disponible (HTTP $SWAGGER)"
fi

if [ "$API_DOCS" = "200" ]; then
    print_success "OpenAPI JSON disponible: $API_URL/api-docs"
else
    print_error "OpenAPI JSON no disponible (HTTP $API_DOCS)"
fi

# ==================== RESUMEN FINAL ====================
print_title "📊 RESUMEN FINAL"

echo -e "${GREEN}✓ Todas las pruebas completadas${NC}"
echo ""
echo -e "${YELLOW}URLs importantes:${NC}"
echo -e "  API Base:        ${BLUE}$API_URL${NC}"
echo -e "  Swagger UI:      ${BLUE}$API_URL/swagger-ui.html${NC}"
echo -e "  OpenAPI JSON:    ${BLUE}$API_URL/api-docs${NC}"
echo -e "  H2 Console:      ${BLUE}$API_URL/h2-console${NC}"
echo ""
echo -e "${YELLOW}Endpoints disponibles:${NC}"
echo -e "  Clientes:        ${BLUE}GET/POST $API_URL/api/customers${NC}"
echo -e "  Productos:       ${BLUE}GET/POST $API_URL/api/products${NC}"
echo -e "  Ventas:          ${BLUE}GET/POST $API_URL/api/sales${NC}"
echo -e "  Caja:            ${BLUE}GET $API_URL/api/cash/summary${NC}"
echo ""
