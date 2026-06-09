#!/bin/bash
set -e

SERVICES=(
  "http://localhost:8761/actuator/health:Eureka Server"
  "http://localhost:8888/actuator/health:Config Server"
  "http://localhost:8081/actuator/health:AuthUser Service"
  "http://localhost:8082/actuator/health:Course Service"
  "http://localhost:8083/actuator/health:Notification Service"
  "http://localhost:8080/actuator/health:API Gateway"
)

TIMEOUT=180
INTERVAL=5

echo "Waiting for all services to be healthy..."

for SERVICE_INFO in "${SERVICES[@]}"; do
  URL="${SERVICE_INFO%%:*}"
  NAME="${SERVICE_INFO##*:}"
  elapsed=0

  while true; do
    if [ "$URL" = "http://localhost:8761/actuator/health" ]; then
      RESPONSE=$(curl -s -u admin:admin123 "$URL" 2>/dev/null || echo "")
    elif [ "$URL" = "http://localhost:8888/actuator/health" ]; then
      RESPONSE=$(curl -s -u config-user:config-pass "$URL" 2>/dev/null || echo "")
    else
      RESPONSE=$(curl -s "$URL" 2>/dev/null || echo "")
    fi

    if echo "$RESPONSE" | grep -q '"status":"UP"'; then
      echo "OK $NAME is UP"
      break
    fi

    if [ $elapsed -ge $TIMEOUT ]; then
      echo "TIMEOUT waiting for $NAME"
      exit 1
    fi

    echo "Waiting for $NAME... (${elapsed}s)"
    sleep $INTERVAL
    elapsed=$((elapsed + INTERVAL))
  done
done

echo ""
echo "All services are healthy!"
echo ""
echo "Dashboards:"
echo "  Eureka:   http://localhost:8761"
echo "  RabbitMQ: http://localhost:15672 (decoder/decoder123)"
echo "  Kibana:   http://localhost:5601"
echo "  Gateway:  http://localhost:8080"
