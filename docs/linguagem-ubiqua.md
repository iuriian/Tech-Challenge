# Linguagem Ubíqua — Sistema de Ordem de Serviço (Oficina Mecânica)

> Documento de referência do vocabulário do domínio (Ubiquitous Language) do projeto
> **Tech-Challenge / Oficina** — *Sistema Integrado de Atendimento e Execução de Serviços*.
>
> O objetivo é alinhar o vocabulário usado por negócio, código e documentação, de modo que
> o mesmo termo signifique a mesma coisa em qualquer artefato (conversa, API, banco, testes).
>
> O glossário está dividido em duas partes:
> - **Parte 1 — Termos presentes no código:** extraídos diretamente do domínio implementado.
> - **Parte 2 — Termos complementares sugeridos:** vocabulário típico de uma oficina mecânica
>   que ainda **não** está modelado, mas que completa o domínio de Ordem de Serviço.

---

## Contexto do domínio

Uma **oficina mecânica** recebe **veículos** de **clientes**, registra um **serviço**
(que cumpre o papel de **Ordem de Serviço**), diagnostica o problema, aplica **peças** retiradas
do **estoque** e mão de obra, e acompanha o trabalho por um ciclo de **status** até a **entrega**
do veículo. O acesso ao sistema é controlado por **perfis** (ADMIN, ATENDENTE, MECANICO, CLIENTE).

---

## Perfis de acesso (Roles)

Definidos no Keycloak e aplicados via `@RolesAllowed` (ver `KeycloakJwtRoleConverter`, `SecurityConfig`).

| Termo         | Significado                                                                | Onde aparece                                    |
|---------------|----------------------------------------------------------------------------|-------------------------------------------------|
| **ADMIN**     | Administrador do sistema; acesso total.                                    | `@RolesAllowed("ADMIN")`, Keycloak              |
| **ATENDENTE** | Recepciona o cliente, cadastra clientes/veículos e abre/gerencia serviços. | `@RolesAllowed("ATENDENTE")` nos controllers    |
| **MECANICO**  | Profissional que executa o diagnóstico e os reparos.                       | Perfil no Keycloak (ainda sem entidade própria) |
| **CLIENTE**   | Dono do veículo / solicitante do serviço.                                  | Perfil no Keycloak; entidade `Cliente`          |

---

# Parte 1 — Termos presentes no código

## Cliente

`domain/entity/Cliente.kt`, `ClienteService`, `ClienteController`, `ClienteDto`.

| Termo         | Definição                                                            | Atributo / Origem   |
|---------------|----------------------------------------------------------------------|---------------------|
| **Cliente**   | Pessoa (física ou jurídica) que possui veículos e contrata serviços. | `Cliente`           |
| **Nome**      | Nome/razão social do cliente.                                        | `Cliente.nome`      |
| **E-mail**    | Endereço de e-mail de contato do cliente.                            | `Cliente.email`     |
| **Documento** | Documento de identificação do cliente (CPF ou CNPJ).                 | `Cliente.documento` |
| **Endereço**  | Localização do cliente.                                              | `Cliente.endereco`  |
| **Contatos**  | Lista de formas de contato do cliente.                               | `Cliente.contatos`  |

### Contato

`domain/entity/Contato.kt`, `ContatoDto`.

| Termo                 | Definição                                               | Atributo           |
|-----------------------|---------------------------------------------------------|--------------------|
| **Contato**           | Forma de comunicação associada a um cliente.            | `Contato`          |
| **Tipo (de contato)** | Categoria do contato (ex.: celular, comercial, recado). | `Contato.tipo`     |
| **Telefone**          | Número de telefone do contato.                          | `Contato.telefone` |

### Endereço

`domain/entity/Endereco.kt`, `EnderecoDto`.

| Termo           | Definição                                    | Atributo               |
|-----------------|----------------------------------------------|------------------------|
| **Logradouro**  | Rua/avenida do endereço.                     | `Endereco.logradouro`  |
| **Número**      | Número do imóvel.                            | `Endereco.numero`      |
| **Complemento** | Informação adicional do endereço (opcional). | `Endereco.complemento` |
| **Bairro**      | Bairro do endereço.                          | `Endereco.bairro`      |
| **Cidade**      | Município do endereço.                       | `Endereco.cidade`      |
| **Estado**      | Unidade federativa (UF).                     | `Endereco.estado`      |
| **CEP**         | Código de Endereçamento Postal.              | `Endereco.cep`         |

## Documento (Value Object)

`domain/valueobject/Documento.kt`, `domain/valueobject/TipoPessoa.kt`.

| Termo                   | Definição                                                               | Origem                                                  |
|-------------------------|-------------------------------------------------------------------------|---------------------------------------------------------|
| **Documento**           | Objeto de valor que representa CPF ou CNPJ, com validação e formatação. | `Documento`                                             |
| **Número do documento** | Dígitos do documento (armazenado sem máscara).                          | `Documento.numero`, `ClienteDto.numeroDocumento`        |
| **Tipo de Pessoa**      | Distingue pessoa física de jurídica.                                    | `TipoPessoa`                                            |
| **Pessoa Física**       | Cliente identificado por **CPF**.                                       | `TipoPessoa.PESSOA_FISICA`                              |
| **Pessoa Jurídica**     | Cliente identificado por **CNPJ**.                                      | `TipoPessoa.PESSOA_JURIDICA`                            |
| **CPF**                 | Cadastro de Pessoa Física (11 dígitos).                                 | `Documento.cpf()`, `ValidadorCpf`                       |
| **CNPJ**                | Cadastro Nacional da Pessoa Jurídica (14 dígitos).                      | `Documento.cnpj()`, `ValidadorCnpj`                     |
| **Validar documento**   | Verifica se o número possui formato e dígito verificador válidos.       | `Documento.isFormatoValido()`, `ValidadorDocumento`     |
| **Formatar documento**  | Aplica a máscara de exibição (ex.: `000.000.000-00`).                   | `Documento.getNumeroFormatado()`, `FormatadorDocumento` |
| **Dígito verificador**  | Dígito calculado que confirma a validade do documento.                  | `calcularDigitoVerificador`                             |

## Veículo

`domain/entity/Veiculo.kt`, `VeiculoService`, `VeiculoController`, `VeiculoDTO`.

| Termo         | Definição                                                             | Atributo                        |
|---------------|-----------------------------------------------------------------------|---------------------------------|
| **Veículo**   | Automóvel do cliente que recebe serviços na oficina.                  | `Veiculo`                       |
| **Marca**     | Fabricante do veículo (ex.: Volkswagen).                              | `Veiculo.marca`                 |
| **Modelo**    | Modelo do veículo (ex.: Gol).                                         | `Veiculo.modelo`                |
| **Ano**       | Ano de fabricação/modelo.                                             | `Veiculo.ano`                   |
| **Placa**     | Placa de identificação do veículo (7 caracteres; chave de unicidade). | `Veiculo.placa`                 |
| **Motorista** | Cliente associado ao veículo (condutor/proprietário).                 | `Veiculo.motorista` → `Cliente` |

## Peça

`domain/entity/Peca.kt`, `PecaService`, `PecaController`, `PecaDto` / `PecaAtualizacaoDto`.

| Termo                          | Definição                                                     | Atributo / Método                  |
|--------------------------------|---------------------------------------------------------------|------------------------------------|
| **Peça**                       | Item de reposição usado nos serviços e controlado em estoque. | `Peca`                             |
| **Código (da peça)**           | Identificador de negócio único da peça (chave de unicidade).  | `Peca.codigo`                      |
| **Descrição**                  | Texto descritivo da peça.                                     | `Peca.descricao`                   |
| **Fabricante**                 | Quem fabrica a peça.                                          | `Peca.fabricante`                  |
| **Fornecedor**                 | Quem fornece a peça à oficina.                                | `Peca.fornecedor`                  |
| **Preço de compra**            | Custo de aquisição da peça.                                   | `Peca.precoDeCompra`               |
| **Preço de venda**             | Valor cobrado do cliente pela peça.                           | `Peca.precoDeVenda`                |
| **Quantidade em estoque**      | Saldo disponível da peça.                                     | `Peca.qtdEstoque`                  |
| **Ativo**                      | Indica se a peça está disponível para uso/venda.              | `Peca.ativo`                       |
| **Retirar peças (do estoque)** | Baixa do estoque; exige saldo suficiente.                     | `Peca.retirarPecas(qtd)`           |
| **Repor peças (no estoque)**   | Entrada/reposição de saldo no estoque.                        | `Peca.reporPecas(qtd)`             |
| **Desativar peça**             | Torna a peça indisponível (exclusão lógica).                  | `Peca.desativar()` / `deletarPeca` |
| **Reativar peça**              | Volta a disponibilizar uma peça inativa.                      | `Peca.reativar()`                  |

## Serviço e Ordem de Serviço

`domain/entity/Servico.kt`, `domain/entity/OrdemServico.kt`,
`domain/valueobject/Orcamento.kt` e `domain/enum/OrdemServicoStatus.kt`.

> **Nota de transição:** no domínio, **Serviço** representa uma operação disponível
> no catálogo da oficina, enquanto **Ordem de Serviço** representa o atendimento
> aberto para um cliente e seu veículo. Alguns nomes das camadas de aplicação,
> persistência e apresentação ainda mantêm `Servico` por compatibilidade e serão
> refatorados gradualmente.

| Termo                       | Definição                                                                  | Representação no domínio        |
|-----------------------------|----------------------------------------------------------------------------|---------------------------------|
| **Serviço**                 | Operação oferecida pela oficina, com descrição e valor.                    | `Servico`                       |
| **Descrição do serviço**    | Identificação da operação disponível no catálogo.                          | `Servico.descricao`             |
| **Valor do serviço**        | Valor cobrado pela operação ou mão de obra.                                | `Servico.valor`                 |
| **Ordem de Serviço (OS)**   | Atendimento aberto para um cliente e seu veículo.                          | `OrdemServico`                  |
| **Descrição da OS**         | Relato do atendimento ou trabalho solicitado.                             | `OrdemServico.descricao`        |
| **Status da OS**            | Etapa atual da ordem no fluxo de atendimento.                              | `OrdemServico.status`           |
| **Funcionário responsável** | Funcionário responsável pela execução da ordem.                            | `OrdemServico.funcionario`      |
| **Cliente da OS**           | Cliente atendido pela ordem de serviço.                                    | `OrdemServico.cliente`          |
| **Veículo da OS**           | Veículo atendido pela ordem de serviço.                                    | `OrdemServico.veiculo`          |
| **Peças da OS**             | Peças associadas ou consumidas durante o atendimento.                      | `OrdemServico.pecas`            |
| **Orçamento**               | Snapshot dos itens e valores calculados para uma ordem de serviço.         | `Orcamento` / `ItemOrcamento`   |

### Status da Ordem de Serviço (ciclo de vida)

`domain/enum/OrdemServicoStatus.kt`.

| Status                   | Descrição no código     | Significado de negócio                             |
|--------------------------|-------------------------|----------------------------------------------------|
| **RECEBIDA**             | "Recebida"              | OS aberta; veículo deu entrada na oficina.         |
| **EM_DIAGNOSTICO**       | "Em Diagnostico"        | Mecânico avalia o veículo e identifica o problema. |
| **AGUARDANDO_APROVACAO** | "Aguardando aprovação"  | Orçamento enviado; aguardando decisão do cliente.  |
| **EM_EXECUCAO**          | "Em Execução"           | Reparos e serviços sendo realizados.               |
| **FINALIZADA**           | "Finalizada"            | Trabalho concluído; veículo pronto.                |
| **ENTREGUE**             | "Entregue"              | Veículo devolvido ao cliente; OS encerrada.        |
| **CANCELADA**            | "Cancelada pelo cliente"| Orçamento recusado e atendimento cancelado.        |
## Operações / Ações do domínio (verbos)

Vocabulário de comandos usado em services e controllers — padronizá-lo evita sinônimos divergentes.

| Verbo                          | Significado                                                              | Exemplos no código                                                                  |
|--------------------------------|--------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| **Criar / Cadastrar / Salvar** | Persistir um novo registro.                                              | `criar`, `salvar`, `salvarCliente`, `salvarPeca`, `salvarVeiculo`                   |
| **Buscar / Listar**            | Consultar registros (por ID, nome, documento, código, placa, motorista). | `buscarPorId`, `buscarPorDocumento`, `buscarPorPlaca`, `listarTodos`, `listarPecas` |
| **Atualizar / Alterar**        | Modificar um registro existente.                                         | `atualizar`, `alterar`, `atualizarPeca`                                             |
| **Remover / Deletar**          | Excluir um registro (físico ou lógico).                                  | `remover`, `removerCliente`, `deletarPorId`, `deletarPeca` (lógico)                 |
| **Retirar / Repor**            | Movimentar saldo de estoque de peças.                                    | `retirarPecas`, `reporPecas`                                                        |
| **Desativar / Reativar**       | Exclusão lógica e sua reversão.                                          | `desativarPeca`, `reativarPeca`                                                     |

---

# Parte 2 — Evoluções complementares do domínio

> Os conceitos abaixo fazem parte do domínio da oficina. Alguns já começaram a
> ser modelados, enquanto outros ainda exigem persistência, casos de uso ou
> integração com a Ordem de Serviço.

## Conceitos centrais da OS

| Conceito                       | Definição                                                                                | Situação atual                                                                      |
|--------------------------------|------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| **Ordem de Serviço (OS)**      | Agregado que formaliza a abertura, o acompanhamento e o encerramento do atendimento.     | Representada por `OrdemServico`; contratos externos ainda possuem nomes legados.    |
| **Orçamento**                  | Estimativa de custos composta por peças e serviços, submetida ao cliente.                | `Orcamento` e `ItemOrcamento` modelados; aprovação, validade e persistência pendentes. |
| **Diagnóstico / Laudo técnico**| Avaliação do mecânico que identifica o problema e o reparo necessário.                   | O estado `EM_DIAGNOSTICO` existe; o laudo ainda não foi modelado.                    |
| **Aprovação / Reprovação**     | Decisão do cliente sobre o orçamento.                                                    | Transições existem parcialmente; decisão explícita do orçamento ainda está pendente. |
| **Item da Ordem de Serviço**   | Linha que vincula peça ou serviço com quantidade e preço registrado.                    | `ItemOrcamento` aceita `PECA` e `SERVICO`; associação de serviços à OS está pendente. |
| **Mão de obra**                | Trabalho cobrado por tarefa ou período.                                                  | Representada pelo `Servico` de catálogo; ainda precisa ser incorporada à OS.         |
| **Serviço de catálogo**        | Operação oferecida pela oficina, como troca de óleo, alinhamento ou revisão.              | Representada por `Servico`; CRUD e persistência específicos ainda estão pendentes.   |
## Pessoas e papéis

| Termo sugerido              | Definição                                       | Relação com o que já existe                                                        |
|-----------------------------|-------------------------------------------------|------------------------------------------------------------------------------------|
| **Mecânico / Funcionário**  | Profissional que executa diagnóstico e reparos. | Hoje só existe `Servico.funcionarioId` (String) e o role `MECANICO`, sem entidade. |
| **Proprietário do veículo** | Cliente dono do veículo.                        | Esclareceria o campo `Veiculo.motorista` (proprietário ≠ condutor).                |

## Estoque e suprimentos

| Termo sugerido                              | Definição                                       | Relação com o que já existe            |
|---------------------------------------------|-------------------------------------------------|----------------------------------------|
| **Movimentação de estoque**                 | Registro histórico de entradas/saídas de peças. | Formaliza `retirarPecas`/`reporPecas`. |
| **Reserva de peças**                        | Alocação de peças a uma OS antes do consumo.    | Vincularia `Peca` ↔ `Servico`.         |
| **Estoque mínimo / Ponto de ressuprimento** | Saldo que dispara reposição/compra.             | Complementa `qtdEstoque`.              |

## Atendimento, financeiro e pós-venda

| Termo sugerido                      | Definição                                                                        |
|-------------------------------------|----------------------------------------------------------------------------------|
| **Agendamento**                     | Reserva de data/horário para atendimento do veículo.                             |
| **Recepção / Checklist de entrada** | Vistoria inicial do veículo na chegada.                                          |
| **Quilometragem (odômetro)**        | Hodômetro do veículo no momento do serviço; relevante para histórico e revisões. |
| **Histórico de manutenção**         | Registro de todas as OS de um veículo.                                           |
| **Prazo / Tempo médio de execução** | Estimativa e acompanhamento do tempo de conclusão.                               |
| **Nota Fiscal / Faturamento**       | Documento fiscal emitido ao concluir a OS.                                       |
| **Pagamento**                       | Registro do pagamento do cliente (forma, valor, status).                         |
| **Garantia**                        | Período de cobertura do serviço/peça após a entrega.                             |
| **Cancelamento da OS**              | Encerramento sem execução (ex.: orçamento reprovado).                            |

---

## Observações de consistência

Pontos onde a linguagem do código diverge ou poderia ser unificada (apenas observações; nenhuma
mudança de código é proposta aqui):

- **"Serviço" com dois sentidos:** `Servico` é, na prática, a **Ordem de Serviço**, mas "serviço"
  também é o termo natural para *tipo de serviço prestado* (mão de obra). Convém escolher um nome
  para cada conceito (ex.: `OrdemDeServico` vs `Servico`/`TipoServico`).
- **Status no feminino** (`RECEBIDA`, `FINALIZADA`, `ENTREGUE`) reforça que descrevem uma *Ordem*
  (feminino), e não um *Serviço* (masculino) — outro indício de que o conceito é **Ordem de Serviço**.
- **`Veiculo.motorista`** aponta para `Cliente`. "Motorista" sugere condutor, enquanto o vínculo de
  negócio costuma ser **proprietário**. Padronizar o termo evita ambiguidade.
- **`Servico.funcionarioId` (String)** referencia um executor que não tem entidade própria; o domínio
  pede um conceito de **Mecânico/Funcionário** explícito.
- **Identificador de Veículo:** `Veiculo.idVeiculo` foge do padrão `id` usado nas demais entidades.

---

*Documento vivo: deve evoluir junto com o domínio. Ao introduzir um termo novo no código, atualize
este glossário para manter a linguagem ubíqua consistente entre negócio, API e implementação.*