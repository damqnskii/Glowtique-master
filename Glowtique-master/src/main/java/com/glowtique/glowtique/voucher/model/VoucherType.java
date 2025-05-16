package com.glowtique.glowtique.voucher.model;

public enum VoucherType {
    TEN_PERCENT("Отсъпка на стойност 10%"),
    FIFTEEN_PERCENT("Отсъпка на стойност 15%"),
    FIFTY_BGN("Отсъпка на стойност 50 лв"),
    HUNDRED_BGN("Отсъпка на стойност 100 лв");

    private String value;
    private VoucherType(String value) {
        this.value = value;
    }

}
