package com.democlass.pos.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super(String.format("Email %s is already registered", email));
    }
}
