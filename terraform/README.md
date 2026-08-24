# Oficina — POC local com Kind + Terraform

Guia para subir a stack completa (`db`, `keycloak`, `sonarqube`, `app`, `nginx`) num cluster Kubernetes local, provisionado inteiramente via Terraform.

## Estrutura esperada do projeto

```text
meu-projeto/
├── Dockerfile
├── kind-cluster.yaml
├── conf/
│   ├── init-db/
│   ├── realm-export.json
│   └── nginx.conf
└── terraform/
    ├── providers.tf
    ├── variables.tf
    ├── db.tf
    ├── keycloak.tf
    ├── sonarqube.tf
    ├── app.tf
    ├── nginx.tf
    └── outputs.tf
```

`terraform/` e `conf/` ficam lado a lado na raiz do projeto — os `.tf` referenciam os arquivos de `conf/` via caminho relativo (`../conf/...`).

---

## 1. Pré-requisitos

| Ferramenta | Verificar instalação |
|---|---|
| Docker | `docker ps` |
| kubectl | `kubectl version --client` |
| kind | `kind version` |
| Terraform | `terraform version` |

No Windows, use o mesmo terminal (PowerShell, CMD ou WSL2) do início ao fim — misturar terminais diferentes com kubeconfigs distintos é uma fonte comum de erro de conexão.

---

## 2. Criar o cluster Kubernetes local (Kind)

`kind-cluster.yaml` (na raiz do projeto):

```yaml
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
name: oficina-local
nodes:
  - role: control-plane
    extraPortMappings:
      - containerPort: 30080   # nginx
        hostPort: 80
      - containerPort: 30081   # keycloak
        hostPort: 8081
      - containerPort: 30090   # sonarqube
        hostPort: 9000
```

```bash
kind create cluster --config kind-cluster.yaml
kind export kubeconfig --name oficina-local
kubectl config use-context kind-oficina-local
kubectl get nodes   # confirma status "Ready"
```

---

## 3. Buildar e carregar a imagem da aplicação

O Kubernetes local (Kind) **não enxerga imagens Docker do host automaticamente** — é preciso carregá-las explicitamente a cada novo build.

```bash
docker build -t oficina-app:0.1.0 .
kind load docker-image oficina-app:0.1.0 --name oficina-local
```

> Ao atualizar o código, repita esse passo com uma tag nova (`0.2.0`, etc.) e ajuste `var.app_image` no `terraform apply` — nunca use `latest`, senão o Kubernetes não detecta que a imagem mudou.

---

## 4. Configurar o `/etc/hosts` (roteamento por hostname no nginx)

O `nginx.conf` roteia por `server_name`, não por porta — então `localhost` sozinho não é suficiente. Adicione ao arquivo de hosts do seu sistema:

- **Windows** (editar como Administrador): `C:\Windows\System32\drivers\etc\hosts`
- **macOS/Linux**: `/etc/hosts`

```
127.0.0.1 sso.postech.com.br
127.0.0.1 api.postech.com.br
```

---

## 5. Provisionar a stack com Terraform

```bash
cd terraform
terraform init
terraform plan
terraform apply
```

O `terraform apply` cria, para cada serviço, o `Deployment` + `Service` (+ `ConfigMap`/`Secret`/`PersistentVolumeClaim` quando aplicável):

| Serviço | O que é criado |
|---|---|
| `db` | Secret de credenciais, ConfigMap, ConfigMap dos scripts de init (via `fileset()`), PVC, Deployment, Service |
| `keycloak` | ConfigMap, Secret, ConfigMap do realm (via `file()`), Deployment (com `initContainer` esperando o banco), Service `NodePort` |
| `sonarqube` | ConfigMap, 3 PVCs, Deployment (com `initContainer` + `startupProbe`), Service `NodePort` |
| `app` | ConfigMap, Deployment (com `initContainer`), Service `ClusterIP` |
| `nginx` | ConfigMap a partir do `conf/nginx.conf` real, Deployment, Service `NodePort` |

---

## 6. Verificar se tudo subiu

```bash
kubectl get pods
```

Espere todos os Pods em `1/1 Running` antes de testar o acesso — especialmente `sonarqube`, que demora mais para responder ao boot.

Se algum Pod não estabilizar, diagnostique com:

```bash
kubectl logs <nome-do-pod>
kubectl describe pod <nome-do-pod>
```

---

## 7. Acessar a aplicação

| Serviço | URL                                               |
|---|---------------------------------------------------|
| App / Swagger (via nginx) | `http://api.postech.com.br/swagger-ui/index.html` |
| Keycloak | `http://localhost:8081`                           |
| SonarQube | `http://localhost:9000`                           |

Para depurar o `app` isoladamente, sem passar pelo nginx (útil se o proxy estiver com problema):

```bash
kubectl port-forward service/app 8080:8080
# depois: http://localhost:8080/swagger-ui/index.html
```

---

## 8. Atualizar a aplicação após uma mudança de código

```bash
docker build -t oficina-app:0.2.0 .
kind load docker-image oficina-app:0.2.0 --name oficina-local
cd terraform
terraform apply -var="app_image=oficina-app:0.2.0"
```

---

## 9. Derrubar o ambiente

Remover só os recursos da aplicação (mantém o cluster de pé):

```bash
cd terraform
terraform destroy
```

Remover o cluster inteiro:

```bash
kind delete cluster --name oficina-local
```

---

## Solução de problemas comuns

| Sintoma | Causa provável | O que fazer |
|---|---|---|
| `ImagePullBackOff` | Imagem não carregada no Kind | Repetir `kind load docker-image` |
| `Pod` preso em `Pending` | PVC sem storage disponível, ou recursos do nó esgotados | `kubectl describe pod` → seção `Events` |
| `CrashLoopBackOff` no app/keycloak/sonarqube | Banco ainda não disponível, ou erro de config | `kubectl logs <pod> --previous` |
| `net/http: TLS handshake timeout` no `kubectl`/Terraform | Cluster sobrecarregado, VPN corporativa interceptando TLS local, ou kubeconfig desatualizado após recriar o cluster | `kind export kubeconfig --name oficina-local`; testar sem VPN; `wsl --shutdown` (Windows) e reabrir o Docker Desktop |
| `nodePort ... already allocated` no Terraform | Duas definições de Service usando a mesma porta | Conferir `kubectl get svc -A` e ajustar o `node_port` em conflito |
| Acesso via `localhost/...` cai sempre no serviço errado | `nginx.conf` roteia por `server_name`, não por porta | Configurar `/etc/hosts` com os hostnames corretos (passo 4) |
| Porta `NodePort` inacessível mesmo com o serviço `Running` | `extraPortMappings` do Kind não inclui essa porta, ou cluster foi recriado sem esses mapeamentos | Adicionar a porta no `kind-cluster.yaml` e recriar o cluster, ou usar `kubectl port-forward` como alternativa temporária |

