package com.democlass.pos.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ValidationErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private List<ValidationError> validationErrors;
    private String path;

    // Constructors
    public ValidationErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ValidationErrorResponseDTO(int status, String error, List<ValidationError> validationErrors, String path) {
        this();
        this.status = status;
        this.error = error;
        this.validationErrors = validationErrors;
        this.path = path;
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static class ValidationError {
        private String field;
        private String message;
        private String rejectedValue;

        public ValidationError() {}

        public ValidationError(String field, String message, String rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(String rejectedValue) {
            this.rejectedValue = rejectedValue;
        }
    }
}
