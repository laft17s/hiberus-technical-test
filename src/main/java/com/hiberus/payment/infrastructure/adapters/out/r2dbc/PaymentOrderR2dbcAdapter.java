package com.hiberus.payment.infrastructure.adapters.out.r2dbc;

import com.hiberus.payment.domain.model.PaymentOrder;
import com.hiberus.payment.domain.repository.PaymentOrderRepository;
import com.hiberus.payment.infrastructure.adapters.out.r2dbc.mapper.PaymentOrderEntityMapper;
import com.hiberus.payment.infrastructure.adapters.out.r2dbc.repository.PaymentOrderSpringDataRepository;
import org.springframework.stereotype.Repository;
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
        return repository.findById(paymentOrderId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByExternalReference(String externalReference) {
        return repository.existsByExternalReference(externalReference);
    }
}
