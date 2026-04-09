package com.hiberus.payment.infrastructure.entrypoints.rest.mapper;

import com.hiberus.payment.domain.model.AccountIdentifier;
import com.hiberus.payment.domain.model.Money;
import com.hiberus.payment.domain.model.PaymentOrder;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.InitiatePaymentOrderRequest;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.PaymentOrderResponse;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.PaymentOrderStatusResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentOrderRestMapper {

    public PaymentOrder toDomain(InitiatePaymentOrderRequest request) {
        PaymentOrder domain = new PaymentOrder();
        domain.setExternalReference(request.getExternalReference());
        
        if (request.getDebtorAccount() != null) {
            domain.setDebtorAccount(new AccountIdentifier(request.getDebtorAccount().getIban()));
        }
        if (request.getCreditorAccount() != null) {
            domain.setCreditorAccount(new AccountIdentifier(request.getCreditorAccount().getIban()));
        }
        if (request.getInstructedAmount() != null) {
            domain.setInstructedAmount(new Money(
                request.getInstructedAmount().getAmount(),
                request.getInstructedAmount().getCurrency()
            ));
        }
        domain.setRemittanceInformation(request.getRemittanceInformation());
        if (request.getRequestedExecutionDate() != null) {
            domain.setRequestedExecutionDate(request.getRequestedExecutionDate());
        }
        return domain;
    }

    public PaymentOrderResponse toResponse(PaymentOrder domain) {
        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setPaymentOrderId(domain.getPaymentOrderId());
        response.setExternalReference(domain.getExternalReference());
        if (domain.getStatus() != null) {
            response.setStatus(domain.getStatus().name());
        }
        return response;
    }

    public PaymentOrderStatusResponse toStatusResponse(PaymentOrder domain) {
        PaymentOrderStatusResponse response = new PaymentOrderStatusResponse();
        response.setPaymentOrderId(domain.getPaymentOrderId());
        if (domain.getStatus() != null) {
            response.setStatus(domain.getStatus().name());
        }
        if (domain.getLastUpdate() != null) {
            response.setLastUpdate(domain.getLastUpdate().atOffset(java.time.ZoneOffset.UTC));
        }
        return response;
    }
}
