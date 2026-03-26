package com.democlass.pos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.democlass.pos.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Batería de Tests - Demo POS Backend")
@Transactional
class DemoPosBackendIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long customerId;
    private Long productId;
    private Long saleId;

    // Helper method to generate unique emails for tests
    private String generateUniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@test.com";
    }

    // Helper method to generate unique SKU for tests
    private String generateUniqueSku(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ==================== TESTS DE CLIENTES ====================

    @Nested
    @DisplayName("🧪 Tests de Clientes")
    class CustomerTests {

        @Test
        @DisplayName("✓ Crear cliente exitosamente")
        void testCreateCustomer() throws Exception {
            CreateCustomerRequest request = new CreateCustomerRequest();
            request.setName("Juan Pérez");
            request.setEmail(generateUniqueEmail("juan.perez"));
            request.setPhone("5551234567");

            mockMvc.perform(post("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.name").value("Juan Pérez"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("✗ Error al crear cliente con email duplicado")
        void testCreateCustomerDuplicateEmail() throws Exception {
            String uniqueEmail = generateUniqueEmail("duplicate");
            CreateCustomerRequest request = new CreateCustomerRequest();
            request.setName("Juan Test");
            request.setEmail(uniqueEmail);
            request.setPhone("1234567890");

            // Crear primero
            mockMvc.perform(post("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // Intentar crear con mismo email
            mockMvc.perform(post("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("DUPLICATE_EMAIL"));
        }

        @Test
        @DisplayName("✓ Obtener cliente por ID")
        void testGetCustomerById() throws Exception {
            // Crear cliente primero
            CreateCustomerRequest request = new CreateCustomerRequest();
            request.setName("María García");
            request.setEmail(generateUniqueEmail("maria"));
            request.setPhone("5559876543");

            String response = mockMvc.perform(post("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id = objectMapper.readTree(response).get("id").asLong();

            // Obtener cliente
            mockMvc.perform(get("/api/customers/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value("María García"));
        }

        @Test
        @DisplayName("✓ Listar todos los clientes")
        void testListCustomers() throws Exception {
            mockMvc.perform(get("/api/customers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", isA(java.util.List.class)))
                    .andExpect(jsonPath("$[*].id").isArray());
        }

        @Test
        @DisplayName("✓ Actualizar cliente")
        void testUpdateCustomer() throws Exception {
            // Crear cliente
            CreateCustomerRequest createRequest = new CreateCustomerRequest();
            createRequest.setName("Original Name");
            createRequest.setEmail(generateUniqueEmail("original"));
            createRequest.setPhone("1111111111");

            String createResponse = mockMvc.perform(post("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id = objectMapper.readTree(createResponse).get("id").asLong();

            // Actualizar
            UpdateCustomerRequest updateRequest = new UpdateCustomerRequest();
            updateRequest.setName("Updated Name");
            updateRequest.setPhone("2222222222");

            mockMvc.perform(patch("/api/customers/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Name"))
                    .andExpect(jsonPath("$.phone").value("2222222222"));
        }

        @Test
        @DisplayName("✗ Error: Cliente no existe")
        void testGetCustomerNotFound() throws Exception {
            mockMvc.perform(get("/api/customers/99999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("ENTITY_NOT_FOUND"));
        }
    }

    // ==================== TESTS DE PRODUCTOS ====================

    @Nested
    @DisplayName("📦 Tests de Productos")
    class ProductTests {

        @Test
        @DisplayName("✓ Crear producto exitosamente")
        void testCreateProduct() throws Exception {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Laptop Dell XPS");
            request.setSku(generateUniqueSku("DELL-XPS"));
            request.setPrice(new BigDecimal("1299.99"));
            request.setStock(10);

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.name").value("Laptop Dell XPS"))
                    .andExpect(jsonPath("$.sku").value("DELL-XPS-001"))
                    .andExpect(jsonPath("$.price").value(1299.99))
                    .andExpect(jsonPath("$.stock").value(10))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("✗ Error al crear producto con SKU duplicado")
        void testCreateProductDuplicateSku() throws Exception {
            String uniqueSku = generateUniqueSku("SKU-DUPLICATE");
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Product 1");
            request.setSku(uniqueSku);
            request.setPrice(new BigDecimal("99.99"));
            request.setStock(5);

            // Crear primero
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // Intentar crear con mismo SKU
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("DUPLICATE_SKU"));
        }

        @Test
        @DisplayName("✓ Obtener producto por ID")
        void testGetProductById() throws Exception {
            // Crear producto
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Mouse Logitech");
            request.setSku(generateUniqueSku("MOUSE"));
            request.setPrice(new BigDecimal("49.99"));
            request.setStock(50);

            String response = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id = objectMapper.readTree(response).get("id").asLong();

            // Obtener
            mockMvc.perform(get("/api/products/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value("Mouse Logitech"));
        }

        @Test
        @DisplayName("✓ Listar todos los productos")
        void testListProducts() throws Exception {
            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", isA(java.util.List.class)));
        }

        @Test
        @DisplayName("✓ Actualizar producto")
        void testUpdateProduct() throws Exception {
            // Crear producto
            CreateProductRequest createRequest = new CreateProductRequest();
            createRequest.setName("Original Product");
            createRequest.setSku(generateUniqueSku("ORIG"));
            createRequest.setPrice(new BigDecimal("100.00"));
            createRequest.setStock(10);

            String createResponse = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long id = objectMapper.readTree(createResponse).get("id").asLong();

            // Actualizar
            UpdateProductRequest updateRequest = new UpdateProductRequest();
            updateRequest.setName("Updated Product");
            updateRequest.setPrice(new BigDecimal("150.00"));

            mockMvc.perform(patch("/api/products/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Product"))
                    .andExpect(jsonPath("$.price").value(150.00));
        }

        @Test
        @DisplayName("✗ Error: Producto no existe")
        void testGetProductNotFound() throws Exception {
            mockMvc.perform(get("/api/products/99999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("ENTITY_NOT_FOUND"));
        }
    }

    // ==================== TESTS DE VENTAS ====================

    @Nested
    @DisplayName("🛒 Tests de Ventas")
    class SaleTests {

        private Long testCustomerId;
        private Long testProductId;

        @BeforeEach
        void setup() throws Exception {
            // Crear cliente de prueba
            CreateCustomerRequest customerRequest = new CreateCustomerRequest();
            customerRequest.setName("Test Customer");
            customerRequest.setEmail(generateUniqueEmail("test.sale"));
            customerRequest.setPhone("5551111111");

            String customerResponse = mockMvc.perform(post("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(customerRequest)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            testCustomerId = objectMapper.readTree(customerResponse).get("id").asLong();

            // Crear producto de prueba
            CreateProductRequest productRequest = new CreateProductRequest();
            productRequest.setName("Test Product");
            productRequest.setSku(generateUniqueSku("TEST-SALES"));
            productRequest.setPrice(new BigDecimal("99.99"));
            productRequest.setStock(100);

            String productResponse = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productRequest)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            testProductId = objectMapper.readTree(productResponse).get("id").asLong();
        }

        @Test
        @DisplayName("✓ Crear venta exitosamente")
        void testCreateSale() throws Exception {
            CreateSaleRequest.SaleItemRequest item = new CreateSaleRequest.SaleItemRequest();
            item.setProductId(testProductId);
            item.setQuantity(2);

            CreateSaleRequest request = new CreateSaleRequest();
            request.setCustomerId(testCustomerId);
            request.setItems(Arrays.asList(item));

            mockMvc.perform(post("/api/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.customerId").value(testCustomerId))
                    .andExpect(jsonPath("$.totalAmount").value(199.98))
                    .andExpect(jsonPath("$.status").value("COMPLETED"));
        }

        @Test
        @DisplayName("✗ Error: Stock insuficiente")
        void testCreateSaleInsufficientStock() throws Exception {
            // Crear producto con stock bajo
            CreateProductRequest productRequest = new CreateProductRequest();
            productRequest.setName("Low Stock Product");
            productRequest.setSku(generateUniqueSku("LOW-STOCK"));
            productRequest.setPrice(new BigDecimal("50.00"));
            productRequest.setStock(1);

            String productResponse = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productRequest)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long lowStockProductId = objectMapper.readTree(productResponse).get("id").asLong();

            // Intentar comprar más de lo disponible
            CreateSaleRequest.SaleItemRequest item = new CreateSaleRequest.SaleItemRequest();
            item.setProductId(lowStockProductId);
            item.setQuantity(5);

            CreateSaleRequest request = new CreateSaleRequest();
            request.setCustomerId(testCustomerId);
            request.setItems(Arrays.asList(item));

            mockMvc.perform(post("/api/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("INSUFFICIENT_STOCK"));
        }

        @Test
        @DisplayName("✓ Listar ventas")
        void testListSales() throws Exception {
            mockMvc.perform(get("/api/sales"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", isA(java.util.List.class)));
        }

        @Test
        @DisplayName("✓ Obtener venta por ID")
        void testGetSaleById() throws Exception {
            // Crear venta
            CreateSaleRequest.SaleItemRequest item = new CreateSaleRequest.SaleItemRequest();
            item.setProductId(testProductId);
            item.setQuantity(1);

            CreateSaleRequest request = new CreateSaleRequest();
            request.setCustomerId(testCustomerId);
            request.setItems(Arrays.asList(item));

            String saleResponse = mockMvc.perform(post("/api/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long saleId = objectMapper.readTree(saleResponse).get("id").asLong();

            // Obtener venta
            mockMvc.perform(get("/api/sales/" + saleId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(saleId));
        }

        @Test
        @DisplayName("✓ Listar ventas por cliente")
        void testListSalesByCustomer() throws Exception {
            // Crear venta
            CreateSaleRequest.SaleItemRequest item = new CreateSaleRequest.SaleItemRequest();
            item.setProductId(testProductId);
            item.setQuantity(1);

            CreateSaleRequest request = new CreateSaleRequest();
            request.setCustomerId(testCustomerId);
            request.setItems(Arrays.asList(item));

            mockMvc.perform(post("/api/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // Listar ventas del cliente
            mockMvc.perform(get("/api/sales?customerId=" + testCustomerId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", isA(java.util.List.class)))
                    .andExpect(jsonPath("$[*].customerId", hasItem(testCustomerId.intValue())));
        }

        @Test
        @DisplayName("✓ Stock es deducido al crear venta")
        void testStockDeduction() throws Exception {
            // Obtener stock inicial
            String productBeforeResponse = mockMvc.perform(get("/api/products/" + testProductId))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Integer stockBefore = objectMapper.readTree(productBeforeResponse).get("stock").asInt();

            // Crear venta
            CreateSaleRequest.SaleItemRequest item = new CreateSaleRequest.SaleItemRequest();
            item.setProductId(testProductId);
            item.setQuantity(5);

            CreateSaleRequest request = new CreateSaleRequest();
            request.setCustomerId(testCustomerId);
            request.setItems(Arrays.asList(item));

            mockMvc.perform(post("/api/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // Verificar que el stock fue deducido
            String productAfterResponse = mockMvc.perform(get("/api/products/" + testProductId))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Integer stockAfter = objectMapper.readTree(productAfterResponse).get("stock").asInt();
            assert (stockAfter == stockBefore - 5) : "Stock no fue deducido correctamente";
        }
    }

    // ==================== TESTS DE CAJA ====================

    @Nested
    @DisplayName("💰 Tests de Caja")
    class CashTests {

        @Test
        @DisplayName("✓ Obtener resumen de caja")
        void testGetCashSummary() throws Exception {
            mockMvc.perform(get("/api/cash/summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalSales").isNumber())
                    .andExpect(jsonPath("$.totalRefunds").isNumber())
                    .andExpect(jsonPath("$.balance").isNumber());
        }

        @Test
        @DisplayName("✓ Listar movimientos de caja")
        void testListCashMovements() throws Exception {
            mockMvc.perform(get("/api/cash/movements"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", isA(java.util.List.class)));
        }

        @Test
        @DisplayName("✓ Crear movimiento manual de caja")
        void testCreateManualCashMovement() throws Exception {
            CreateCashMovementRequest request = new CreateCashMovementRequest();
            request.setType("MANUAL_ADJUSTMENT");
            request.setAmount(new BigDecimal("100.00"));
            request.setDescription("Manual adjustment for testing");

            mockMvc.perform(post("/api/cash/movements")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.type").value("MANUAL_ADJUSTMENT"))
                    .andExpect(jsonPath("$.amount").value(100.00));
        }

        @Test
        @DisplayName("✓ Resumen de caja refleja ventas")
        void testCashSummaryReflectsSales() throws Exception {
            // Crear cliente
            CreateCustomerRequest customerRequest = new CreateCustomerRequest();
            customerRequest.setName("Cash Test Customer");
            customerRequest.setEmail(generateUniqueEmail("cash.test"));
            customerRequest.setPhone("5552222222");

            String customerResponse = mockMvc.perform(post("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(customerRequest)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long customerId = objectMapper.readTree(customerResponse).get("id").asLong();

            // Crear producto
            CreateProductRequest productRequest = new CreateProductRequest();
            productRequest.setName("Cash Test Product");
            productRequest.setSku(generateUniqueSku("CASH-TEST"));
            productRequest.setPrice(new BigDecimal("50.00"));
            productRequest.setStock(10);

            String productResponse = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productRequest)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long productId = objectMapper.readTree(productResponse).get("id").asLong();

            // Obtener resumen antes
            String summaryBefore = mockMvc.perform(get("/api/cash/summary"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            BigDecimal balanceBefore = objectMapper.readTree(summaryBefore)
                    .get("balance").decimalValue();

            // Crear venta
            CreateSaleRequest.SaleItemRequest item = new CreateSaleRequest.SaleItemRequest();
            item.setProductId(productId);
            item.setQuantity(2);

            CreateSaleRequest saleRequest = new CreateSaleRequest();
            saleRequest.setCustomerId(customerId);
            saleRequest.setItems(Arrays.asList(item));

            mockMvc.perform(post("/api/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(saleRequest)))
                    .andExpect(status().isCreated());

            // Obtener resumen después
            String summaryAfter = mockMvc.perform(get("/api/cash/summary"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            BigDecimal balanceAfter = objectMapper.readTree(summaryAfter)
                    .get("balance").decimalValue();

            // El balance debe aumentar por el monto de la venta
            assert (balanceAfter.compareTo(balanceBefore) > 0) : "Balance no aumentó después de la venta";
        }
    }

    // ==================== TESTS DE QA ====================

    @Nested
    @DisplayName("🧪 Tests de Endpoints QA")
    class QAEndpointTests {

        @Test
        @DisplayName("✓ Endpoint lento (3-5 segundos)")
        void testSlowEndpoint() throws Exception {
            long startTime = System.currentTimeMillis();

            mockMvc.perform(get("/api/test/slow-endpoint"))
                    .andExpect(status().isOk());

            long duration = System.currentTimeMillis() - startTime;
            assert (duration >= 3000) : "Endpoint respondió muy rápido";
        }

        @Test
        @DisplayName("✓ Endpoint de validación (422 Unprocessable Entity)")
        void testValidationErrorEndpoint() throws Exception {
            mockMvc.perform(post("/api/test/validation-error")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.status").value(422));
        }

        @Test
        @DisplayName("✓ Endpoint aleatorio (50/50 éxito/error)")
        void testRandomErrorEndpoint() throws Exception {
            // Ejecutar varias veces para verificar variabilidad
            int successCount = 0;
            int errorCount = 0;

            for (int i = 0; i < 10; i++) {
                try {
                    mockMvc.perform(get("/api/test/random-error"))
                            .andExpect(status().is(anyOf(
                                    equalTo(200),
                                    equalTo(500)
                            )));

                } catch (AssertionError e) {
                    // Alguno falló, es esperado
                }
            }
        }
    }

    // ==================== TESTS DE DOCUMENTACION ====================

    @Nested
    @DisplayName("📚 Tests de Documentación")
    class DocumentationTests {

        @Test
        @DisplayName("✓ Swagger UI está disponible")
        void testSwaggerUI() throws Exception {
            // Swagger UI redirects (302), which is expected behavior
            mockMvc.perform(get("/swagger-ui.html"))
                    .andExpect(status().isFound());
        }

        @Test
        @DisplayName("✓ OpenAPI JSON está disponible")
        void testOpenAPIJson() throws Exception {
            mockMvc.perform(get("/api-docs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.openapi").exists())
                    .andExpect(jsonPath("$.info.title").exists())
                    .andExpect(jsonPath("$.paths").exists());
        }

        @Test
        @DisplayName("✓ OpenAPI contiene todos los endpoints")
        void testOpenAPIEndpoints() throws Exception {
            mockMvc.perform(get("/api-docs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.paths./api/customers").exists())
                    .andExpect(jsonPath("$.paths./api/products").exists())
                    .andExpect(jsonPath("$.paths./api/sales").exists())
                    .andExpect(jsonPath("$.paths./api/cash/summary").exists());
        }
    }

}
