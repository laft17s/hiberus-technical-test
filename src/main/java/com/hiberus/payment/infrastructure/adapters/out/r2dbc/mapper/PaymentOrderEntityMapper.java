package com.hiberus.payment.infrastructure.adapters.out.r2dbc.mapper;

import com.hiberus.payment.domain.model.AccountIdentifier;
import com.hiberus.payment.domain.model.Money;
import com.hiberus.payment.domain.model.PaymentOrder;
import com.hiberus.payment.domain.model.PaymentOrderStatus;
import com.hiberus.payment.infrastructure.adapters.out.r2dbc.entity.PaymentOrderEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentOrderEntityMapper {

    @NonNull
    public PaymentOrderEntity toEntity(PaymentOrder domain) {
        PaymentOrderEntity entity = new PaymentOrderEntity();
        entity.setPaymentOrderId(domain.getPaymentOrderId());
        entity.setExternalReference(domain.getExternalReference());
        
        if (domain.getDebtorAccount() != null) {
            entity.setDebtorIban(domain.getDebtorAccount().iban());
        }
        if (domain.getCreditorAccount() != null) {
            entity.setCreditorIban(domain.getCreditorAccount().iban());
        }
        if (domain.getInstructedAmount() != null) {
            entity.setAmount(BigDecimal.valueOf(domain.getInstructedAmount().amount()));
            entity.setCurrency(domain.getInstructedAmount().currency());
        }
        
        entity.setRemittanceInformation(domain.getRemittanceInformation());
        entity.setRequestedExecutionDate(domain.getRequestedExecutionDate());
        
        if (domain.getStatus() != null) {
            entity.setStatus(domain.getStatus().name());
        }
        entity.setLastUpdate(domain.getLastUpdate());
        
        return entity;
    }

    @NonNull
    public PaymentOrder toDomain(PaymentOrderEntity entity) {
        return new PaymentOrder(
                entity.getPaymentOrderId(),
                entity.getExternalReference(),
                entity.getDebtorIban() != null ? new AccountIdentifier(entity.getDebtorIban()) : null,
                entity.getCreditorIban() != null ? new AccountIdentifier(entity.getCreditorIban()) : null,
                entity.getAmount() != null && entity.getCurrency() != null 
                        ? new Money(entity.getAmount().doubleValue(), entity.getCurrency()) : null,
                entity.getRemittanceInformation(),
                entity.getRequestedExecutionDate(),
                entity.getStatus() != null ? PaymentOrderStatus.valueOf(entity.getStatus()) : null,
                entity.getLastUpdate()
        );
    }
}
