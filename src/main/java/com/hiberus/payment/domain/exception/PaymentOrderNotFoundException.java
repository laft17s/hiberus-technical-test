package com.hiberus.payment.domain.exception;

public class PaymentOrderNotFoundException extends RuntimeException {
    public PaymentOrderNotFoundException(String message) {
        super(message);
    }
}
