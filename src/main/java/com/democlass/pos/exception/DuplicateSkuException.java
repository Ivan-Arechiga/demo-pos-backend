package com.democlass.pos.exception;

public class DuplicateSkuException extends RuntimeException {
    public DuplicateSkuException(String sku) {
        super(String.format("SKU %s is already in use", sku));
    }
}
