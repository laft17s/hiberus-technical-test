package com.hiberus.payment.infrastructure.entrypoints.rest.controller;

import com.hiberus.payment.application.usecase.PaymentOrderUseCase;
import com.hiberus.payment.infrastructure.entrypoints.rest.api.PaymentOrderApi;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.ApiResponsePaymentOrder;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.ApiResponsePaymentOrderList;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.ApiResponsePaymentOrderStatus;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.InitiatePaymentOrderRequest;
import com.hiberus.payment.infrastructure.entrypoints.rest.mapper.PaymentOrderRestMapper;
import com.hiberus.payment.infrastructure.entrypoints.rest.constants.ApiConstants;
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
    public Mono<ResponseEntity<ApiResponsePaymentOrder>> initiatePaymentOrder(Mono<InitiatePaymentOrderRequest> initiatePaymentOrderRequest, ServerWebExchange exchange) {
        return initiatePaymentOrderRequest
                .map(mapper::toDomain)
                .flatMap(paymentOrderUseCase::initiatePaymentOrder)
                .map(mapper::toResponse)
                .map(resp -> {
                    ApiResponsePaymentOrder wrapper = new ApiResponsePaymentOrder()
                            .code(ApiConstants.CODE_SUCCESS)
                            .status(ApiConstants.STATUS_SUCCESS)
                            .message(ApiConstants.MSG_PAYMENT_ORDER_CREATED)
                            .data(resp);
                    return ResponseEntity.status(HttpStatus.CREATED).body(wrapper);
                });
    }

    @Override
    public Mono<ResponseEntity<ApiResponsePaymentOrder>> retrievePaymentOrder(String id, ServerWebExchange exchange) {
        return paymentOrderUseCase.retrievePaymentOrder(id)
                .map(mapper::toResponse)
                .map(resp -> {
                    ApiResponsePaymentOrder wrapper = new ApiResponsePaymentOrder()
                            .code(ApiConstants.CODE_SUCCESS)
                            .status(ApiConstants.STATUS_SUCCESS)
                            .message(ApiConstants.MSG_PAYMENT_ORDER_RETRIEVED)
                            .data(resp);
                    return ResponseEntity.ok(wrapper);
                });
    }

    @Override
    public Mono<ResponseEntity<ApiResponsePaymentOrderStatus>> retrievePaymentOrderStatus(String id, ServerWebExchange exchange) {
        return paymentOrderUseCase.retrievePaymentOrderStatus(id)
                .map(mapper::toStatusResponse)
                .map(resp -> {
                    ApiResponsePaymentOrderStatus wrapper = new ApiResponsePaymentOrderStatus()
                            .code(ApiConstants.CODE_SUCCESS)
                            .status(ApiConstants.STATUS_SUCCESS)
                            .message(ApiConstants.MSG_PAYMENT_ORDER_STATUS_RETRIEVED)
                            .data(resp);
                    return ResponseEntity.ok(wrapper);
                });
    }

    @Override
    public Mono<ResponseEntity<ApiResponsePaymentOrderList>> retrieveAllPaymentOrders(ServerWebExchange exchange) {
        return paymentOrderUseCase.retrieveAllPaymentOrders()
                .map(mapper::toResponse)
                .collectList()
                .map(list -> {
                    ApiResponsePaymentOrderList wrapper = new ApiResponsePaymentOrderList()
                            .code(ApiConstants.CODE_SUCCESS)
                            .status(ApiConstants.STATUS_SUCCESS)
                            .message(ApiConstants.MSG_PAYMENT_ORDER_LIST_RETRIEVED)
                            .data(list);
                    return ResponseEntity.ok(wrapper);
                });
    }
}
