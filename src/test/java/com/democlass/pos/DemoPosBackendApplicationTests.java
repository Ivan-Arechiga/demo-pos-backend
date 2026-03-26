package com.democlass.pos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DemoPosBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCustomerCreation() throws Exception {
        String customerJson = """
            {
                "name": "John Doe",
                "email": "john@example.com",
                "phone": "123456789"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.name", equalTo("John Doe")))
            .andExpect(jsonPath("$.email", equalTo("john@example.com")));
    }

    @Test
    public void testProductCreation() throws Exception {
        String productJson = """
            {
                "name": "Test Product",
                "sku": "TEST-001",
                "price": 19.99,
                "stock": 100
            }
            """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.name", equalTo("Test Product")))
            .andExpect(jsonPath("$.sku", equalTo("TEST-001")));
    }

    @Test
    public void testGetNonExistentCustomer() throws Exception {
        mockMvc.perform(get("/api/customers/9999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", equalTo("Not Found")))
            .andExpect(jsonPath("$.status", equalTo(404)));
    }

    @Test
    public void testSlowEndpoint() throws Exception {
        long start = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/test/slow-endpoint"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", notNullValue()));
        
        long elapsed = System.currentTimeMillis() - start;
        assert elapsed >= 3000; // Should take at least 3 seconds
    }

    @Test
    public void testValidationErrorEndpoint() throws Exception {
        mockMvc.perform(post("/api/test/validation-error")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status", equalTo(422)))
            .andExpect(jsonPath("$.validationErrors", hasSize(greaterThan(0))));
    }

    @Test
    public void testSwaggerEndpoint() throws Exception {
        // Test that the API documentation is available
        mockMvc.perform(get("/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.openapi", notNullValue()));
    }
}
