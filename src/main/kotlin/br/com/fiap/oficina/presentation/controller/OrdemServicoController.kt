package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.dto.AlterarStatusRequest
import br.com.fiap.oficina.application.dto.OrcamentoResponse
import br.com.fiap.oficina.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.application.dto.TempoMedioExecucaoResponse
import br.com.fiap.oficina.application.service.OrdemServicoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/servicos")
@Tag(
    name = "Ordens de Serviço",
    description = "Operações relacionadas ao gerenciamento de ordens de serviço",
)
class OrdemServicoController(private val service: OrdemServicoService) {
    @PostMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Criar ordem de serviço",
        description = "Cadastra uma nova ordem de serviço",
    )
    fun criar(
        @Valid
        @RequestBody
        request: OrdemServicoRequest,
    ): OrdemServicoResponse = service.criar(request)

    @PutMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Atualizar ordem de serviço",
        description = "Atualiza uma ordem de serviço existente",
    )
    fun atualizar(
        @Parameter(description = "ID da ordem de serviço", required = true)
        @PathVariable id: String,
        @Valid
        @RequestBody
        request: OrdemServicoRequest,
    ): OrdemServicoResponse = service.atualizar(id, request)

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Buscar ordem de serviço por ID",
        description = "Busca uma ordem de serviço pelo identificador",
    )
    fun listarPorId(@PathVariable id: String): OrdemServicoResponse? = service.listarPorId(id)

    @GetMapping("/{id}/orcamento")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Obter orçamento da ordem de serviço",
        description = "Retorna o orçamento associado à ordem de serviço",
    )
    fun obterOrcamento(@PathVariable id: String): OrcamentoResponse = try {
        service.obterOrcamento(id)
    } catch (exception: IllegalArgumentException) {
        throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            exception.message,
            exception,
        )
    }

    @PatchMapping("/{id}/avancar")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Avançar status da ordem de serviço",
        description = "Move a ordem de serviço para o próximo status permitido",
    )
    fun avancarStatus(@PathVariable id: String): OrdemServicoResponse = try {
        service.avancarStatus(id)
    } catch (exception: IllegalArgumentException) {
        throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            exception.message,
            exception,
        )
    } catch (exception: IllegalStateException) {
        throw ResponseStatusException(
            HttpStatus.UNPROCESSABLE_ENTITY,
            exception.message,
            exception,
        )
    }

    @PatchMapping("/{id}/status")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Alterar status da ordem de serviço",
        description = "Altera o status respeitando as transições permitidas",
    )
    fun alterarStatus(
        @PathVariable id: String,
        @Valid
        @RequestBody
        request: AlterarStatusRequest,
    ): OrdemServicoResponse = try {
        service.alterarStatus(
            id = id,
            novoStatus = request.status,
        )
    } catch (exception: IllegalArgumentException) {
        throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            exception.message,
            exception,
        )
    } catch (exception: IllegalStateException) {
        throw ResponseStatusException(
            HttpStatus.UNPROCESSABLE_ENTITY,
            exception.message,
            exception,
        )
    }

    @GetMapping("/cliente/{clienteId}")
    @RolesAllowed("ATENDENTE", "ADMIN", "CLIENTE")
    @Operation(
        summary = "Listar ordens de serviço por cliente",
        description = "Lista as ordens de serviço associadas a um cliente",
    )
    fun listarPorCliente(@PathVariable clienteId: String): List<OrdemServicoResponse> =
        service.listarPorCliente(clienteId)

    @GetMapping("/metricas/tempo-medio")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Tempo médio de execução",
        description = "Retorna métricas de tempo das ordens de serviço finalizadas",
    )
    fun tempoMedioExecucao(): TempoMedioExecucaoResponse = service.calcularTempoMedioExecucao()

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Listar ordens de serviço",
        description = "Lista todas as ordens de serviço",
    )
    fun listarTodos(): List<OrdemServicoResponse> = service.listarTodos()

    @DeleteMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Excluir ordem de serviço",
        description = "Exclui uma ordem de serviço pelo identificador",
    )
    fun deletarPorId(@PathVariable id: String) {
        service.deletarPorId(id)
    }
}
