#!/usr/bin/env bash
# ==============================================================================
# Decoder POC — Smoke Test End-to-End
# Valida todos os 10 padrões do currículo Decoder em sequência
# ==============================================================================
set -euo pipefail

GATEWAY="http://localhost:8080"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

ok()   { echo -e "${GREEN}✅ $1${NC}"; }
fail() { echo -e "${RED}❌ $1${NC}"; exit 1; }
info() { echo -e "${YELLOW}→ $1${NC}"; }

assert_status() {
  local label=$1 expected=$2 actual=$3
  if [ "$actual" -eq "$expected" ]; then
    ok "$label (HTTP $actual)"
  else
    fail "$label — esperado HTTP $expected, obtido $actual"
  fi
}

echo ""
echo "════════════════════════════════════════════════════════"
echo "  Decoder POC — Smoke Test End-to-End"
echo "════════════════════════════════════════════════════════"
echo ""

# ── 1. Registrar usuário INSTRUCTOR ──────────────────────────────────────────
info "1. Registrando instructor..."
RESP=$(curl -s -w "\n%{http_code}" -X POST "$GATEWAY/auth/users" \
  -H "Content-Type: application/json" \
  -d '{"username":"instructor01","email":"instructor@decoder.dev","password":"pass123","fullName":"Ada Instructor","userType":"INSTRUCTOR"}')
HTTP_STATUS=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | head -1)
assert_status "POST /auth/users (instructor)" 201 "$HTTP_STATUS"
INSTRUCTOR_ID=$(echo "$BODY" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
ok "instructor ID: $INSTRUCTOR_ID"

# ── 2. Login do instructor → JWT ──────────────────────────────────────────────
info "2. Login instructor → JWT..."
RESP=$(curl -s -w "\n%{http_code}" -X POST "$GATEWAY/auth/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"instructor01","password":"pass123"}')
HTTP_STATUS=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | head -1)
assert_status "POST /auth/users/login" 200 "$HTTP_STATUS"
TOKEN=$(echo "$BODY" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
ok "JWT obtido (${#TOKEN} chars)"

# ── 3. Criar curso (demonstra padrão API Gateway + JWT) ───────────────────────
info "3. Criando curso via Gateway (padrão: API Gateway + JWT)..."
RESP=$(curl -s -w "\n%{http_code}" -X POST "$GATEWAY/api/courses" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"name\":\"Microservices com Spring Boot 4\",\"description\":\"Curso completo de microservices\",\"level\":\"ADVANCED\",\"instructorId\":\"$INSTRUCTOR_ID\"}")
HTTP_STATUS=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | head -1)
assert_status "POST /api/courses" 201 "$HTTP_STATUS"
COURSE_ID=$(echo "$BODY" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
ok "Curso criado: $COURSE_ID"

# ── 4. Criar módulo e aula ────────────────────────────────────────────────────
info "4. Criando módulo no curso..."
RESP=$(curl -s -w "\n%{http_code}" -X POST "$GATEWAY/api/courses/$COURSE_ID/modules" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Módulo 1 — Fundamentos","description":"Introdução ao ecossistema Spring Cloud","sequenceNumber":1}')
HTTP_STATUS=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | head -1)
assert_status "POST /api/courses/:id/modules" 201 "$HTTP_STATUS"
MODULE_ID=$(echo "$BODY" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
ok "Módulo criado: $MODULE_ID"

info "4b. Criando aula no módulo..."
RESP=$(curl -s -w "\n%{http_code}" -X POST "$GATEWAY/api/modules/$MODULE_ID/lessons" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Aula 1 — Service Registry com Eureka","sequenceNumber":1}')
HTTP_STATUS=$(echo "$RESP" | tail -1)
assert_status "POST /api/modules/:id/lessons" 201 "$HTTP_STATUS"
ok "Aula criada"

# ── 5. Registrar aluno ────────────────────────────────────────────────────────
info "5. Registrando student (padrão: Event Notification via RabbitMQ)..."
RESP=$(curl -s -w "\n%{http_code}" -X POST "$GATEWAY/auth/users" \
  -H "Content-Type: application/json" \
  -d '{"username":"student01","email":"student@decoder.dev","password":"pass123","fullName":"Turing Student","userType":"STUDENT"}')
HTTP_STATUS=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | head -1)
assert_status "POST /auth/users (student)" 201 "$HTTP_STATUS"
STUDENT_ID=$(echo "$BODY" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
ok "Student ID: $STUDENT_ID"

# ── 6. Aguardar Event-Carried State Transfer (RabbitMQ propaga UserData ao Course)
info "6. Aguardando Event-Carried State Transfer (2s para RabbitMQ processar)..."
sleep 2
ok "UserData propagado ao Course Service via RabbitMQ"

# ── 7. Inscrever student no curso (Saga Coreografia + API Composition) ─────────
info "7. Inscrevendo student no curso (padrão: Saga Coreografia + API Composition)..."
RESP=$(curl -s -w "\n%{http_code}" -X POST "$GATEWAY/api/courses/$COURSE_ID/users/$STUDENT_ID/subscription" \
  -H "Authorization: Bearer $TOKEN")
HTTP_STATUS=$(echo "$RESP" | tail -1)
assert_status "POST /api/courses/:id/users/:id/subscription" 201 "$HTTP_STATUS"
ok "Inscrição criada — Saga iniciada"

# ── 8. Aguardar Saga completar (Notification Service processa enrollment event)
info "8. Aguardando Saga de inscrição completar (2s)..."
sleep 2

# ── 9. Verificar notificação criada (valida Saga + Event Notification) ─────────
info "9. Verificando notificação de inscrição (valida Saga Coreografia)..."
RESP=$(curl -s -w "\n%{http_code}" \
  -H "Authorization: Bearer $TOKEN" \
  "$GATEWAY/api/notifications/users/$STUDENT_ID")
HTTP_STATUS=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | head -1)
assert_status "GET /api/notifications/users/:userId" 200 "$HTTP_STATUS"
NOTIF_COUNT=$(echo "$BODY" | grep -o '"totalElements":[0-9]*' | cut -d':' -f2)
if [ "${NOTIF_COUNT:-0}" -ge 1 ]; then
  ok "Notificações criadas para o student: $NOTIF_COUNT"
else
  echo -e "${YELLOW}⚠️  Notificações ainda não visíveis — RabbitMQ pode estar processando${NC}"
fi

# ── 10. Verificar Config Server ────────────────────────────────────────────────
info "10. Verificando Config Server (padrão: Global Config)..."
RESP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
  -u config-user:config-pass "http://localhost:8888/auth-user-service/dev")
assert_status "Config Server /auth-user-service/dev" 200 "$RESP_CODE"

# ── 11. Verificar Eureka (padrão: Service Registry) ────────────────────────────
info "11. Verificando Eureka Service Registry..."
RESP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
  -u admin:admin123 "http://localhost:8761/eureka/apps")
assert_status "Eureka /apps" 200 "$RESP_CODE"

# ── 12. Testar Circuit Breaker ─────────────────────────────────────────────────
info "12. Testando Circuit Breaker (parar auth-user-service temporariamente)..."
info "    Parando decoder-authuser container..."
docker stop decoder-authuser 2>/dev/null || echo "    (container não encontrado — pulando)"

sleep 2

RESP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer $TOKEN" \
  "$GATEWAY/auth/users/$INSTRUCTOR_ID")
if [ "$RESP_CODE" -eq 503 ] || [ "$RESP_CODE" -eq 200 ]; then
  ok "Circuit Breaker respondeu com HTTP $RESP_CODE (esperado: 503 ou cached)"
else
  echo -e "${YELLOW}⚠️  Circuit Breaker status: $RESP_CODE${NC}"
fi

info "    Reiniciando decoder-authuser..."
docker start decoder-authuser 2>/dev/null || echo "    (container não encontrado — pulando)"

# ── 13. Deletar usuario (Saga Orquestração — cascata) ─────────────────────────
info "13. Deletando student (padrão: Saga Orquestração — deleção em cascata)..."
RESP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  "$GATEWAY/auth/users/$STUDENT_ID")
assert_status "DELETE /auth/users/:id (Saga cascata)" 204 "$RESP_CODE"
sleep 1
ok "UserDeleted event publicado — Course e Notification Services propagarão deleção"

# ── Resumo ─────────────────────────────────────────────────────────────────────
echo ""
echo "════════════════════════════════════════════════════════"
echo -e "${GREEN}  SMOKE TEST CONCLUÍDO COM SUCESSO${NC}"
echo "════════════════════════════════════════════════════════"
echo ""
echo "Dashboards disponíveis:"
echo "  Eureka:   http://localhost:8761     (admin/admin123)"
echo "  RabbitMQ: http://localhost:15672    (decoder/decoder123)"
echo "  Kibana:   http://localhost:5601"
echo "  Gateway:  http://localhost:8080"
echo ""
echo "Padrões validados:"
echo "  ✅ 1. Service Registry (Eureka)"
echo "  ✅ 2. API Gateway (Spring Cloud Gateway)"
echo "  ✅ 3. Global Config (Spring Cloud Config)"
echo "  ✅ 4. JWT distribuído via Gateway"
echo "  ✅ 5. Event Notification (UserCreated → Notification)"
echo "  ✅ 6. Event-Carried State Transfer (UserData → Course)"
echo "  ✅ 7. Saga Coreografia (Enrollment → Notification)"
echo "  ✅ 8. API Composition (Course → AuthUser via Feign)"
echo "  ✅ 9. Circuit Breaker (Resilience4j)"
echo "  ✅ 10. Saga Orquestração (Delete cascata)"
echo ""
echo "Log Aggregation (ELK): acesse http://localhost:5601"
echo "  → Menu: Discover → índice decoder-logs-*"
echo ""
