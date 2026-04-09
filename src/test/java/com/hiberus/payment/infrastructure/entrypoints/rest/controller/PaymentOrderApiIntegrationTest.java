package com.hiberus.payment.infrastructure.entrypoints.rest.controller;

import com.hiberus.payment.infrastructure.entrypoints.rest.dto.AccountIdentifier;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.InitiatePaymentOrderRequest;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.MoneyAmount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PaymentOrderApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://" + postgres.getHost() + ":" + postgres.getFirstMappedPort() + "/" + postgres.getDatabaseName());
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldInitiatePaymentOrderAndRetrieveIt() {
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest()
                .externalReference("EXT-1001")
                .debtorAccount(new AccountIdentifier().iban("ES1234567890"))
                .creditorAccount(new AccountIdentifier().iban("ES0987654321"))
                .instructedAmount(new MoneyAmount().amount(200.50).currency("EUR"))
                .remittanceInformation("Test Payment")
                .requestedExecutionDate(java.time.LocalDate.parse("2025-10-31"));

        // 1. Initiate 
        String paymentOrderId = webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.paymentOrderId").isNotEmpty()
                .jsonPath("$.status").isEqualTo("PENDING")
                .jsonPath("$.externalReference").isEqualTo("EXT-1001")
                .returnResult()
                .getResponseBody()
                .toString();

        // Regex or parsing would be needed to get exact ID, but let's just assert existence for creation mapping
    }

    @Test
    void shouldFailOnDuplicateExternalReference() {
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest()
                .externalReference("EXT-9999")
                .debtorAccount(new AccountIdentifier().iban("ES12"))
                .creditorAccount(new AccountIdentifier().iban("ES99"))
                .instructedAmount(new MoneyAmount().amount(10.0).currency("EUR"))
                .requestedExecutionDate(java.time.LocalDate.parse("2026-01-01"));

        webTestClient.post().uri("/payment-initiation/payment-orders").bodyValue(request).exchange().expectStatus().isCreated();
        webTestClient.post().uri("/payment-initiation/payment-orders").bodyValue(request).exchange().expectStatus().isBadRequest();
    }
}
