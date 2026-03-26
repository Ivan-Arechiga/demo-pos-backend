package com.democlass.pos.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId, Integer requestedQuantity, Integer availableStock) {
        super(String.format("Product %d: requested %d units, but only %d available", productId, requestedQuantity, availableStock));
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}
