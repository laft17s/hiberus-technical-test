package com.hiberus.payment.infrastructure.adapters.out.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("payment_orders")
public class PaymentOrderEntity {
    @Id
    @Column("payment_order_id")
    private String paymentOrderId;

    @Column("external_reference")
    private String externalReference;

    @Column("debtor_iban")
    private String debtorIban;

    @Column("creditor_iban")
    private String creditorIban;

    private BigDecimal amount;
    private String currency;

    @Column("remittance_information")
    private String remittanceInformation;

    @Column("requested_execution_date")
    private LocalDate requestedExecutionDate;

    private String status;

    @Column("last_update")
    private LocalDateTime lastUpdate;

    // Getters and Setters

    public String getPaymentOrderId() { return paymentOrderId; }
    public void setPaymentOrderId(String paymentOrderId) { this.paymentOrderId = paymentOrderId; }

    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }

    public String getDebtorIban() { return debtorIban; }
    public void setDebtorIban(String debtorIban) { this.debtorIban = debtorIban; }

    public String getCreditorIban() { return creditorIban; }
    public void setCreditorIban(String creditorIban) { this.creditorIban = creditorIban; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getRemittanceInformation() { return remittanceInformation; }
    public void setRemittanceInformation(String remittanceInformation) { this.remittanceInformation = remittanceInformation; }

    public LocalDate getRequestedExecutionDate() { return requestedExecutionDate; }
    public void setRequestedExecutionDate(LocalDate requestedExecutionDate) { this.requestedExecutionDate = requestedExecutionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
}
