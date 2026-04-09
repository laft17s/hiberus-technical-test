package com.hiberus.payment.infrastructure.adapters.out.r2dbc.mapper;

import com.hiberus.payment.domain.model.AccountIdentifier;
import com.hiberus.payment.domain.model.Money;
import com.hiberus.payment.domain.model.PaymentOrder;
import com.hiberus.payment.domain.model.PaymentOrderStatus;
import com.hiberus.payment.infrastructure.adapters.out.r2dbc.entity.PaymentOrderEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentOrderEntityMapperTest {

    private final PaymentOrderEntityMapper mapper = new PaymentOrderEntityMapper();

    @Test
    void shouldMapDomainToEntity() {
        PaymentOrder domain = new PaymentOrder(
                "PO-1", "EXT-1", new AccountIdentifier("IBAN1"), new AccountIdentifier("IBAN2"),
                new Money(150.0, "USD"), "Remittance", LocalDate.now(), PaymentOrderStatus.PENDING, LocalDateTime.now()
        );

        PaymentOrderEntity entity = mapper.toEntity(domain);
        assertThat(entity.getPaymentOrderId()).isEqualTo("PO-1");
        assertThat(entity.getExternalReference()).isEqualTo("EXT-1");
        assertThat(entity.getDebtorIban()).isEqualTo("IBAN1");
        assertThat(entity.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(150.0));
    }

    @Test
    void shouldMapEntityToDomain() {
        PaymentOrderEntity entity = new PaymentOrderEntity();
        entity.setPaymentOrderId("PO-1");
        entity.setAmount(BigDecimal.valueOf(200.5));
        entity.setCurrency("EUR");
        entity.setStatus("ACCEPTED");

        PaymentOrder domain = mapper.toDomain(entity);
        assertThat(domain.getPaymentOrderId()).isEqualTo("PO-1");
        assertThat(domain.getInstructedAmount().amount()).isEqualTo(200.5);
        assertThat(domain.getStatus()).isEqualTo(PaymentOrderStatus.ACCEPTED);
    }
}
