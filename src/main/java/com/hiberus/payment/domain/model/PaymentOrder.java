package com.hiberus.payment.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentOrder {
    private String paymentOrderId;
    private String externalReference;
    private AccountIdentifier debtorAccount;
    private AccountIdentifier creditorAccount;
    private Money instructedAmount;
    private String remittanceInformation;
    private LocalDate requestedExecutionDate;
    private PaymentOrderStatus status;
    private LocalDateTime lastUpdate;

    public PaymentOrder(String paymentOrderId, String externalReference, AccountIdentifier debtorAccount,
                        AccountIdentifier creditorAccount, Money instructedAmount, String remittanceInformation,
                        LocalDate requestedExecutionDate, PaymentOrderStatus status, LocalDateTime lastUpdate) {
        this.paymentOrderId = paymentOrderId;
        this.externalReference = externalReference;
        this.debtorAccount = debtorAccount;
        this.creditorAccount = creditorAccount;
        this.instructedAmount = instructedAmount;
        this.remittanceInformation = remittanceInformation;
        this.requestedExecutionDate = requestedExecutionDate;
        this.status = status;
        this.lastUpdate = lastUpdate;
    }

    public PaymentOrder() {
    }

    public String getPaymentOrderId() {
        return paymentOrderId;
    }

    public void setPaymentOrderId(String paymentOrderId) {
        this.paymentOrderId = paymentOrderId;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public AccountIdentifier getDebtorAccount() {
        return debtorAccount;
    }

    public void setDebtorAccount(AccountIdentifier debtorAccount) {
        this.debtorAccount = debtorAccount;
    }

    public AccountIdentifier getCreditorAccount() {
        return creditorAccount;
    }

    public void setCreditorAccount(AccountIdentifier creditorAccount) {
        this.creditorAccount = creditorAccount;
    }

    public Money getInstructedAmount() {
        return instructedAmount;
    }

    public void setInstructedAmount(Money instructedAmount) {
        this.instructedAmount = instructedAmount;
    }

    public String getRemittanceInformation() {
        return remittanceInformation;
    }

    public void setRemittanceInformation(String remittanceInformation) {
        this.remittanceInformation = remittanceInformation;
    }

    public LocalDate getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(LocalDate requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }

    public PaymentOrderStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentOrderStatus status) {
        this.status = status;
        this.lastUpdate = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
