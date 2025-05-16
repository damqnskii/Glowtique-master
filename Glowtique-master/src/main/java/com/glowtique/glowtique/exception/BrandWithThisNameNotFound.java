package com.glowtique.glowtique.exception;

public class BrandWithThisNameNotFound extends RuntimeException {
    public BrandWithThisNameNotFound(String message) {
        super(message);
    }
}
