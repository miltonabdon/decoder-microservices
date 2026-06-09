# Decoder POC — Microservices EAD Platform

POC completa que implementa todos os padrões do currículo do curso [Decoder](https://decoder.dev), com stack atualizada: **Java 26 + Spring Boot 4.0.6 + Spring Cloud 2025.1.1 (Oakwood)**.

## Arquitetura

```
API Gateway :8080  (Spring Cloud Gateway + JWT + Circuit Breaker)
├── auth-user-service :8081  (AuthUser + JWT + RabbitMQ publisher)
├── course-service    :8082  (Feign + RabbitMQ consumer/publisher)
└── notification-service :8083  (3 RabbitMQ consumers + DLQ)

Infraestrutura:
├── eureka-server  :8761   Service Registry
├── config-server  :8888   Centralized Config
├── PostgreSQL     :5432   3 databases separadas
├── RabbitMQ       :5672   Topic exchanges + DLQ
└── ELK Stack      :9200/:5601  Log Aggregation
```

## 10 Padrões Implementados

| # | Padrão | Tecnologia | Onde |
|---|---|---|---|
| 1 | Service Registry | Eureka | eureka-server + clientes |
| 2 | API Gateway | Spring Cloud Gateway | api-gateway |
| 3 | Circuit Breaker | Resilience4j | api-gateway → serviços |
| 4 | Global Config | Spring Cloud Config | config-server + config-repo |
| 5 | Saga Coreografia | RabbitMQ | enrollment flow |
| 6 | Saga Orquestração | RabbitMQ | user deletion cascade |
| 7 | Event Notification | RabbitMQ | UserCreated → Notification |
| 8 | Event-Carried State Transfer | RabbitMQ | UserData → Course |
| 9 | API Composition | OpenFeign | Course → AuthUser |
| 10 | Log Aggregation | ELK Stack | todos os serviços |

## Stack

- **Java 26.0.1** + **Spring Boot 4.0.6**
- **Spring Cloud 2025.1.1** (Oakwood)
- **Spring Framework 7.x** + Jakarta EE 11
- **JJWT 0.12.6**, **Resilience4j 2.x**, **Flyway 10.x**
- **PostgreSQL 16**, **RabbitMQ 4.3.1**, **ELK 9.4.2**

## Subir a stack completa

```bash
# Build de todos os módulos
export JAVA_HOME=$(/usr/libexec/java_home -v 26)
mvn clean package -DskipTests

# Subir toda a infraestrutura + apps
docker compose up -d

# Aguardar todos os serviços ficarem healthy
./scripts/wait-for-services.sh

# Executar smoke test end-to-end
./scripts/demo.sh
```

## Subir só infraestrutura (dev local)

```bash
docker compose -f docker-compose.infra.yml up -d
# Rodar cada serviço na IDE ou com: mvn spring-boot:run -pl auth-user-service
```

## Validar manualmente

```bash
# 1. Registrar usuário
curl -X POST http://localhost:8080/auth/users \
  -H "Content-Type: application/json" \
  -d '{"username":"milton","email":"milton@decoder.dev","password":"pass123","fullName":"Milton Abdon","userType":"INSTRUCTOR"}'

# 2. Login → JWT
TOKEN=$(curl -s -X POST http://localhost:8080/auth/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"milton","password":"pass123"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 3. Criar curso
curl -X POST http://localhost:8080/api/courses \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Microservices na prática","level":"ADVANCED"}'
```

## Dashboards

| Dashboard | URL | Credenciais |
|---|---|---|
| Eureka | http://localhost:8761 | admin/admin123 |
| RabbitMQ | http://localhost:15672 | decoder/decoder123 |
| Kibana | http://localhost:5601 | — |
| API Gateway | http://localhost:8080 | — |

## Estrutura hexagonal (padrão em todos os serviços)

```
adapter/
  in/
    controller/    ← REST endpoints
    consumer/      ← RabbitMQ listeners
    security/      ← JWT filter (auth-user only)
  out/
    persistence/   ← JPA repositories + adapters
    messaging/     ← RabbitMQ publishers
    client/        ← Feign clients (course only)
domain/
  model/           ← JPA entities + enums
  port/            ← interfaces (ports)
  service/         ← business logic
application/
  usecase/         ← use case orchestration
config/            ← Spring beans (RabbitMQ, Security)
```

## Caveats desta POC

- **Java 26** é feature release (expira set/2026) — para produção use Java 21 LTS
- **Deploy**: módulo de infra usa Docker Compose local (não Heroku)
- **Versão Spring Boot 4.x**: Jackson 3.x com breaking changes vs 2.x
- **Resilience4j**: usa `spring-boot3` starter enquanto não há `spring-boot4`
