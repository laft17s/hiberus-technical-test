package com.hiberus.payment.application.usecase;

import com.hiberus.payment.domain.exception.IdempotencyException;
import com.hiberus.payment.domain.exception.PaymentOrderNotFoundException;
import com.hiberus.payment.domain.model.Money;
import com.hiberus.payment.domain.model.PaymentOrder;
import com.hiberus.payment.domain.model.PaymentOrderStatus;
import com.hiberus.payment.domain.repository.PaymentOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentOrderUseCaseTest {

    @Mock
    private PaymentOrderRepository repository;

    @InjectMocks
    private PaymentOrderUseCase useCase;

    @Test
    void initiatePaymentOrder_success() {
        PaymentOrder order = new PaymentOrder();
        order.setExternalReference("EXT-123");
        order.setInstructedAmount(new Money(100.0, "USD"));

        when(repository.existsByExternalReference("EXT-123")).thenReturn(Mono.just(false));
        when(repository.save(any(PaymentOrder.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.initiatePaymentOrder(order))
                .assertNext(saved -> {
                    assertThat(saved.getPaymentOrderId()).isNotBlank();
                    assertThat(saved.getStatus()).isEqualTo(PaymentOrderStatus.PENDING);
                })
                .verifyComplete();

        verify(repository).save(any(PaymentOrder.class));
    }

    @Test
    void initiatePaymentOrder_idempotencyException() {
        PaymentOrder order = new PaymentOrder();
        order.setExternalReference("EXT-123");

        when(repository.existsByExternalReference("EXT-123")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.initiatePaymentOrder(order))
                .expectError(IdempotencyException.class)
                .verify();

        verify(repository, never()).save(any());
    }

    @Test
    void retrievePaymentOrder_success() {
        PaymentOrder order = new PaymentOrder();
        order.setPaymentOrderId("PO-123");

        when(repository.findById("PO-123")).thenReturn(Mono.just(order));

        StepVerifier.create(useCase.retrievePaymentOrder("PO-123"))
                .assertNext(found -> assertThat(found.getPaymentOrderId()).isEqualTo("PO-123"))
                .verifyComplete();
    }

    @Test
    void retrievePaymentOrder_notFound() {
        when(repository.findById("PO-123")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.retrievePaymentOrder("PO-123"))
                .expectError(PaymentOrderNotFoundException.class)
                .verify();
    }
}
