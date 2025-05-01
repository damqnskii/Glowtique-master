package com.glowtique.glowtique.product.model;

import lombok.Getter;

@Getter
public enum FragranceType {
    ALDEHYDIC("Алдехидни"),
    AROMATIC("Ароматни"),
    AQUATIC("Водни"),
    GOURMAND("Гурме"),
    OTHER("Други"),
    WOODY("Дървесни"),
    GREEN("Зелени"),
    LEATHER("Кожени"),
    MUSKY("Мускусен"),
    ORIENTAL("Ориенталски"),
    SPICY("Пикантни"),
    FRUITY("Плодови"),
    FOUGERE("Фужерен"),
    FLORAL("Цветни"),
    CITRUS("Цитрусови"),
    CHYPRE("Шипрови");

    private String value;

    FragranceType(String value) {
        this.value = value;
    }
}
