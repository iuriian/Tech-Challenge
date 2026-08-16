package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.PecaServicoComando
import br.com.fiap.oficina.application.service.ServicoComando
import br.com.fiap.oficina.application.service.ServicoService
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.AlterarStatusDto
import br.com.fiap.oficina.presentation.dto.OrcamentoDto
import br.com.fiap.oficina.presentation.dto.ServicoDto
import br.com.fiap.oficina.presentation.dto.TempoMedioExecucaoDto
import br.com.fiap.oficina.presentation.mapper.ServicoMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/servicos")
@Tag(name = "Serviços", description = "Operações relacionadas ao gerenciamento de serviços")
class ServicoController(
    private val service: ServicoService,
    private val mapper: ServicoMapper,
) {
    @PostMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Criar um novo servico",
        description = "Cadastra um novo servico no sistema",
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun criar(
        @Valid
        @RequestBody
        dto: ServicoDto,
    ): ServicoDto {
        val saved = service.salvar(toComando(dto, id = null))

        return mapper.toResponse(saved)
    }

    @PutMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Atualizar um servico",
        description = "Atualiza um servico no sistema",
    )
    fun atualizar(
        @Parameter(description = "ID do serviço a ser atualizado", required = true)
        @PathVariable id: String,
        @Valid
        @RequestBody
        dto: ServicoDto,
    ): ServicoDto {
        val saved = service.salvar(toComando(dto, id = Id.fromString(id)))

        return mapper.toResponse(saved)
    }

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Listar servico por ID",
        description = "Lista um servico do sistema pelo ID. Clientes podem consultar o status da própria OS.",
    )
    fun listarPorId(
        @Parameter(description = "ID do serviço", required = true)
        @PathVariable id: String,
    ): ServicoDto? =
        service.listarPorId(Id.fromString(id))?.let {
            mapper.toResponse(it)
        }

    @GetMapping("/{id}/orcamento")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Obter orçamento do servico",
        description =
            "Retorna o orçamento do servico, totalizando o valor das peças " +
                "(preço de venda multiplicado pela quantidade). " +
                "Clientes podem consultar o orçamento para decidir sobre a aprovação.",
    )
    fun obterOrcamento(
        @Parameter(description = "ID do serviço", required = true)
        @PathVariable id: String,
    ): OrcamentoDto =
        try {
            mapper.toResponse(service.obterOrcamento(Id.fromString(id)))
        } catch (exception: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, exception.message, exception)
        }

    @PatchMapping("/{id}/avancar")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Avançar status da OS",
        description =
            "Move a ordem de serviço para o próximo status no fluxo. " +
                "A partir de AGUARDANDO_APROVACAO o próximo passo é EM_EXECUCAO. " +
                "Retorna 422 se o serviço já estiver em um estado final.",
    )
    fun avancarStatus(
        @Parameter(description = "ID do serviço", required = true)
        @PathVariable id: String,
    ): ServicoDto =
        try {
            mapper.toResponse(service.avancarStatus(Id.fromString(id)))
        } catch (exception: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, exception.message, exception)
        } catch (exception: IllegalStateException) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, exception.message, exception)
        }

    @PatchMapping("/{id}/status")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Alterar status da OS",
        description =
            "Altera o status da ordem de serviço para um status específico, " +
                "respeitando as transições permitidas pela máquina de estados. " +
                "Clientes só podem aprovar (EM_EXECUCAO) ou recusar (CANCELADA) a partir de AGUARDANDO_APROVACAO. " +
                "Retorna 422 se a transição solicitada não for permitida.",
    )
    fun alterarStatus(
        @Parameter(description = "ID do serviço", required = true)
        @PathVariable id: String,
        @Valid @RequestBody dto: AlterarStatusDto,
    ): ServicoDto =
        try {
            mapper.toResponse(service.alterarStatus(Id.fromString(id), dto.status))
        } catch (exception: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, exception.message, exception)
        } catch (exception: IllegalStateException) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, exception.message, exception)
        }

    @GetMapping("/cliente/{clienteId}")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Listar serviços por cliente",
        description =
            "Lista todas as ordens de serviço associadas a um cliente. " +
                "Clientes podem usar este endpoint para acompanhar o progresso de seus próprios serviços.",
    )
    fun listarPorCliente(
        @Parameter(description = "ID do cliente", required = true)
        @PathVariable clienteId: String,
    ): List<ServicoDto> = service.listarPorCliente(Id.fromString(clienteId)).map { mapper.toResponse(it) }

    @GetMapping("/metricas/tempo-medio")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Tempo médio de execução",
        description =
            "Retorna o tempo médio (em minutos) entre o início e a finalização dos serviços concluídos, " +
                "junto com o total de ordens consideradas. Retorna null para tempoMedioMinutos quando não há " +
                "serviços finalizados.",
    )
    fun tempoMedioExecucao(): TempoMedioExecucaoDto = mapper.toResponse(service.calcularTempoMedioExecucao())

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Listar servicos",
        description = "Lista todos os servicos",
    )
    fun listarTodos(): List<ServicoDto> =
        service.listarTodos().map {
            mapper.toResponse(it)
        }

    @DeleteMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Deletar servico por ID",
        description = "Deleta um servico do sistema pelo ID",
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletarPorId(
        @Parameter(description = "ID do servico a ser removido", required = true)
        @PathVariable id: String,
    ) {
        service.deletarPorId(Id.fromString(id))
    }

    private fun toComando(
        dto: ServicoDto,
        id: Id?,
    ): ServicoComando =
        ServicoComando(
            id = id,
            descricao = dto.descricao,
            funcionarioId = Id.fromString(dto.funcionarioId),
            status = dto.status ?: OrdemServicoStatus.RECEBIDA,
            clienteId = Id.fromString(dto.clienteId),
            veiculoId = Id.fromString(dto.veiculoId),
            pecas = dto.pecas.map { PecaServicoComando(Id.fromString(it.pecaId), it.quantidade) },
        )
}
