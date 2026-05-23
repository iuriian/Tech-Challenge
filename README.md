# Tech-Challenge
Sistema Integrado de Atendimento e Execução de Serviços

## 📚 Sobre o Projeto

Este projeto é um **trabalho acadêmico** desenvolvido para o curso de **Pós-Graduação em Arquitetura e Desenvolvimento Java** da **FIAP (PosTech)**. O sistema foi criado como parte dos requisitos de avaliação do programa, demonstrando a aplicação prática dos conceitos aprendidos durante o curso.

## 🛠️ Tecnologias Utilizadas

### Backend
- **Kotlin** 2.2.x
- **Java** 21 (LTS)
- **Spring Boot** 3.4.0
- **Spring Data JPA**
- **Spring MVC**
- **MapStruct** 1.6.3 - Mapeamento de objetos
- **SpringDoc OpenAPI** 2.8.5 - Documentação da API

### Banco de Dados
- **PostgreSQL** 16

### Autenticação e Autorização
- **Keycloak** 26.2.1 - Gerenciamento de identidade e acesso

### Infraestrutura
- **Docker** & **Docker Compose**
- **Nginx** - Proxy reverso

### Qualidade de Código
- **JaCoCo** - Cobertura de testes
- **SonarQube** - Análise estática de código

### Build & Dependências
- **Gradle** (Kotlin DSL)

## 🚀 Como Executar o Projeto

### Pré-requisitos

- [Docker](https://www.docker.com/) e Docker Compose instalados
- [JDK 21](https://adoptium.net/) instalado (para execução local)
- [Gradle](https://gradle.org/) (ou use o wrapper incluso)

### Configuração do Hosts (Opcional)

Para utilizar os domínios configurados no Nginx, adicione as seguintes entradas no arquivo `/etc/hosts` (Linux/Mac) ou `C:\Windows\System32\drivers\etc\hosts` (Windows):
```

127.0.0.1 sso.postech.com.br
127.0.0.1 api.postech.com.br
```
---

### Opção 1: Executando com Docker Compose (Infraestrutura Completa)

Esta opção inicia todos os serviços de infraestrutura: PostgreSQL, Keycloak e Nginx.

```bash
# Clone o repositório
git clone <url-do-repositorio>
cd Tech-Challenge

# Inicie os containers
docker-compose up -d

# Para verificar os logs
docker-compose logs -f

# Para parar os containers
docker-compose down
```

**Serviços disponíveis:**

| Serviço    | URL                          | Credenciais          |
|------------|------------------------------|----------------------|
| PostgreSQL | `localhost:5432`             | user / password      |
| Keycloak   | `http://localhost:8081`      | admin / admin        |
| Nginx      | `http://localhost:80`        | -                    |

---

### Opção 2: Executando a Aplicação Spring Boot (Desenvolvimento)

Antes de executar a aplicação localmente, certifique-se de que o banco de dados está em execução (via Docker Compose ou instalação local).

```shell script
# Inicie apenas o banco de dados com Docker
docker-compose up -d db

# Execute a aplicação usando o Gradle Wrapper
./gradlew bootRun

# No Windows, use:
gradlew.bat bootRun
```


A aplicação estará disponível em: `http://localhost:8080`

---

### Opção 3: Executando com IDE (IntelliJ IDEA)

1. Importe o projeto como um projeto Gradle
2. Aguarde a sincronização das dependências
3. Inicie os serviços de infraestrutura: `docker-compose up -d db keycloak`
4. Execute a classe principal da aplicação

---

## 📖 Documentação da API

Após iniciar a aplicação, a documentação da API estará disponível via Swagger UI:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

## 🧪 Executando os Testes

```shell script
# Executar todos os testes
./gradlew test

# Executar testes com relatório de cobertura
./gradlew test jacocoTestReport
```


O relatório de cobertura será gerado em: `build/reports/jacoco/test/html/index.html`

## 📁 Estrutura do Projeto

```
Tech-Challenge/
├── conf/                    # Arquivos de configuração
│   ├── init-db/            # Scripts de inicialização do banco
│   ├── nginx.conf          # Configuração do Nginx
│   └── realm-export.json   # Configuração do Realm Keycloak
├── src/
│   ├── main/
│   │   ├── kotlin/         # Código fonte Kotlin
│   │   └── resources/      # Recursos da aplicação
│   └── test/               # Testes automatizados
├── build.gradle.kts        # Configuração do Gradle
├── docker-compose.yaml     # Orquestração dos containers
└── README.md
```


## 👥 Autores

Desenvolvido por alunos da **FIAP PosTech** - Turma de Arquitetura e Desenvolvimento Java.


