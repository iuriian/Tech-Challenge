# Deploy no Kubernetes (GKE) — Staging

Implementação mínima para subir a aplicação **oficina-mecanica** em um cluster GKE.

## Arquivos

| Arquivo | Conteúdo |
|---|---|
| `app/configmap.yaml` | ConfigMap `app-config` (nome do banco, realm e client do Keycloak) |
| `postgres/pvc.yaml` | PersistentVolumeClaim de 10Gi do PostgreSQL |
| `postgres/deployment.yaml` | Deployment + Service do PostgreSQL 16 |
| `keycloak/service.yaml` | Service `LoadBalancer` do Keycloak (aplicado antes do Deployment) |
| `keycloak/deployment.yaml` | Deployment do Keycloak 26 com import do realm `Fiap` |
| `app/deployment.yaml` | Deployment (2 réplicas) + Service `LoadBalancer` da aplicação |
| `deploy.sh` | Orquestra o deploy: secrets, configmaps, ordem e rollout |

> A ordem de aplicação é definida pelo `deploy.sh`, não pelo nome dos arquivos.

Para testar tudo isso localmente antes do GKE, veja [`kind/README.md`](../kind/README.md).

Os ConfigMaps `postgres-initdb` e `keycloak-realm` **não** são versionados aqui: o
`deploy.sh` os gera a partir de `conf/init-db/` e `conf/realm-export.json`, os mesmos
arquivos usados pelo `docker-compose.yml`.

## Como funciona a ordem do deploy

O `issuer-uri` validado pelo Spring Security precisa ser idêntico à URL pela qual o
usuário obteve o token. Por isso o script:

1. cria o Service do Keycloak e **espera o IP externo** do LoadBalancer;
2. aplica o Deployment do Keycloak com `KC_HOSTNAME=http://<IP>:8080`;
3. aplica a aplicação com `KEYCLOAK_URL=http://<IP>:8080` (issuer público) e
   `KEYCLOAK_JWK_SET_URI=http://keycloak:8080/...` (busca das chaves pela rede interna).

Se o IP não for atribuído dentro de `KEYCLOAK_IP_TIMEOUT` (padrão 300s), o deploy
continua usando o issuer interno `http://keycloak:8080` e emite um aviso.

## Pré-requisitos no GKE

```bash
gcloud container clusters create-auto tech-challenge-staging --region=us-central1
# ou, para cluster standard:
gcloud container clusters create tech-challenge-staging \
  --zone=us-central1-a --num-nodes=2 --machine-type=e2-standard-2

gcloud container clusters get-credentials tech-challenge-staging \
  --zone us-central1-a --project SEU_PROJECT_ID
```

## Deploy manual

```bash
# Secrets (opcional: o script cria valores padrão de desenvolvimento se não existirem)
kubectl create secret generic db-credentials \
  --from-literal=POSTGRES_USER=user \
  --from-literal=POSTGRES_PASSWORD='SENHA_FORTE'

kubectl create secret generic keycloak-credentials \
  --from-literal=KEYCLOAK_ADMIN_USER=admin \
  --from-literal=KEYCLOAK_ADMIN_PASSWORD='SENHA_FORTE'

# ATENÇÃO: o Service do Keycloak é LoadBalancer — no GKE o console de admin fica
# acessível pela internet. Nunca deixe a senha padrão em um cluster público.

# Secret para puxar a imagem do GHCR (repositório privado)
kubectl create secret docker-registry ghcr-creds \
  --docker-server=ghcr.io \
  --docker-username=SEU_USUARIO \
  --docker-password=SEU_PAT

./k8s/deploy.sh ghcr.io/seu-usuario/tech-challenge:latest staging default
```

Variáveis de ambiente aceitas pelo script: `POSTGRES_USER`, `POSTGRES_PASSWORD`,
`KEYCLOAK_ADMIN_USER`, `KEYCLOAK_ADMIN_PASSWORD`, `KEYCLOAK_IP_TIMEOUT`, `ROLLOUT_TIMEOUT`.

## Deploy pelo pipeline

O workflow `.github/workflows/cd.yml` já chama `./k8s/deploy.sh "<imagem>" staging default`
depois de buildar, testar (smoke test) e publicar a imagem no GHCR.

Secrets necessários no repositório GitHub:

| Secret | Obrigatório | Uso |
|---|---|---|
| `GCP_SA_KEY` | sim | JSON da service account com papel `roles/container.developer` |
| `GCP_PROJECT_ID` | sim | ID do projeto GCP |
| `GKE_CLUSTER` | sim | Nome do cluster |
| `GKE_ZONE` | sim | Zona/região do cluster |
| `DB_PASSWORD` | sim | Senha do PostgreSQL |
| `KEYCLOAK_ADMIN_PASSWORD` | sim | Senha do admin do Keycloak — o workflow falha sem ela |
| `GHCR_PAT` | **na prática, sim** | PAT com escopo `read:packages` para o cluster puxar a imagem |

### Por que o `GHCR_PAT` importa

Sem ele o workflow cai no `GITHUB_TOKEN`, que **expira quando o job termina**. O deploy
passa (a imagem é puxada durante o job) e, horas depois, o primeiro pod que reiniciar
— restart, escala, upgrade de nó do GKE — falha com `ImagePullBackOff` sem que nada
tenha mudado no código. Crie um PAT clássico com escopo `read:packages` e salve como
`GHCR_PAT`, ou torne o package público no GHCR.

## Verificação

```bash
kubectl get pods
kubectl get svc                       # pega o EXTERNAL-IP de app e keycloak
curl http://<APP_IP>/actuator/health  # deve retornar {"status":"UP"}
```

Swagger UI: `http://<APP_IP>/swagger-ui/index.html`

Token de teste (usuário `admin` / senha `admin` do realm importado):

```bash
curl -X POST "http://<KEYCLOAK_IP>:8080/realms/Fiap/protocol/openid-connect/token" \
  -d "client_id=oficina" -d "username=admin" -d "password=admin" \
  -d "grant_type=password"
```

## Troubleshooting

```bash
kubectl describe pod -l app=app
kubectl logs -l app=app -c wait-for-db --tail=30   # esperando o banco
kubectl logs -l app=app -c app --tail=100          # aplicação / Flyway
kubectl logs -l app=keycloak --tail=100
kubectl get events --sort-by=.lastTimestamp | tail -30
```

| Sintoma | Causa provável |
|---|---|
| `ImagePullBackOff` | Secret `ghcr-creds` ausente ou PAT sem escopo `read:packages` |
| App reinicia após o init container | Flyway falhou — ver logs do container `app` |
| `401` mesmo com token válido | Issuer divergente: token emitido por URL diferente de `KEYCLOAK_URL` |
| Keycloak em `CrashLoopBackOff` | Banco `keycloak` não criado — conferir ConfigMap `postgres-initdb` |
| PVC `Pending` | Cluster sem StorageClass padrão ou sem quota de disco |

## Limpeza

```bash
kubectl delete -f k8s/app/deployment.yaml -f k8s/keycloak/deployment.yaml \
  -f k8s/keycloak/service.yaml -f k8s/postgres/deployment.yaml -f k8s/app/configmap.yaml
kubectl delete pvc postgres-data
```
