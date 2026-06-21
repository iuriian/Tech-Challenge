# Relatório de Análise de Vulnerabilidades

**Projeto:** Tech-Challenge — Sistema Integrado de Atendimento e Execução de Serviços  
**Data:** 2026-06-20  
**Ferramenta:** SonarQube LTS Community Edition  
**Scanner:** SonarScanner CLI (sonarsource/sonar-scanner-cli)  
**Branch analisada:** `feat/refactor_kotlin_pattern`

---

## Resumo Executivo

| Métrica | Resultado | Rating |
|---|---|---|
| Bugs | 0 | A |
| Vulnerabilidades | 0 | A |
| Security Hotspots | 0 | A |
| Code Smells | 1 | A |
| Cobertura de testes | 92,5% | — |
| Duplicação de código | 0,0% | — |
| Linhas de código (NCLOC) | 2.058 | — |

Todos os ratings de Confiabilidade, Segurança e Manutenibilidade atingiram o nível **A** (máximo).

---

## Vulnerabilidades e Bugs

Nenhuma vulnerabilidade ou bug identificado.

---

## Security Hotspots

Nenhum Security Hotspot identificado.

---

## Issues Encontradas

### CODE_SMELL — MAJOR

| Campo | Valor |
|---|---|
| Arquivo | `src/main/kotlin/br/com/fiap/oficina/domain/entity/Peca.kt` |
| Linha | 20 |
| Severidade | MAJOR |
| Regra | Função com número excessivo de parâmetros |
| Mensagem | *"This function has 8 parameters, which is greater than the 7 authorized."* |

**Contexto:** O factory method `Peca.criar()` recebe 8 parâmetros (`codigo`, `nome`, `descricao`, `fabricante`, `fornecedor`, `precoDeCompra`, `precoDeVenda`, `qtdEstoque`). A regra do SonarQube limita a 7 parâmetros por função.

**Risco:** Baixo — trata-se de uma questão de legibilidade/manutenibilidade, sem impacto de segurança ou funcional.

**Sugestão de correção:** Introduzir um objeto de comando (`PecaCriacaoComando`) agrupando os parâmetros, seguindo o mesmo padrão já adotado em `ServicoComando`.

---

## Cobertura de Testes

A cobertura de 92,5% supera amplamente o mínimo exigido de **80%** pela especificação do projeto, e o gate de verificação configurado no JaCoCo (instruction ≥ 80%, branch ≥ 75%, line ≥ 80%) é validado a cada build.

---

## Conclusão

A análise não identificou **nenhuma vulnerabilidade de segurança, bug ou security hotspot**. O único issue encontrado é um code smell de nível MAJOR de baixo risco relacionado ao número de parâmetros de um factory method.

O projeto apresenta postura de segurança excelente para um MVP, com todos os ratings SonarQube no nível A.

---

## Como reproduzir a análise

```bash
# 1. Subir infraestrutura (PostgreSQL + SonarQube)
docker-compose up -d db sonarqube

# 2. Criar banco sonarqube (apenas na primeira vez)
docker exec oficina-db psql -U user -d oficina -c 'CREATE DATABASE sonarqube;'

# 3. Aguardar SonarQube estar disponível em http://localhost:9000

# 4. Gerar relatório de cobertura
./gradlew test jacocoTestReport

# 5. Executar análise
docker run --rm \
  -e SONAR_HOST_URL=http://host.docker.internal:9000 \
  -e SONAR_TOKEN=<token> \
  -v $(pwd):/usr/src \
  sonarsource/sonar-scanner-cli \
  -Dsonar.projectKey=br.com.fiap.oficina:tech-challenge \
  -Dsonar.projectName=Tech-Challenge \
  -Dsonar.sources=src/main/kotlin \
  -Dsonar.tests=src/test/kotlin \
  -Dsonar.java.binaries=build/classes \
  -Dsonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/test/jacocoTestReport.xml \
  -Dsonar.exclusions="**/OfficinaApplication*"
```

Dashboard: `http://localhost:9000/dashboard?id=br.com.fiap.oficina%3Atech-challenge`
