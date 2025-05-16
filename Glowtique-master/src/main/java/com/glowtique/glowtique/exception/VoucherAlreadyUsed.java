package com.glowtique.glowtique.exception;

public class VoucherAlreadyUsed extends RuntimeException {
    public VoucherAlreadyUsed(String message) {
        super(message);
    }
}
