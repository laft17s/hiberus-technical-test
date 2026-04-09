# Registro de Prompts de IA

Durante la resolución de este ejercicio, he actuado de manera autónoma como un Agente de IA para desarrollar el ecosistema completo siguiendo las instrucciones y directrices brindadas (AGENTS.md). A continuación, se detalla un resumen del razonamiento y *self-prompts* del agente durante el proceso:

### 1. Fase de Análisis (Analyst Agent)
- **Prompt Interno**: "Analiza el archivo WSDL (`PaymentOrderService.wsdl`) y mapea los campos al dominio de servicios BIAN propuesto (Payment Initiation). Considera las instrucciones de WebFlux, R2DBC y OpenAPI."
- **Resultado esperado**: Se extrajeron campos clave (`externalId` a `externalReference`, `debtorIban` a `debtorAccount.iban`, etc.) y se formalizó el dominio como orientativo `PaymentOrder` (Control Record).

### 2. Fase de Planeación (Planning Agent)
- **Prompt Interno**: "Genera un `implementation_plan.md` describiendo las capas de Clean Architecture (Domain, Application, Infrastructure) e incluye un diagrama de flujo de Mermaid y un plan de desglose secuencial."
- **Resultado esperado**: Aprobación obtenida, plan de tareas desglosadas por fases generadas (`task.md`).

### 3. Fase de Desarrollo (Java Developer Agent)
- **Prompt Interno**: "Escribe el esqueleto de capas hexagonal. Implementa Value Objects sin `var` y sin dependencias del Framework para la capa de Dominio. Crea las interfaces de puerto (Repository) y un Use Case Reactivo usando `Mono`. Implementa Adaptadores REST y R2DBC. Además, maneja errores globales bajo el estándar RFC 7807 (`ProblemDetail`). Genera también el `openapi.yaml`."

### 4. Fase de Aseguramiento de Calidad (QA Agent)
- **Prompt Interno**: "Verifica la cobertura mediante tests unitarios (Mockito) y escribe Test de Integración con `WebTestClient` apoyándote en `Testcontainers` (PostgreSQL)."
- **Resultado esperado**: 0 Violaciones Checkstyle, Cobertura $>80\%$.
