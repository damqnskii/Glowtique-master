package com.glowtique.glowtique.voucher.model;

public enum VoucherType {
    TEN_PERCENT("Отсъпка на стойност 10%"),
    FIFTEEN_PERCENT("Отсъпка на стойност 15%"),
    TWENTY_BGN("Отсъпка на стойност 20 €"),
    FIFTY_BGN("Отсъпка на стойност 50 €");

    private String value;
    private VoucherType(String value) {
        this.value = value;
    }

}
