# BankFlow

BankFlow es un reto técnico para demostrar dominio en Java + Spring Boot aplicando conceptos críticos de sistemas financieros: transferencias atómicas entre cuentas, consistencia ACID, concurrencia segura, idempotencia, auditoría, observabilidad, contenedores y CI/CD. Diseñado para usarse como proyecto de portfolio en GitHub.

---

## Tabla de contenido
- Descripción
- Objetivos
- Requisitos
- Estructura del repositorio
- Arranque rápido (local)
- Variables de entorno / application.yml ejemplo
- Endpoints principales (ejemplos curl)
- Migraciones y seed data
- Tests
- Observabilidad y métricas
- CI / GitHub Actions
- Diseño técnico y decisiones clave
- Criterios de evaluación
- Milestones sugeridos
- Plantillas de commit / PR
- Contribuir
- Licencia

---

## Descripción
BankFlow implementa un motor de transferencias bancarias entre cuentas con garantías de consistencia y seguridad: crear cuentas, realizar transferencias (intra-banco) con estados PENDING/COMPLETED/FAILED/CANCELLED, idempotencia, registro de auditoría y emisión de eventos para conciliación.

---

## Objetivos
- Transferencias atómicas y ACID.
- Evitar race conditions y doble gasto en concurrencia.
- Idempotencia en peticiones cliente.
- Persistencia por servicio (PostgreSQL recomendado) y migraciones.
- Observabilidad: logs, métricas y health.
- Automatización: Docker + docker-compose + GitHub Actions.
- Cobertura de tests: unitarios, integración con Testcontainers, E2E opcional.

---

## Requisitos
- Java 17+ (recomendado) o Java 21
- Maven o Gradle
- Docker y Docker Compose
- Git
- (Opcional) Kafka / RabbitMQ para eventos asíncronos
- (Opcional) Grafana / Prometheus para visualización de métricas

---

## Estructura del repositorio
- /bankflow-app
  - src/main/java/...
    - config
    - controller
    - dto
    - service
    - repository
    - model (entities)
    - exception
    - audit
    - security
  - src/test/java/...
  - Dockerfile
  - application.yml (perfiles: dev, test, prod)
- /scripts (migrations, seed)
- docker-compose.yml
- .github/workflows/ci.yml
- README.md
- openapi.yml

---

## Arranque rápido (local)

1. Clonar repo:
   - git clone <tu-repo>
   - cd bankflow

2. Configurar variables (usar `application.yml.example`):
   - cp bankflow-app/src/main/resources/application.yml.example bankflow-app/src/main/resources/application.yml
   - Editar credenciales si es necesario.

3. Levantar servicios con Docker Compose:
   - docker-compose up -d --build
   - Esto arranca: app, postgres y (opcional) broker.

4. Ejecutar la aplicación (si no se usa contenedor):
   - cd bankflow-app
   - ./mvnw spring-boot:run

5. Acceder:
   - Health: http://localhost:8080/actuator/health
   - Swagger / OpenAPI: http://localhost:8080/swagger-ui.html o /swagger-ui/index.html

---

## Variables de entorno / application.yml ejemplo

Provee un `application.yml.example` con claves principales:

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bankflow
    username: bankflow
    password: bankflow
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: false
flyway:
  enabled: true
  locations: classpath:db/migration
server:
  port: 8080
security:
  jwt:
    secret: changeme1234567890
    expiration: 3600

(Guarda secretos en variables de entorno en CI / producción.)

---

## Endpoints principales (resumen y ejemplos)

Accounts
- POST /api/v1/accounts
  - Body: { "ownerName":"Ana", "currency":"EUR" }
  - curl:
    - curl -X POST http://localhost:8080/api/v1/accounts -H "Content-Type: application/json" -d '{"ownerName":"Ana","currency":"EUR"}'

- GET /api/v1/accounts/{id}

Transfers
- POST /api/v1/transfers
  - Body: { "fromAccountId":"...", "toAccountId":"...", "amount":100.00, "currency":"EUR", "reference":"orden-123" }
  - Header opcional: `Idempotency-Key: <uuid>`
  - curl:
    - curl -X POST http://localhost:8080/api/v1/transfers -H "Content-Type: application/json" -H "Idempotency-Key: 1111-2222" -d '{"fromAccountId":"a","toAccountId":"b","amount":10.00,"currency":"EUR","reference":"tst"}'

- GET /api/v1/transfers/{id}
- GET /api/v1/transfers?accountId=...&status=...

Actuator
- GET /actuator/health
- GET /actuator/prometheus (si Micrometer Prometheus está habilitado)

---

## Migraciones y seed data
- Flyway está configurado para manejar migraciones en `src/main/resources/db/migration`.
- Incluye scripts:
  - V1__create_accounts.sql
  - V2__create_transactions.sql
  - V3__create_audit_events.sql
- Para cargar datos de ejemplo, incluir script `V100__seed_data.sql` (opcional).

---

## Persistencia y esquema (resumen)
Tabla `accounts`:
- id UUID PK, owner_name, currency, balance numeric(18,2), status, created_at, updated_at.

Tabla `transactions`:
- id UUID PK, from_account_id, to_account_id, amount, currency, status, idempotency_key, reference, error_message, created_at, updated_at.

Índices:
- Unique(idempotency_key, from_account_id)
- Index(status), index(created_at)

---

## Concurrencia, ACID y idempotencia (resumen de implementación)

- Toda transferencia ejecuta una transacción DB (@Transactional).
- Uso recomendado: SELECT ... FOR UPDATE sobre las filas de `accounts` para bloquear saldo origen y destino en la misma transacción y evitar condiciones de carrera.
- Alternativa: Optimistic Locking con columna `version` (PROS/CONS explicados más abajo).
- Flujo:
  1. Crear fila `transactions` status=PENDING.
  2. SELECT FOR UPDATE accounts.
  3. Comprobar fondos; si insuficientes → marcar FAILED y rollback.
  4. Actualizar saldos, marcar transactions COMPLETED, emitir evento.
  5. Commit.
- Idempotencia:
  - Cliente puede enviar `Idempotency-Key`.
  - Si existe transacción con esa key y status COMPLETED → devolver resultado guardado.
  - Si existe y está PENDING → devolver 202 o gestionar según diseño (espera/reintento).

Trade-offs:
- SELECT FOR UPDATE: simple, consistente, pero puede reducir concurrencia en alto throughput.
- Optimistic Locking: mejor paralelismo, pero aumenta posibilidad de rollback/retry en escrituras altas.

---

## Tests

- Unitarios: lógica de negocio, validaciones, servicio de idempotencia.
- Integración: Testcontainers PostgreSQL; tests de concurrencia (p. ej. 50 hilos que intentan debitar la misma cuenta).
- E2E: docker-compose + suite RestAssured.
- Comando:
  - ./mvnw test

Cobertura recomendada: 80% en lógica crítica.

---

## Observabilidad
- Logs estructurados JSON (Logback).
- Spring Boot Actuator + Micrometer.
- Exposición de métricas para Prometheus.
- (Opcional) OpenTelemetry / Sleuth para trazas distribuidas.
- Emitir eventos para auditoría / conciliación (Kafka/RabbitMQ o tabla de eventos).

---

## CI / GitHub Actions (resumen)
- Pipeline mínimo:
  - build (mvn -DskipTests=false test)
  - static analysis (SpotBugs, Checkstyle)
  - integration tests (usar profile `ci` para Testcontainers)
  - build docker image (opcional push a GHCR)
- Archivo: .github/workflows/ci.yml (incluir badge en README).

---

## Criterios de evaluación
- Correctitud: transferencias ACID y prevención de saldo negativo.
- Concurrencia: pruebas que demuestren integridad con acceso concurrente.
- Robustez: idempotencia y manejo de errores.
- Observabilidad: logs, métricas y health.
- Infraestructura reproducible: Docker + docker-compose + CI funcional.
- Documentación clara y OpenAPI/Swagger.
- Código limpio, pruebas suficientes y commits coherentes.

---

## Milestones sugeridos
1. M1 (Día 1): Esqueleto + entidades + migraciones.
2. M2 (Día 2): Endpoints de cuentas + tests unitarios.
3. M3 (Día 3–4): Transferencias ACID con SELECT FOR UPDATE + tests de integración.
4. M4 (Día 5): Idempotencia + auditoría + eventos.
5. M5 (Día 6): Observabilidad + OpenAPI.
6. M6 (Día 7): Docker, docker-compose, CI y documentación final.

(Ajusta tiempos a tu disponibilidad.)

---

## Plantillas de commit / PR

Commit:
- feat(transfers): implement atomic transfer with SELECT FOR UPDATE
- fix(account): prevent negative balance on concurrent updates
- test(integration): add concurrency test for transfers

PR template (short):
- Resumen
- Qué cambia
- Cómo probar
- Checklist: tests añadidos, migraciones, docs actualizadas, CI passing
