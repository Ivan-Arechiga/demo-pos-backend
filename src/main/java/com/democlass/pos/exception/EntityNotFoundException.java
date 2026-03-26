package com.democlass.pos.exception;

public class EntityNotFoundException extends RuntimeException {
    private final String entityName;
    private final Long id;

    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s with id %d not found", entityName, id));
        this.entityName = entityName;
        this.id = id;
    }

    public EntityNotFoundException(String message) {
        super(message);
        this.entityName = null;
        this.id = null;
    }

    public String getEntityName() {
        return entityName;
    }

    public Long getId() {
        return id;
    }
}
