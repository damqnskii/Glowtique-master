package com.glowtique.glowtique.order.model;

public enum OrderMethod {

    SPEEDY_ADDRESS("Speedy до адрес"),
    SPEEDY_OFFICE("Офис на Speedy"),
    SPEEDY_POST("Автомат на Speedy"),
    EASY_BOX("Easy Box"),
    ECONT_ADDRESS("Еконт до адрес"),
    ECONT_OFFICE("Офис на Еконт");

    private final String displayName;

    OrderMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}