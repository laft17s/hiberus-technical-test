package com.hiberus.payment.domain.repository;

import com.hiberus.payment.domain.model.PaymentOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentOrderRepository {
    Mono<PaymentOrder> save(PaymentOrder paymentOrder);
    Mono<PaymentOrder> findById(String paymentOrderId);
    Mono<Boolean> existsByExternalReference(String externalReference);
    Flux<PaymentOrder> findAll();
}
