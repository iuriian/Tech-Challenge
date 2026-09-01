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
NS=tech-challenge-staging

# O deploy.sh cria o namespace, mas os secrets precisam dele antes
kubectl create namespace $NS

# Secrets (o script cria valores padrão de desenvolvimento se não existirem)
kubectl create secret generic db-credentials -n $NS \
  --from-literal=POSTGRES_USER=user \
  --from-literal=POSTGRES_PASSWORD='SENHA_FORTE'

# ATENÇÃO: o Service do Keycloak é LoadBalancer — no GKE o console de admin fica
# acessível pela internet. Nunca deixe a senha padrão em um cluster público.
kubectl create secret generic keycloak-credentials -n $NS \
  --from-literal=KEYCLOAK_ADMIN_USER=admin \
  --from-literal=KEYCLOAK_ADMIN_PASSWORD='SENHA_FORTE'

# Secret para puxar a imagem do GHCR (repositório privado)
kubectl create secret docker-registry ghcr-creds -n $NS \
  --docker-server=ghcr.io \
  --docker-username=SEU_USUARIO \
  --docker-password=SEU_PAT

./k8s/deploy.sh ghcr.io/seu-usuario/tech-challenge:latest staging $NS
```

Variáveis de ambiente aceitas pelo script: `POSTGRES_USER`, `POSTGRES_PASSWORD`,
`KEYCLOAK_ADMIN_USER`, `KEYCLOAK_ADMIN_PASSWORD`, `KEYCLOAK_IP_TIMEOUT`, `ROLLOUT_TIMEOUT`.

## Deploy pelo pipeline

O workflow `.github/workflows/cd.yml` dispara em push nas branches **`release/**`**
(e por `workflow_dispatch`), e encadeia: `build` → `smoke-test` → `promote` → `deploy-staging`.
O job de deploy chama `./k8s/deploy.sh "<imagem>" staging <namespace>`.

### Configuração do Environment `staging`

Toda a configuração de acesso ao cluster vive no **GitHub Environment**, não em
secrets do repositório. Crie em *Settings → Environments → New environment → `staging`*.

**Environment secrets:**

| Secret | Uso |
|---|---|
| `GCP_SA_KEY` | JSON da service account com papel `roles/container.developer` |
| `DB_PASSWORD` | Senha do PostgreSQL |
| `KEYCLOAK_ADMIN_PASSWORD` | Senha do admin do Keycloak |

**Environment variables:**

| Variável | Obrigatória | Uso |
|---|---|---|
| `GCP_PROJECT_ID` | sim | ID do projeto GCP |
| `GKE_CLUSTER` | sim | Nome do cluster |
| `GKE_ZONE` | sim | Zona/região do cluster |
| `KUBE_NAMESPACE` | não | Padrão: `tech-challenge-staging` |

**Secret do repositório** (não é específico do ambiente):

| Secret | Uso |
|---|---|
| `GHCR_PAT` | PAT com escopo `read:packages` para o cluster puxar a imagem |

O job valida essa configuração no primeiro passo e falha nomeando o que estiver
faltando, em vez de deixar o `gcloud` errar com "cluster not found".

### Por que Environment e não secrets do repositório

Secrets de ambiente só ficam disponíveis para jobs que declaram `environment: staging` —
o job de build, por exemplo, nunca enxerga a chave do GCP. O ambiente também é onde se
configuram **regras de proteção**: aprovadores obrigatórios, janela de espera e
restrição de branches. Nada disso existe para secrets de repositório.

### Por que o `GHCR_PAT` importa

Sem ele o workflow cai no `GITHUB_TOKEN`, que **expira quando o job termina**. O deploy
passa (a imagem é puxada durante o job) e, horas depois, o primeiro pod que reiniciar
— restart, escala, upgrade de nó do GKE — falha com `ImagePullBackOff` sem que nada
tenha mudado no código. Crie um PAT clássico com escopo `read:packages` e salve como
`GHCR_PAT`, ou torne o package público no GHCR.

## Verificação

```bash
NS=tech-challenge-staging
kubectl get pods -n $NS
kubectl get svc -n $NS                # pega o EXTERNAL-IP de app e keycloak
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
