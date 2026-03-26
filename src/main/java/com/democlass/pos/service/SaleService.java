package com.democlass.pos.service;

import com.democlass.pos.dto.CreateSaleRequest;
import com.democlass.pos.dto.SaleDTO;
import com.democlass.pos.dto.SaleItemDTO;
import com.democlass.pos.entity.CashMovement;
import com.democlass.pos.entity.Product;
import com.democlass.pos.entity.Sale;
import com.democlass.pos.entity.SaleItem;
import com.democlass.pos.exception.EntityNotFoundException;
import com.democlass.pos.exception.InsufficientStockException;
import com.democlass.pos.exception.ProductOutOfStockException;
import com.democlass.pos.repository.CashMovementRepository;
import com.democlass.pos.repository.CustomerRepository;
import com.democlass.pos.repository.SaleRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductService productService;
    private final CashMovementRepository cashMovementRepository;

    public SaleService(SaleRepository saleRepository, CustomerRepository customerRepository, 
                       ProductService productService, CashMovementRepository cashMovementRepository) {
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.productService = productService;
        this.cashMovementRepository = cashMovementRepository;
    }

    public List<SaleDTO> getAllSales() {
        return saleRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public List<SaleDTO> getSalesByCustomerId(Long customerId) {
        // Verify customer exists
        customerRepository.findById(customerId)
            .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));
        
        return saleRepository.findByCustomerId(customerId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public SaleDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Sale", id));
        return toDTO(sale);
    }

    public SaleDTO createSale(CreateSaleRequest request) {
        // Verify customer exists
        customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new EntityNotFoundException("Customer", request.getCustomerId()));

        // Verify all products exist and have sufficient stock
        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateSaleRequest.SaleItemRequest itemRequest : request.getItems()) {
            Product product = productService.getProductByIdEntity(itemRequest.getProductId());

            // Check if product is OUT_OF_STOCK
            if (product.getStatus() == Product.ProductStatus.OUT_OF_STOCK) {
                throw new ProductOutOfStockException(product.getId());
            }

            // Check sufficient stock
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(product.getId(), itemRequest.getQuantity(), product.getStock());
            }

            // Create sale item
            SaleItem item = new SaleItem(
                product.getId(),
                itemRequest.getQuantity(),
                product.getPrice()
            );
            saleItems.add(item);
            totalAmount = totalAmount.add(item.getLineTotal());

            // Reduce stock
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productService.updateProductStockOnly(product);
        }

        // Create sale
        Sale sale = new Sale();
        sale.setCustomerId(request.getCustomerId());
        sale.setDate(LocalDateTime.now());
        sale.setStatus(Sale.SaleStatus.COMPLETED);
        sale.setTotalAmount(totalAmount);
        sale.setCreatedAt(LocalDateTime.now());

        Sale savedSale = saleRepository.save(sale);

        // Add items to sale
        for (SaleItem item : saleItems) {
            item.setSale(savedSale);
            savedSale.getItems().add(item);
        }

        savedSale = saleRepository.save(savedSale);

        // Create cash movement
        CashMovement movement = new CashMovement(
            CashMovement.MovementType.SALE,
            totalAmount,
            "Sale #" + savedSale.getId(),
            savedSale.getId()
        );
        cashMovementRepository.save(movement);

        return toDTO(savedSale);
    }

    public void cancelSale(Long id) {
        Sale sale = saleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Sale", id));

        if (sale.getStatus() == Sale.SaleStatus.CANCELLED) {
            throw new IllegalArgumentException("Sale is already cancelled");
        }

        sale.setStatus(Sale.SaleStatus.CANCELLED);
        sale.setUpdatedAt(LocalDateTime.now());
        saleRepository.save(sale);

        // Restore stock
        for (SaleItem item : sale.getItems()) {
            Product product = productService.getProductByIdEntity(item.getProductId());
            product.setStock(product.getStock() + item.getQuantity());
            productService.updateProductStockOnly(product);
        }

        // Create refund cash movement
        CashMovement movement = new CashMovement(
            CashMovement.MovementType.REFUND,
            sale.getTotalAmount().negate(),
            "Refund for Sale #" + sale.getId(),
            sale.getId()
        );
        cashMovementRepository.save(movement);
    }

    private SaleDTO toDTO(Sale sale) {
        List<SaleItemDTO> itemDTOs = sale.getItems().stream()
            .map(item -> new SaleItemDTO(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal()
            ))
            .collect(Collectors.toList());

        return new SaleDTO(
            sale.getId(),
            sale.getCustomerId(),
            sale.getDate(),
            sale.getTotalAmount(),
            sale.getStatus().toString(),
            itemDTOs,
            sale.getCreatedAt()
        );
    }
}
