#!/bin/bash

# ========================================
# Script de Deploy com CI/CD
# ========================================

set -e  # Para na primeira falha

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Variáveis de configuração
DOCKER_REGISTRY="seu-usuario"  # Mude para seu usuário Docker Hub ou registry
APP_NAME="oficina-app"
VERSION="${1:-latest}"
ENVIRONMENT="${2:-dev}"
NAMESPACE="oficina"

echo -e "${YELLOW}=== Deploy Script ===${NC}"
echo "Registry: $DOCKER_REGISTRY"
echo "App: $APP_NAME"
echo "Version: $VERSION"
echo "Environment: $ENVIRONMENT"

# ========================================
# 1. Build da imagem Docker
# ========================================
echo -e "\n${YELLOW}[1/5] Building Docker image...${NC}"
docker build -f Dockerfile.prod \
  -t $DOCKER_REGISTRY/$APP_NAME:$VERSION \
  -t $DOCKER_REGISTRY/$APP_NAME:latest \
  .

if [ $? -eq 0 ]; then
  echo -e "${GREEN}✓ Docker build succeeded${NC}"
else
  echo -e "${RED}✗ Docker build failed${NC}"
  exit 1
fi

# ========================================
# 2. Push para Docker Registry
# ========================================
echo -e "\n${YELLOW}[2/5] Pushing image to registry...${NC}"
docker push $DOCKER_REGISTRY/$APP_NAME:$VERSION
docker push $DOCKER_REGISTRY/$APP_NAME:latest

if [ $? -eq 0 ]; then
  echo -e "${GREEN}✓ Docker push succeeded${NC}"
else
  echo -e "${RED}✗ Docker push failed${NC}"
  exit 1
fi

# ========================================
# 3. Criar namespace
# ========================================
echo -e "\n${YELLOW}[3/5] Creating namespace...${NC}"
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

if [ $? -eq 0 ]; then
  echo -e "${GREEN}✓ Namespace created${NC}"
else
  echo -e "${RED}✗ Namespace creation failed${NC}"
  exit 1
fi

# ========================================
# 4. Deploy dos serviços
# ========================================
echo -e "\n${YELLOW}[4/5] Deploying services...${NC}"

# Atualizar imagem no deployment
kubectl set image deployment/spring-app \
  spring-app=$DOCKER_REGISTRY/$APP_NAME:$VERSION \
  -n $NAMESPACE \
  --record \
  2>/dev/null || echo "Deployment not found, applying files..."

# Aplicar manifestos
kubectl apply -f k8s/postgres.yaml -n $NAMESPACE
kubectl apply -f k8s/keycloak.yaml -n $NAMESPACE

# Atualizar imagem da app
sed "s|DOCKER_IMAGE|$DOCKER_REGISTRY/$APP_NAME:$VERSION|g" k8s/app.yaml | kubectl apply -f - -n $NAMESPACE

if [ $? -eq 0 ]; then
  echo -e "${GREEN}✓ Services deployed${NC}"
else
  echo -e "${RED}✗ Services deployment failed${NC}"
  exit 1
fi

# ========================================
# 5. Verificar status
# ========================================
echo -e "\n${YELLOW}[5/5] Checking deployment status...${NC}"

# Aguardar rollout
echo "Waiting for rollout..."
kubectl rollout status deployment/spring-app -n $NAMESPACE --timeout=5m || true

# Mostrar status
echo -e "\n${GREEN}=== Deployment Status ===${NC}"
kubectl get pods -n $NAMESPACE
echo ""
kubectl get svc -n $NAMESPACE

# ========================================
# Info de acesso
# ========================================
echo -e "\n${GREEN}=== Access Information ===${NC}"
echo "Namespace: $NAMESPACE"
echo "App Version: $VERSION"
echo ""
echo "Port forward (local testing):"
echo "  kubectl port-forward svc/spring-app 8080:8080 -n $NAMESPACE"
echo "  kubectl port-forward svc/keycloak 8081:8080 -n $NAMESPACE"
echo ""
echo "View logs:"
echo "  kubectl logs -f deployment/spring-app -n $NAMESPACE"
echo ""
echo "Delete deployment:"
echo "  kubectl delete all --all -n $NAMESPACE"

echo -e "\n${GREEN}✓ Deploy completed successfully!${NC}"