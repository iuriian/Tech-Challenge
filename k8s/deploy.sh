#!/bin/bash

################################################################################
# Kubernetes Deployment Script
#
# Uso:
#   ./k8s/deploy.sh <docker-image> <environment> <namespace>
#
# Exemplos:
#   ./k8s/deploy.sh "ghcr.io/seu-usuario/tech-challenge:latest" staging default
#   ./k8s/deploy.sh "ghcr.io/seu-usuario/tech-challenge:latest" production default
#
# O que faz:
#   1. Cria namespace (se não existir)
#   2. Aplica secrets e configmaps
#   3. Cria volumes (PVCs)
#   4. Faz deploy de cada serviço em ordem
#   5. Atualiza imagem da aplicação
#   6. Verifica status de rollout
################################################################################

set -e  # Exit on error

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para prints coloridos
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

################################################################################
# Validação de Inputs
################################################################################

if [ $# -ne 3 ]; then
    log_error "Uso incorreto!"
    echo ""
    echo "Sintaxe: $0 <docker-image> <environment> <namespace>"
    echo ""
    echo "Exemplos:"
    echo "  $0 'ghcr.io/seu-usuario/tech-challenge:latest' staging default"
    echo "  $0 'ghcr.io/seu-usuario/tech-challenge:latest' production default"
    echo ""
    exit 1
fi

DOCKER_IMAGE="$1"
ENVIRONMENT="$2"
NAMESPACE="$3"

log_info "Iniciando deploy com:"
log_info "  Imagem: $DOCKER_IMAGE"
log_info "  Ambiente: $ENVIRONMENT"
log_info "  Namespace: $NAMESPACE"
echo ""

# Validar que o environment é válido
if [[ "$ENVIRONMENT" != "staging" && "$ENVIRONMENT" != "production" ]]; then
    log_error "Environment deve ser 'staging' ou 'production', recebido: $ENVIRONMENT"
    exit 1
fi

################################################################################
# Passo 1: Criar Namespace
################################################################################

log_info "Passo 1: Criando namespace '$NAMESPACE'..."

if kubectl get namespace "$NAMESPACE" &> /dev/null; then
    log_warning "Namespace '$NAMESPACE' já existe"
else
    kubectl create namespace "$NAMESPACE"
    log_success "Namespace criado"
fi

echo ""

################################################################################
# Passo 2: Aplicar Secrets
################################################################################

log_info "Passo 2: Aplicando secrets..."

# Database Secret (se arquivo existir)
if kubectl get secret db-credentials -n "$NAMESPACE" &> /dev/null; then
    log_warning "  → Secret db-credentials ja existe (criado pelo pipeline), mantendo"
elif [ -f "k8s/db/secret.yaml" ]; then
    log_info "  → Aplicando db/secret.yaml..."
    kubectl apply -f k8s/db/secret.yaml -n "$NAMESPACE"
    log_success "  → DB secret aplicado"
else
    log_warning "  → Arquivo k8s/db/secret.yaml não encontrado, pulando"
fi

# Keycloak Secret (se arquivo existir)
if [ -f "k8s/keycloak/secret.yaml" ]; then
    log_info "  → Aplicando keycloak/secret.yaml..."
    kubectl apply -f k8s/keycloak/secret.yaml -n "$NAMESPACE"
    log_success "  → Keycloak secret aplicado"
fi

echo ""

################################################################################
# Passo 3: Aplicar ConfigMaps
################################################################################

log_info "Passo 3: Aplicando configmaps..."

# Database ConfigMap
if [ -f "k8s/db/configmap.yaml" ]; then
    log_info "  → Aplicando db/configmap.yaml..."
    kubectl apply -f k8s/db/configmap.yaml -n "$NAMESPACE"
    log_success "  → DB configmap aplicado"
fi

# Database Init Scripts ConfigMap
if [ -f "k8s/db/init-scripts-configmap.yaml" ]; then
    log_info "  → Aplicando db/init-scripts-configmap.yaml..."
    kubectl apply -f k8s/db/init-scripts-configmap.yaml -n "$NAMESPACE"
    log_success "  → DB init scripts configmap aplicado"
fi

# Keycloak ConfigMap
if [ -f "k8s/keycloak/configmap.yaml" ]; then
    log_info "  → Aplicando keycloak/configmap.yaml..."
    kubectl apply -f k8s/keycloak/configmap.yaml -n "$NAMESPACE"
    log_success "  → Keycloak configmap aplicado"
fi

# Nginx ConfigMap
if [ -f "k8s/nginx/configmap.yaml" ]; then
    log_info "  → Aplicando nginx/configmap.yaml..."
    kubectl apply -f k8s/nginx/configmap.yaml -n "$NAMESPACE"
    log_success "  → Nginx configmap aplicado"
fi

# SonarQube ConfigMap
if [ -f "k8s/sonarqube/configmap.yaml" ]; then
    log_info "  → Aplicando sonarqube/configmap.yaml..."
    kubectl apply -f k8s/sonarqube/configmap.yaml -n "$NAMESPACE"
    log_success "  → SonarQube configmap aplicado"
fi

echo ""

################################################################################
# Passo 4: Criar PersistentVolumeClaims
################################################################################

log_info "Passo 4: Criando volumes (PVCs)..."

# Database PVC
if [ -f "k8s/db/pvc.yaml" ]; then
    log_info "  → Aplicando db/pvc.yaml..."
    kubectl apply -f k8s/db/pvc.yaml -n "$NAMESPACE"
    log_success "  → DB PVC criado"
fi

# SonarQube PVC
if [ -f "k8s/sonarqube/pvc.yaml" ]; then
    log_info "  → Aplicando sonarqube/pvc.yaml..."
    kubectl apply -f k8s/sonarqube/pvc.yaml -n "$NAMESPACE"
    log_success "  → SonarQube PVC criado"
fi

echo ""

################################################################################
# Passo 4.1: Aplicar Services (ANTES dos Deployments)
################################################################################

log_info "Passo 4.1: Aplicando Services..."

for SVC in db keycloak app sonarqube nginx; do
    if [ -f "k8s/$SVC/service.yaml" ]; then
        log_info "  → Aplicando $SVC/service.yaml..."
        kubectl apply -f "k8s/$SVC/service.yaml" -n "$NAMESPACE"
        log_success "  → Service $SVC aplicado"
    else
        log_warning "  → k8s/$SVC/service.yaml nao encontrado, pulando"
    fi
done

echo ""

################################################################################
# Passo 5: Deploy Database
################################################################################

log_info "Passo 5: Fazendo deploy do Database..."

if [ -f "k8s/db/deployment.yaml" ]; then
    log_info "  → Aplicando db/deployment.yaml..."
    kubectl apply -f k8s/db/deployment.yaml -n "$NAMESPACE"

    # Aguardar DB estar pronto
    log_info "  → Aguardando Database ficar Ready..."
    kubectl wait --for=condition=ready pod -l app=db -n "$NAMESPACE" --timeout=300s 2>/dev/null || {
        log_warning "  → Timeout aguardando DB, continuando mesmo assim..."
    }
    log_success "  → Database deployado"
else
    log_warning "Arquivo k8s/db/deployment.yaml não encontrado, pulando"
fi

echo ""

################################################################################
# Passo 6: Deploy Keycloak
################################################################################

log_info "Passo 6: Fazendo deploy do Keycloak..."

if [ -f "k8s/keycloak/deployment.yaml" ]; then
    log_info "  → Aplicando keycloak/deployment.yaml..."
    kubectl apply -f k8s/keycloak/deployment.yaml -n "$NAMESPACE"
    log_success "  → Keycloak deployado"
else
    log_warning "Arquivo k8s/keycloak/deployment.yaml não encontrado, pulando"
fi

echo ""

################################################################################
# Passo 7: Deploy Nginx
################################################################################

log_info "Passo 7: Fazendo deploy do Nginx..."

if [ -f "k8s/nginx/deployment.yaml" ]; then
    log_info "  → Aplicando nginx/deployment.yaml..."
    kubectl apply -f k8s/nginx/deployment.yaml -n "$NAMESPACE"
    log_success "  → Nginx deployado"
else
    log_warning "Arquivo k8s/nginx/deployment.yaml não encontrado, pulando"
fi

echo ""

################################################################################
# Passo 8: Deploy SonarQube
################################################################################

log_info "Passo 8: Fazendo deploy do SonarQube..."

if [ -f "k8s/sonarqube/deployment.yaml" ]; then
    log_info "  → Aplicando sonarqube/deployment.yaml..."
    kubectl apply -f k8s/sonarqube/deployment.yaml -n "$NAMESPACE"
    log_success "  → SonarQube deployado"
else
    log_warning "Arquivo k8s/sonarqube/deployment.yaml não encontrado, pulando"
fi

echo ""

################################################################################
# Passo 9: Deploy Service (Ingress/Nginx)
################################################################################

log_info "Passo 9: Fazendo deploy do Nginx Service..."

if [ -f "k8s/nginx/service.yaml" ]; then
    log_info "  → Aplicando nginx/service.yaml..."
    kubectl apply -f k8s/nginx/service.yaml -n "$NAMESPACE"
    log_success "  → Nginx Service deployado"
else
    log_warning "Arquivo k8s/nginx/service.yaml não encontrado, pulando"
fi

echo ""

################################################################################
# Passo 10: Deploy Aplicação Principal
################################################################################

log_info "Passo 10: Fazendo deploy da Aplicação..."

if [ -f "k8s/app/deployment.yaml" ]; then
    log_info "  → Aplicando app/deployment.yaml..."
    kubectl apply -f k8s/app/deployment.yaml -n "$NAMESPACE"
    log_success "  → App deployment criado"
else
    log_error "Arquivo k8s/app/deployment.yaml não encontrado!"
    exit 1
fi

echo ""

################################################################################
# Passo 11: Atualizar Imagem da Aplicação
################################################################################

log_info "Passo 11: Atualizando imagem da aplicação..."

log_info "  → Atualizando deployment/app com imagem: $DOCKER_IMAGE"

kubectl set image deployment/app \
    app="$DOCKER_IMAGE" \
    -n "$NAMESPACE" \
    --record

log_success "  → Imagem atualizada"

echo ""

################################################################################
# Passo 12: Aguardar Rollout
################################################################################

log_info "Passo 12: Aguardando rollout completo..."

if kubectl rollout status deployment/app -n "$NAMESPACE" --timeout=1800s; then
    log_success "  → Rollout completado com sucesso!"
else
    log_error "  → Rollout falhou ou timeout!"
    exit 1
fi

echo ""

################################################################################
# Passo 13: Verificação Final
################################################################################

log_info "Passo 13: Verificação final..."

echo ""
log_info "Status dos Pods:"
kubectl get pods -n "$NAMESPACE" -o wide

echo ""
log_info "Status dos Services:"
kubectl get svc -n "$NAMESPACE" -o wide

echo ""
log_info "Status das Deployments:"
kubectl get deployments -n "$NAMESPACE" -o wide

echo ""

################################################################################
# Resumo
################################################################################

log_success "✨ Deploy completado com sucesso!"
echo ""
log_info "Próximas ações:"
echo "  1. Verificar logs: kubectl logs -l app=app -n $NAMESPACE -f"
echo "  2. Verificar eventos: kubectl get events -n $NAMESPACE --sort-by='.lastTimestamp'"
echo "  3. Testar acesso: kubectl port-forward svc/nginx 8080:80 -n $NAMESPACE"
echo ""