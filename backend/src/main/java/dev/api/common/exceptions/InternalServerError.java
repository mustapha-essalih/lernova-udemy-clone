package dev.api.common.exceptions;

public class InternalServerError  extends RuntimeException {
    public InternalServerError(String message) {
        super(message);
    }
} 