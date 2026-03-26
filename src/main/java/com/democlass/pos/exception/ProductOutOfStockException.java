package com.democlass.pos.exception;

public class ProductOutOfStockException extends RuntimeException {
    public ProductOutOfStockException(Long productId) {
        super(String.format("Product %d is out of stock", productId));
    }
}
