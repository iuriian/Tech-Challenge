#!/usr/bin/env bash
set -euo pipefail

SONAR_URL="${SONAR_HOST_URL:-http://localhost:9000}"
ADMIN_USER="${SONAR_ADMIN_USER:-admin}"
ADMIN_PASS="${SONAR_ADMIN_PASS:-admin}"
NEW_PASS="${SONAR_NEW_PASS:-c0cada}"
PROJECT_KEY="br.com.fiap.oficina:tech-challenge"
PROJECT_NAME="Tech-Challenge"
TOKEN_NAME="tech-challenge-ci"

wait_sonar() {
  echo "Aguardando SonarQube ficar disponivel em $SONAR_URL ..."
  local attempts=0
  until curl -sf "$SONAR_URL/api/system/status" | grep -q '"status":"UP"'; do
    attempts=$((attempts + 1))
    if [ "$attempts" -ge 30 ]; then
      echo "ERRO: SonarQube nao respondeu apos 5 minutos." >&2
      exit 1
    fi
    echo "  tentativa $attempts/30 — aguardando 10s..."
    sleep 10
  done
  echo "SonarQube disponivel."
}

change_default_password() {
  # SonarQube exige troca de senha no primeiro login quando usando o padrao "admin/admin"
  local http_code
  http_code=$(curl -s -o /dev/null -w "%{http_code}" \
    -u "$ADMIN_USER:$NEW_PASS" \
    "$SONAR_URL/api/authentication/validate")

  if [ "$http_code" = "200" ]; then
    echo "Senha ja foi alterada anteriormente, pulando etapa."
    return 0
  fi

  echo "Alterando senha padrao do admin..."
  curl -sf -X POST \
    -u "$ADMIN_USER:$ADMIN_PASS" \
    "$SONAR_URL/api/users/change_password" \
    -d "login=$ADMIN_USER&previousPassword=$ADMIN_PASS&password=$NEW_PASS"
  echo "Senha alterada para: $NEW_PASS"
}

create_project() {
  echo "Verificando se o projeto '$PROJECT_KEY' ja existe..."
  local exists
  exists=$(curl -sf \
    -u "$ADMIN_USER:$NEW_PASS" \
    "$SONAR_URL/api/projects/search?projects=$PROJECT_KEY" \
    | grep -c "\"key\":\"$PROJECT_KEY\"" || true)

  if [ "$exists" -gt 0 ]; then
    echo "Projeto '$PROJECT_KEY' ja existe, pulando criacao."
    return 0
  fi

  echo "Criando projeto '$PROJECT_NAME' ($PROJECT_KEY)..."
  curl -sf -X POST \
    -u "$ADMIN_USER:$NEW_PASS" \
    "$SONAR_URL/api/projects/create" \
    -d "project=$PROJECT_KEY&name=$PROJECT_NAME&visibility=public"
  echo "Projeto criado."
}

generate_token() {
  echo "Verificando token '$TOKEN_NAME'..."
  local existing
  existing=$(curl -sf \
    -u "$ADMIN_USER:$NEW_PASS" \
    "$SONAR_URL/api/user_tokens/search" \
    | grep -c "\"name\":\"$TOKEN_NAME\"" || true)

  if [ "$existing" -gt 0 ]; then
    echo "Token '$TOKEN_NAME' ja existe — para regenera-lo revogue primeiro:"
    echo "  curl -u $ADMIN_USER:<senha> -X POST $SONAR_URL/api/user_tokens/revoke -d 'name=$TOKEN_NAME'"
    return 0
  fi

  echo "Gerando token '$TOKEN_NAME'..."
  local response token
  response=$(curl -sf -X POST \
    -u "$ADMIN_USER:$NEW_PASS" \
    "$SONAR_URL/api/user_tokens/generate" \
    -d "name=$TOKEN_NAME")
  token=$(echo "$response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

  echo ""
  echo "========================================================"
  echo "  Token gerado — guarde-o agora, nao sera exibido novamente:"
  echo "  SONAR_TOKEN=$token"
  echo ""
  echo "  Use nas analises:"
  echo "  SONAR_LOGIN=$token ./gradlew sonar"
  echo "========================================================"
}

main() {
  wait_sonar
  change_default_password
  create_project
  generate_token
  echo ""
  echo "Setup concluido. Projeto disponivel em: $SONAR_URL/dashboard?id=$PROJECT_KEY"
}

main
