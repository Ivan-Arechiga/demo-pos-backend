package com.democlass.pos.service;

import com.democlass.pos.dto.CashMovementDTO;
import com.democlass.pos.dto.CashSummaryDTO;
import com.democlass.pos.dto.CreateCashMovementRequest;
import com.democlass.pos.entity.CashMovement;
import com.democlass.pos.exception.InvalidCashMovementException;
import com.democlass.pos.repository.CashMovementRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CashMovementService {

    private final CashMovementRepository cashMovementRepository;

    public CashMovementService(CashMovementRepository cashMovementRepository) {
        this.cashMovementRepository = cashMovementRepository;
    }

    public List<CashMovementDTO> getAllMovements() {
        return cashMovementRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public CashSummaryDTO getCashSummary() {
        List<CashMovement> movements = cashMovementRepository.findAll();

        BigDecimal totalSales = movements.stream()
            .filter(m -> m.getType() == CashMovement.MovementType.SALE)
            .map(CashMovement::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRefunds = movements.stream()
            .filter(m -> m.getType() == CashMovement.MovementType.REFUND)
            .map(CashMovement::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Manual adjustments
        BigDecimal manualAdjustments = movements.stream()
            .filter(m -> m.getType() == CashMovement.MovementType.MANUAL_ADJUSTMENT)
            .map(CashMovement::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalSales.add(totalRefunds).add(manualAdjustments);

        return new CashSummaryDTO(totalSales, totalRefunds.abs(), balance);
    }

    public CashMovementDTO createMovement(CreateCashMovementRequest request) {
        // Validate amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidCashMovementException("Amount cannot be zero");
        }

        // Validate type
        CashMovement.MovementType type;
        try {
            type = CashMovement.MovementType.valueOf(request.getType());
        } catch (IllegalArgumentException e) {
            throw new InvalidCashMovementException(String.format("Invalid movement type: %s", request.getType()));
        }

        // Only MANUAL_ADJUSTMENT can be created manually
        if (type != CashMovement.MovementType.MANUAL_ADJUSTMENT) {
            throw new InvalidCashMovementException("Only MANUAL_ADJUSTMENT movements can be created manually");
        }

        CashMovement movement = new CashMovement(
            type,
            request.getAmount(),
            request.getDescription(),
            request.getReferenceId()
        );
        movement.setDate(LocalDateTime.now());
        movement.setCreatedAt(LocalDateTime.now());

        CashMovement savedMovement = cashMovementRepository.save(movement);
        return toDTO(savedMovement);
    }

    private CashMovementDTO toDTO(CashMovement movement) {
        return new CashMovementDTO(
            movement.getId(),
            movement.getType().toString(),
            movement.getAmount(),
            movement.getDescription(),
            movement.getDate(),
            movement.getReferenceId(),
            movement.getCreatedAt()
        );
    }
}
