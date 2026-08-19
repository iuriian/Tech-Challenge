package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.ClienteService
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.ClienteDto
import br.com.fiap.oficina.presentation.mapper.ClienteMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Operações relacionadas ao gerenciamento de clientes")
class ClienteController(
    private val service: ClienteService,
    private val mapper: ClienteMapper,
) {
    @PostMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar um novo cliente", description = "Cadastra um novo cliente no sistema")
    fun criar(
        @Valid @RequestBody cliente: ClienteDto,
    ): ClienteDto {
        val entity = this.mapper.toEntity(cliente)
        return mapper.toResponse(service.salvarCliente(entity))
    }

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar cliente por ID", description = "Busca um cliente através do seu identificador único")
    fun buscarPorId(
        @Parameter(description = "ID do cliente", required = true)
        @PathVariable id: String,
    ): ClienteDto? = service.buscarPorId(Id.fromString(id))?.let { mapper.toResponse(it) }

    @GetMapping("/nome/{nome}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Buscar cliente por nome", description = "Busca um cliente através do seu nome")
    fun buscarPorNome(
        @Parameter(description = "Nome do cliente", required = true, example = "João Silva")
        @PathVariable nome: String,
    ): ClienteDto? = service.buscarPorNome(nome)?.let { mapper.toResponse(it) }

    @GetMapping("/documento/{documentoNumero}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Buscar cliente por número de documento",
        description = "Busca um cliente através do número do documento de identificação",
    )
    fun buscarPorCpf(
        @Parameter(
            description =
                "Número do documento de identificação. " +
                    "Deve conter apenas números, sem caracteres especiais (pontos, hífens, etc.)",
            required = true,
            example = "12345678900",
        )
        @PathVariable documentoNumero: String,
    ): ClienteDto? = service.buscarPorDocumento(documentoNumero)?.let { mapper.toResponse(it) }

    @PutMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Alterar dados de um cliente", description = "Atualiza as informações de um cliente existente")
    fun alterar(
        @Parameter(description = "ID do cliente a ser alterado", required = true)
        @PathVariable id: String,
        @Valid @RequestBody cliente: ClienteDto,
    ): ClienteDto {
        val entity = this.mapper.toEntity(cliente).copy(id = Id.fromString(id))
        return mapper.toResponse(service.salvarCliente(entity))
    }

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(summary = "Listar clientes", description = "Lista todos os clientes cadastrados no sistema")
    fun listarTodos(): List<ClienteDto> = service.listarTodos().map { mapper.toResponse(it) }

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover um cliente", description = "Exclui um cliente do sistema através do seu ID")
    fun remover(
        @Parameter(description = "ID do cliente a ser removido", required = true)
        @PathVariable id: String,
    ) {
        service.removerCliente(Id.fromString(id))
    }
}
