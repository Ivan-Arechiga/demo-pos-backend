# Test API Script - Batería de Pruebas

Este archivo contiene un script de pruebas completo y documentado para validar todos los servicios del API.

## 🚀 Uso Rápido

### Cambiar URL del servidor

El script usa una variable `API_URL` que se puede cambiar fácilmente:

```bash
# En el archivo test-api.sh, cambiar la línea 4:
API_URL="https://demo-pos-backend-mnxr.onrender.com"  # ← CAMBIAR AQUI

# O ejecutar con variable de entorno:
API_URL="https://tu-nuevo-servidor.com" ./test-api.sh
```

## 📋 Casos de Prueba

El script `test-api.sh` ejecuta 11 baterías de pruebas:

### 1️⃣ **Health Check**
Verifica que el API está vivo y respondiendo

```bash
curl -s "$API_URL/swagger-ui.html" -o /dev/null -w "%{http_code}"
```

### 2️⃣ **Crear Cliente**
Crea un nuevo cliente con nombre, email y teléfono

```bash
POST /api/customers
{
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "phone": "5551234567"
}
```

### 3️⃣ **Listar Clientes**
Obtiene lista de todos los clientes

```bash
GET /api/customers
```

### 4️⃣ **Crear Producto**
Crea un nuevo producto con precio y stock

```bash
POST /api/products
{
  "name": "Laptop Dell",
  "sku": "DELL-XPS-001",
  "price": 1299.99,
  "stock": 10
}
```

### 5️⃣ **Listar Productos**
Obtiene lista de todos los productos

```bash
GET /api/products
```

### 6️⃣ **Crear Venta**
Crea una venta con items (compra producto)

```bash
POST /api/sales
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "unitPrice": 1299.99
    }
  ]
}
```

### 7️⃣ **Listar Ventas**
Obtiene lista de todas las ventas

```bash
GET /api/sales
```

### 8️⃣ **Resumen de Caja**
Obtiene el resumen financiero (total ventas, refundos, balance)

```bash
GET /api/cash/summary
```

**Respuesta esperada:**
```json
{
  "totalSales": 2599.98,
  "totalRefunds": 0,
  "balance": 2599.98
}
```

### 9️⃣ **Movimientos de Caja**
Lista todos los movimientos de caja

```bash
GET /api/cash/movements
```

### 🔟 **Endpoints de Prueba (QA)**

**• Endpoint Lento** (simula timeout)
```bash
GET /api/test/slow-endpoint
# Respuesta: 200 OK después de 3-5 segundos
```

**• Endpoint con Validación** (error intencional)
```bash
POST /api/test/validation-error
# Respuesta: 422 Unprocessable Entity
```

**• Endpoint Aleatorio** (50/50 success/error)
```bash
GET /api/test/random-error
# Respuesta: 50% 200 OK, 50% 500 Internal Server Error
```

### 📚 **Documentación**

Acceder a:
- Swagger UI: `{BASE_URL}/swagger-ui.html`
- OpenAPI JSON: `{BASE_URL}/api-docs`

---

## 🎯 Ejemplo de Ejecución

### Local

```bash
# Cambiar URL en el script
API_URL="http://localhost:8080" ./test-api.sh
```

### En Render

```bash
# La URL ya está configurada
./test-api.sh
```

### Con variable de entorno

```bash
API_URL="https://demo-pos-backend-mnxr.onrender.com" ./test-api.sh
```

---

## 📊 Resultados Esperados

✅ Todos los clientes creados

✅ Todos los productos disponibles con stock actualizado

✅ Ventas registradas correctamente

✅ Movimientos de caja sincronizados

✅ Balance correcto

---

## 🔧 Personalización

### Cambiar servidor

```bash
# Opción 1: Editar el script
sed -i 's|https://demo-pos-backend-mnxr.onrender.com|https://tu-servidor.com|g' test-api.sh

# Opción 2: Variable de entorno
API_URL="https://tu-servidor.com" ./test-api.sh

# Opción 3: Editar manualmente (línea 4 del script)
API_URL="https://tu-servidor.com"
```

### Agregar más pruebas

Editar `test-api.sh` y agregar nuevas secciones siguiendo el patrón:

```bash
# ==================== TEST X: NOMBRE ====================
print_title "X️⃣ NOMBRE"

# Tu prueba aquí...
```

---

## 📋 Checklist de Testing

- [ ] Health check: API respondiendo
- [ ] Crear cliente: Exitoso
- [ ] Listar clientes: Múltiples registros
- [ ] Crear producto: Con stock inicial
- [ ] Listar productos: Stock actualizado
- [ ] Crear venta: Con deducción de stock
- [ ] Listar ventas: Transacciones registradas
- [ ] Resumen caja: Balance correcto
- [ ] Movimientos caja: Historial completo
- [ ] Endpoints QA: Funcionando correctamente
- [ ] Documentación: Accessible (Swagger)

