package com.hiberus.payment.infrastructure.adapters.out.r2dbc;

import java.util.Objects;

import com.hiberus.payment.domain.model.PaymentOrder;
import com.hiberus.payment.domain.repository.PaymentOrderRepository;
import com.hiberus.payment.infrastructure.adapters.out.r2dbc.mapper.PaymentOrderEntityMapper;
import com.hiberus.payment.infrastructure.adapters.out.r2dbc.repository.PaymentOrderSpringDataRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository

public class PaymentOrderR2dbcAdapter implements PaymentOrderRepository {

    private final PaymentOrderSpringDataRepository repository;
    private final PaymentOrderEntityMapper mapper;

    public PaymentOrderR2dbcAdapter(PaymentOrderSpringDataRepository repository, PaymentOrderEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<PaymentOrder> save(PaymentOrder paymentOrder) {
        return repository.save(mapper.toEntity(paymentOrder))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<PaymentOrder> findById(String paymentOrderId) {
        Objects.requireNonNull(paymentOrderId, "paymentOrderId cannot be null");
        return repository.findById(paymentOrderId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByExternalReference(String externalReference) {
        Objects.requireNonNull(externalReference, "externalReference cannot be null");
        return repository.existsByExternalReference(externalReference);
    }

    @Override
    public Flux<PaymentOrder> findAll() {
        return repository.findAll()
                .map(mapper::toDomain);
    }
}
