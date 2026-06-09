#!/usr/bin/env bash
# Build de todos os módulos Maven com Java 26
set -euo pipefail

JAVA26=$(/usr/libexec/java_home -v 26 2>/dev/null || echo "")
if [ -z "$JAVA26" ]; then
  echo "Java 26 não encontrado. Instale via: brew install --cask temurin@26"
  exit 1
fi

export JAVA_HOME="$JAVA26"
echo "Usando Java: $(java -version 2>&1 | head -1)"

cd "$(dirname "$0")/.."

echo "Building decoder-poc (todos os módulos)..."
mvn clean package -DskipTests --no-transfer-progress

echo ""
echo "Build concluído. JARs em cada módulo/target/"
echo ""
echo "Para subir com Docker Compose:"
echo "  docker compose up -d"
