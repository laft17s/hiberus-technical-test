package com.hiberus.payment.infrastructure.entrypoints.rest.mapper;

import com.hiberus.payment.domain.model.PaymentOrder;
import com.hiberus.payment.domain.model.PaymentOrderStatus;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.InitiatePaymentOrderRequest;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.MoneyAmount;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.PaymentOrderResponse;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.PaymentOrderStatusResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentOrderRestMapperTest {

    private final PaymentOrderRestMapper mapper = new PaymentOrderRestMapper();

    @Test
    void shouldMapRequestToDomain() {
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();
        request.setExternalReference("EXT-2");
        request.setInstructedAmount(new MoneyAmount().amount(100.0).currency("USD"));
        
        com.hiberus.payment.domain.model.PaymentOrder domain = mapper.toDomain(request);
        assertThat(domain.getExternalReference()).isEqualTo("EXT-2");
        assertThat(domain.getInstructedAmount().amount()).isEqualTo(100.0);
    }

    @Test
    void shouldMapDomainToResponse() {
        PaymentOrder domain = new PaymentOrder();
        domain.setPaymentOrderId("PO-3");
        domain.setStatus(PaymentOrderStatus.PENDING);

        PaymentOrderResponse response = mapper.toResponse(domain);
        assertThat(response.getPaymentOrderId()).isEqualTo("PO-3");
        assertThat(response.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void shouldMapDomainToStatusResponse() {
        PaymentOrder domain = new PaymentOrder();
        domain.setPaymentOrderId("PO-3");
        domain.setStatus(PaymentOrderStatus.ACCEPTED);
        domain.setLastUpdate(LocalDateTime.of(2025, 1, 1, 10, 0));

        PaymentOrderStatusResponse response = mapper.toStatusResponse(domain);
        assertThat(response.getPaymentOrderId()).isEqualTo("PO-3");
        assertThat(response.getStatus()).isEqualTo("ACCEPTED");
        assertThat(response.getLastUpdate()).isEqualTo("2025-01-01T10:00:00Z");
    }
}
