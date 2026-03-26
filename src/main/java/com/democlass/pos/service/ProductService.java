package com.democlass.pos.service;

import com.democlass.pos.dto.CreateProductRequest;
import com.democlass.pos.dto.ProductDTO;
import com.democlass.pos.dto.UpdateProductRequest;
import com.democlass.pos.entity.Product;
import com.democlass.pos.exception.DuplicateSkuException;
import com.democlass.pos.exception.EntityNotFoundException;
import com.democlass.pos.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product", id));
        return toDTO(product);
    }

    public ProductDTO createProduct(CreateProductRequest request) {
        // Check if SKU already exists
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new DuplicateSkuException(request.getSku());
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(Product.ProductStatus.ACTIVE);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);
        return toDTO(savedProduct);
    }

    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product", id));

        // Check SKU uniqueness if SKU is being updated
        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            if (productRepository.findBySku(request.getSku()).isPresent()) {
                throw new DuplicateSkuException(request.getSku());
            }
            product.setSku(request.getSku());
        }

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }

        if (request.getStatus() != null) {
            product.setStatus(Product.ProductStatus.valueOf(request.getStatus()));
        }

        product.setUpdatedAt(LocalDateTime.now());
        Product updatedProduct = productRepository.save(product);
        return toDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product", id));
        
        // Soft delete: set status to DISCONTINUED
        product.setStatus(Product.ProductStatus.DISCONTINUED);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    public Product getProductByIdEntity(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product", id));
    }

    public void updateProductStockOnly(Product product) {
        productRepository.save(product);
    }

    private ProductDTO toDTO(Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getSku(),
            product.getPrice(),
            product.getStock(),
            product.getStatus().toString(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}
