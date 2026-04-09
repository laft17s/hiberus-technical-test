package com.hiberus.payment.domain.model;

public record AccountIdentifier(String iban) {
    public AccountIdentifier {
        if (iban == null || iban.isBlank()) {
            throw new IllegalArgumentException("IBAN must be defined");
        }
    }
}
