package com.zpi.transportservice.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    USD("USD", "$"),
    PLN("PLN", "zł"),
    EURO("EUR", "€");

    private final String name;
    private final String symbol;
}
