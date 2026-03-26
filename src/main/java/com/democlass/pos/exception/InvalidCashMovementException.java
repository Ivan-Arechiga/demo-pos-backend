package com.democlass.pos.exception;

public class InvalidCashMovementException extends RuntimeException {
    public InvalidCashMovementException(String message) {
        super(message);
    }
}
