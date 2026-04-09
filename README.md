# Payment Order Service Migrado (BIAN & Clean Architecture)

Este repositorio contiene la migración del servicio legado de órdenes de pago SOAP, a una moderna API Arquitectónica basada en **Spring WebFlux (R2DBC)** orientada a contratos BIAN mediante **OpenAPI**, implementando **Clean Architecture (Hexagonal)**.

## Arquitectura

- **Contract-First**: La API está descrita en `src/main/resources/openapi.yaml`. Los Endpoints e Interfaces REST DTO se auto-generan vía `openapi-generator-maven-plugin`.
- **Arquitectura Hexagonal**: Separación limpia entre Domain, Application y Adapters. El Dominio es puro (sin librerías de Spring).
- **Reactiva**: El stack es asíncrono y no bloqueante empleando `spring-webflux` y `spring-data-r2dbc`.

## Requisitos de Ejecución
- Java 17+
- Maven
- Docker / Docker Compose

## Ejecución Local
*Requiere que un Postgres esté levantado de manera autónoma, o inicializarlo temporalmente.*
```bash
mvn spring-boot:run
```

## Ejecución Completa con Docker Compose
```bash
docker-compose up --build
```
Esto levantará:
1. Una base de datos `payment_postgres` aislada internamente.
2. La API `payment_api` expuesta en `http://localhost:8080`.

## Pruebas y Cobertura (QA Gate)
El proyecto incluye Testcontainers para integración robusta y validación JaCoCo/SpotBugs/Checkstyle.
Correr el sistema completo de validaciones (las configuraciones están en el `pom.xml`):
```bash
mvn clean verify
```

## Uso de Inteligencia Artificial
La implementación fue creada usando herramientas de IA y agentes contextuales. 
Puede revisar los detalles de Generación y Decisiones en la carpeta [/ai/](ai).
