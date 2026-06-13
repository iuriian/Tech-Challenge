package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.ServicoComando
import br.com.fiap.oficina.application.service.ServicoService
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.ServicoDto
import br.com.fiap.oficina.presentation.mapper.ServicoMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

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
        description = "Cadastra um novo servico no sistema"
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun criar(
        @Valid
        @RequestBody
        dto: ServicoDto
    ): ServicoDto {
        val saved = service.salvar(toComando(dto, id = null))

        return mapper.toResponse(saved)
    }

    @PutMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Atualizar um servico",
        description = "Atualiza um servico no sistema"
    )
    fun atualizar(
        @Parameter(description = "ID do serviço a ser atualizado", required = true)
        @PathVariable id: UUID,
        @Valid
        @RequestBody
        dto: ServicoDto
    ): ServicoDto {
        val saved = service.salvar(toComando(dto, id = Id.from(id)))

        return mapper.toResponse(saved)
    }

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Listar servico por ID",
        description = "Lista um servico do sistema pelo ID"
    )
    fun listarPorId(
        @Parameter(description = "ID do serviço", required = true)
        @PathVariable id: UUID
    ): ServicoDto? {
        return service.listarPorId(Id.from(id))?.let {
            mapper.toResponse(it)
        }
    }

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Listar servicos",
        description = "Lista todos os servicos"
    )
    fun listarTodos(): List<ServicoDto> {
        return service.listarTodos().map {
            mapper.toResponse(it)
        }
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN")
    @Operation(
        summary = "Deletar servico por ID",
        description = "Deleta um servico do sistema pelo ID"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletarPorId(
        @Parameter(description = "ID do servico a ser removido", required = true)
        @PathVariable id: UUID
    ) {
        service.deletarPorId(Id.from(id))
    }

    private fun toComando(dto: ServicoDto, id: Id?): ServicoComando =
        ServicoComando(
            id = id,
            descricao = dto.descricao,
            funcionarioId = dto.funcionarioId,
            status = dto.status ?: ServicoStatus.RECEBIDA,
            clienteId = Id.from(dto.clienteId),
            veiculoId = Id.from(dto.veiculoId),
            pecasIds = dto.pecasIds.map { Id.from(it) }
        )

}
