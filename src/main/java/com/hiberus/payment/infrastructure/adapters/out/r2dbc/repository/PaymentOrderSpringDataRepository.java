package com.hiberus.payment.infrastructure.adapters.out.r2dbc.repository;

import com.hiberus.payment.infrastructure.adapters.out.r2dbc.entity.PaymentOrderEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PaymentOrderSpringDataRepository extends R2dbcRepository<PaymentOrderEntity, String> {
    Mono<Boolean> existsByExternalReference(String externalReference);
}
