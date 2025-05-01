package com.glowtique.glowtique.exception;

public class UserNotExisting extends RuntimeException {
    public UserNotExisting(String message) {
        super(message);
    }
}
