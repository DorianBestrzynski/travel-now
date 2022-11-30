package com.zpi.tripgroupservice.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    USD("USD", "$"),
    PLN("PLN", "zł"),
    EURO("EUR", "€"),
    CZK("CZK", "Kč"),
    TRY("TRY", "₺"),
    GBP("GBP", "£"),
    HRK("HRK", "kn"),
    ARS("ARS", "$"),
    UAH("UAH", "₴"),
    JPY("JPY", "¥"),
    RON("RON", "L");


    private final String name;
    private final String symbol;
}
