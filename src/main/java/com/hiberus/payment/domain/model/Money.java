package com.hiberus.payment.domain.model;

public record Money(Double amount, String currency) {
    public Money {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must be defined");
        }
    }
}
