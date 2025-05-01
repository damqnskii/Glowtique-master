package com.glowtique.glowtique.exception;

public class ExistingPhoneNumber extends RuntimeException {
    public ExistingPhoneNumber(String message) {
        super(message);
    }
}
