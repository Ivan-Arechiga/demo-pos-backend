package com.democlass.pos.controller;

import com.democlass.pos.dto.CreateSaleRequest;
import com.democlass.pos.dto.SaleDTO;
import com.democlass.pos.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sales", description = "API para gestionar ventas en el sistema POS")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    @Operation(summary = "Obtener lista de ventas", description = "Retorna todas las ventas registradas. Opcionalmente filtrar por customerId")
    @ApiResponse(responseCode = "200", description = "Lista de ventas obtenida exitosamente")
    public ResponseEntity<List<SaleDTO>> getAllSales(
            @Parameter(description = "ID del cliente para filtrar ventas (opcional)")
            @RequestParam(required = false) Long customerId) {
        if (customerId != null) {
            return ResponseEntity.ok(saleService.getSalesByCustomerId(customerId));
        }
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Retorna los detalles completos de una venta con sus líneas de producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta encontrada"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    public ResponseEntity<SaleDTO> getSaleById(
            @Parameter(description = "ID de la venta a buscar")
            @PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva venta", description = "Registra una nueva venta. Valida disponibilidad de productos, deduce stock, y registra movimiento de caja automáticamente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Venta creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (producto sin stock, cantidad inválida, etc.)"),
        @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado")
    })
    public ResponseEntity<SaleDTO> createSale(
            @Valid @RequestBody CreateSaleRequest request) {
        SaleDTO created = saleService.createSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar venta", description = "Cancela una venta, restaura stock y registra movimiento de reembolso automáticamente")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Venta cancelada"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
        @ApiResponse(responseCode = "400", description = "Venta ya estaba cancelada")
    })
    public ResponseEntity<Void> cancelSale(
            @Parameter(description = "ID de la venta a cancelar")
            @PathVariable Long id) {
        saleService.cancelSale(id);
        return ResponseEntity.noContent().build();
    }
}
