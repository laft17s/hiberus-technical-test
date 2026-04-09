package com.hiberus.payment.application.usecase;

import com.hiberus.payment.domain.exception.IdempotencyException;
import com.hiberus.payment.domain.exception.PaymentOrderNotFoundException;
import com.hiberus.payment.domain.model.PaymentOrder;
import com.hiberus.payment.domain.repository.PaymentOrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentOrderUseCase {

    private final PaymentOrderRepository paymentOrderRepository;

    public PaymentOrderUseCase(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }

    public Mono<PaymentOrder> initiatePaymentOrder(PaymentOrder paymentOrder) {
        return paymentOrderRepository.existsByExternalReference(paymentOrder.getExternalReference())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new IdempotencyException("Payment Order with external reference " 
                            + paymentOrder.getExternalReference() + " already exists"));
                    }
                    paymentOrder.setPaymentOrderId("PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    paymentOrder.setStatus(com.hiberus.payment.domain.model.PaymentOrderStatus.PENDING);
                    paymentOrder.setLastUpdate(LocalDateTime.now());
                    return paymentOrderRepository.save(paymentOrder);
                });
    }

    public Mono<PaymentOrder> retrievePaymentOrder(String paymentOrderId) {
        return paymentOrderRepository.findById(paymentOrderId)
                .switchIfEmpty(Mono.error(new PaymentOrderNotFoundException("Payment Order " + paymentOrderId + " not found")));
    }

    public Mono<PaymentOrder> retrievePaymentOrderStatus(String paymentOrderId) {
        return retrievePaymentOrder(paymentOrderId); // Returns the whole order, adapter can map just the status
    }

    public Flux<PaymentOrder> retrieveAllPaymentOrders() {
        return paymentOrderRepository.findAll();
    }
}
