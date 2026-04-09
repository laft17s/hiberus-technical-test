# Decisiones de Diseño e IA Generativa

### BIAN Mapping (WSDL -> REST)
- Se determinó usar `Payment Initiation` como Service Domain.
- El objeto central es `PaymentOrder`.
- Operaciones REST derivadas del SOAP:
  - **SubmitPaymentOrder** $\rightarrow$ `POST /payment-initiation/payment-orders`
  - **GetPaymentOrderStatus** $\rightarrow$ `GET /payment-initiation/payment-orders/{id}/status`

### Arquitectura Hexagonal y Reatividad
- Se aislaron los modelos de base de datos (`PaymentOrderEntity`) de los del dominio (`PaymentOrder` + `Money`).
- Para favorecer el paralelismo, se usó **Project Reactor** (`Mono`/`Flux`).
- Todo el mapeamiento se realizó manualmente y de forma explícita para evitar errores con reflexiones bajo entornos funcionales como WebFlux.

### Correcciones Manuales y Ajustes Cognitivos de la IA
1. **Compilation Match**: OpenAPI Tools genera fechas tipo `date` como `java.time.LocalDate` en DTOs. El agente infirió inicialmente usar `String`, pero en la compilación hubo un error, por lo cual se auto-corrigió el `PaymentOrderRestMapper.java` en tiempo real.
2. **Idempotencia**: Se inyectó explícitamente en el `PaymentOrderUseCase` una validación contra el repositorio (`repository.existsByExternalReference()`) para abortar transacciones ya realizadas antes de ser insertadas (evitando Exceptions SQL puras y gestionándolas con el Dominio en base a `IdempotencyException`).
