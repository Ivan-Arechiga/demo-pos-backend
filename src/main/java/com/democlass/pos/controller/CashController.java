package com.democlass.pos.controller;

import com.democlass.pos.dto.CashMovementDTO;
import com.democlass.pos.dto.CashSummaryDTO;
import com.democlass.pos.dto.CreateCashMovementRequest;
import com.democlass.pos.service.CashMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cash")
@Tag(name = "Cash", description = "API para gestionar movimientos de caja en el sistema POS")
public class CashController {

    private final CashMovementService cashMovementService;

    public CashController(CashMovementService cashMovementService) {
        this.cashMovementService = cashMovementService;
    }

    @GetMapping("/movements")
    @Operation(summary = "Obtener movimientos de caja", description = "Retorna todos los movimientos de caja (ventas, reembolsos, ajustes manuales)")
    @ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida exitosamente")
    public ResponseEntity<List<CashMovementDTO>> getMovements() {
        return ResponseEntity.ok(cashMovementService.getAllMovements());
    }

    @GetMapping("/summary")
    @Operation(summary = "Obtener resumen de caja", description = "Retorna totales: ventas, reembolsos, y balance final")
    @ApiResponse(responseCode = "200", description = "Resumen de caja obtenido exitosamente")
    public ResponseEntity<CashSummaryDTO> getCashSummary() {
        return ResponseEntity.ok(cashMovementService.getCashSummary());
    }

    @PostMapping("/movements")
    @Operation(summary = "Registrar movimiento manual de caja", description = "Crea un ajuste manual de caja. Solo se pueden crear movimientos de tipo MANUAL_ADJUSTMENT")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Movimiento registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (amount cero, tipo inválido, etc.)")
    })
    public ResponseEntity<CashMovementDTO> createMovement(
            @Valid @RequestBody CreateCashMovementRequest request) {
        CashMovementDTO created = cashMovementService.createMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
