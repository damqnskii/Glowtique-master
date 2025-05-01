package com.glowtique.glowtique.exception;

public class CartNotExisting extends RuntimeException {
    public CartNotExisting(String message) {
        super(message);
    }
}
