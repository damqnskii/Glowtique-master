package com.glowtique.glowtique.product.model;

import lombok.Getter;

@Getter
public enum FragranceType {
    ALDEHYDIC("Алдехидни"),
    AROMATIC("Ароматни"),
    AQUATIC("Водни"),
    CITRUS("Цитрусови"),
    CHYPRE("Шипрови"),
    GREEN("Зелени"),
    FLORAL("Цветни"),
    FOUGERE("Фужерен"),
    FRUITY("Плодови"),
    GOURMAND("Гурме"),
    LEATHER("Кожени"),
    MUSKY("Мускусен"),
    ORIENTAL("Ориенталски"),
    OTHER("Други"),
    SPICY("Пикантни"),
    WOODY("Дървесни");

    private String value;

    FragranceType(String value) {
        this.value = value;
    }
}
