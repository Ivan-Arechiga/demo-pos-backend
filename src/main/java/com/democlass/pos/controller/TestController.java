package com.democlass.pos.controller;

import com.democlass.pos.dto.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test Endpoints", description = "Endpoints especiales para pruebas QA. NO representan funcionalidad real del sistema.")
public class TestController {

    private final Random random = new Random();

    @GetMapping("/slow-endpoint")
    @Operation(summary = "Endpoint lento para pruebas de timeout",
        description = "Responde exitosamente pero después de 3-5 segundos. Usado para probar timeouts y performance.")
    @ApiResponse(responseCode = "200", description = "Respuesta exitosa después del delay")
    public ResponseEntity<Map<String, Object>> slowEndpoint() throws InterruptedException {
        int delay = 3000 + random.nextInt(2000); // 3-5 segundos
        Thread.sleep(delay);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Slow endpoint responded after " + delay + "ms");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/random-error")
    @Operation(summary = "Endpoint con error aleatorio",
        description = "Retorna 200 OK o 500 Internal Server Error aleatoriamente (50/50). Usado para probar manejo de errores intermitentes.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Éxito (50% de probabilidad)"),
        @ApiResponse(responseCode = "500", description = "Error del servidor (50% de probabilidad)")
    })
    public ResponseEntity<Map<String, Object>> randomError() {
        boolean shouldFail = random.nextBoolean();
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        
        if (shouldFail) {
            response.put("message", "Random error occurred");
            response.put("status", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } else {
            response.put("message", "Random success");
            response.put("status", 200);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/validation-error")
    @Operation(summary = "Endpoint que siempre falla con error de validación",
        description = "Retorna 422 Unprocessable Entity con ejemplo de errores de validación. Usado para probar manejo de validaciones.")
    @ApiResponse(responseCode = "422", description = "Siempre falla con errores de validación")
    public ResponseEntity<ValidationErrorResponseDTO> validationError() {
        ValidationErrorResponseDTO response = new ValidationErrorResponseDTO();
        response.setStatus(422);
        response.setError("Unprocessable Entity");
        response.setTimestamp(LocalDateTime.now());
        response.setPath("/api/test/validation-error");
        response.setValidationErrors(Arrays.asList(
            new ValidationErrorResponseDTO.ValidationError("email", "Email must be valid", "invalid-email"),
            new ValidationErrorResponseDTO.ValidationError("name", "Name is required", ""),
            new ValidationErrorResponseDTO.ValidationError("price", "Price must be greater than 0", "-10.50")
        ));
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }
}
