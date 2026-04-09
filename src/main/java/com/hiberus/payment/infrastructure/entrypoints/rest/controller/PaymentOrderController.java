package com.hiberus.payment.infrastructure.entrypoints.rest.controller;

import com.hiberus.payment.application.usecase.PaymentOrderUseCase;
import com.hiberus.payment.infrastructure.entrypoints.rest.api.PaymentOrderApi;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.InitiatePaymentOrderRequest;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.PaymentOrderResponse;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.PaymentOrderStatusResponse;
import com.hiberus.payment.infrastructure.entrypoints.rest.mapper.PaymentOrderRestMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class PaymentOrderController implements PaymentOrderApi {

    private final PaymentOrderUseCase paymentOrderUseCase;
    private final PaymentOrderRestMapper mapper;

    public PaymentOrderController(PaymentOrderUseCase paymentOrderUseCase, PaymentOrderRestMapper mapper) {
        this.paymentOrderUseCase = paymentOrderUseCase;
        this.mapper = mapper;
    }

    @Override
    public Mono<ResponseEntity<PaymentOrderResponse>> initiatePaymentOrder(Mono<InitiatePaymentOrderRequest> initiatePaymentOrderRequest, ServerWebExchange exchange) {
        return initiatePaymentOrderRequest
                .map(mapper::toDomain)
                .flatMap(paymentOrderUseCase::initiatePaymentOrder)
                .map(mapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Override
    public Mono<ResponseEntity<PaymentOrderResponse>> retrievePaymentOrder(String id, ServerWebExchange exchange) {
        return paymentOrderUseCase.retrievePaymentOrder(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<PaymentOrderStatusResponse>> retrievePaymentOrderStatus(String id, ServerWebExchange exchange) {
        return paymentOrderUseCase.retrievePaymentOrderStatus(id)
                .map(mapper::toStatusResponse)
                .map(ResponseEntity::ok);
    }
}
