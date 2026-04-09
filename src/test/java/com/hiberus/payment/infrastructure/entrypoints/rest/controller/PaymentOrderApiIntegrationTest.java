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
@SuppressWarnings("null")
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
        byte[] responseBody = webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo(1)
                .jsonPath("$.status").isEqualTo("SUCCESS")
                .jsonPath("$.data.paymentOrderId").isNotEmpty()
                .jsonPath("$.data.status").isEqualTo("PENDING")
                .jsonPath("$.data.externalReference").isEqualTo("EXT-1001")
                .returnResult()
                .getResponseBody();

        // Extraer el ID crudo usando JsonPath para no ensuciar con dependencias directas de deserialización
        String jsonResponse = new String(responseBody != null ? responseBody : new byte[0], java.nio.charset.StandardCharsets.UTF_8);
        String paymentOrderId = com.jayway.jsonpath.JsonPath.parse(jsonResponse).read("$.data.paymentOrderId", String.class);

        // 2. Retrieve it
        webTestClient.get()
                .uri("/payment-initiation/payment-orders/" + paymentOrderId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(1)
                .jsonPath("$.status").isEqualTo("SUCCESS")
                .jsonPath("$.data.paymentOrderId").isEqualTo(paymentOrderId)
                .jsonPath("$.data.externalReference").isEqualTo("EXT-1001");
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
        webTestClient.post().uri("/payment-initiation/payment-orders").bodyValue(request).exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.code").isEqualTo(-1).jsonPath("$.status").isEqualTo("ERROR")
                .jsonPath("$.message").isEqualTo("Invalid request payload or parameters");
    }

    @Test
    void shouldRetrieveAllPaymentOrders() {
        webTestClient.get().uri("/payment-initiation/payment-orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(1)
                .jsonPath("$.status").isEqualTo("SUCCESS")
                .jsonPath("$.data").isArray();
    }

    @Test
    void shouldReturnNotFoundForInvalidId() {
        webTestClient.get().uri("/payment-initiation/payment-orders/INVALID-ID")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(-1)
                .jsonPath("$.status").isEqualTo("ERROR")
                .jsonPath("$.message").isEqualTo("Resource not found");
    }

    @Test
    void shouldReturnNotFoundStatusForInvalidId() {
        webTestClient.get().uri("/payment-initiation/payment-orders/INVALID-ID/status")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(-1);
    }
}
