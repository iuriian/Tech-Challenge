package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.dto.ServicoRequest
import br.com.fiap.oficina.application.dto.ServicoResponse
import br.com.fiap.oficina.application.service.ServicoService
import io.swagger.v3.oas.annotations.Operation
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
import java.util.UUID

@RestController
@RequestMapping("/catalogo/servicos")
@Tag(
    name = "Catálogo de Serviços",
    description = "Operações relacionadas ao catálogo de serviços da oficina",
)
class ServicoController(private val service: ServicoService) {
    @PostMapping
    @RolesAllowed("ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Criar serviço",
        description = "Cadastra um novo serviço no catálogo da oficina",
    )
    fun criar(
        @Valid
        @RequestBody
        request: ServicoRequest,
    ): ServicoResponse = service.criar(request)

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Listar serviços ativos",
        description = "Lista os serviços ativos disponíveis no catálogo",
    )
    fun listarAtivos(): List<ServicoResponse> = service.listarAtivos()

    @GetMapping("/todos")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Listar todos os serviços",
        description = "Lista serviços ativos e inativos para administração do catálogo",
    )
    fun listarTodos(): List<ServicoResponse> = service.listarTodos()

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Buscar serviço",
        description = "Busca um serviço do catálogo pelo identificador",
    )
    fun buscar(@PathVariable id: UUID): ServicoResponse = service.buscar(id)

    @PutMapping("/{id}")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Atualizar serviço",
        description = "Atualiza descrição e valor de um serviço do catálogo",
    )
    fun atualizar(
        @PathVariable id: UUID,
        @Valid
        @RequestBody
        request: ServicoRequest,
    ): ServicoResponse = service.atualizar(id, request)

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Desativar serviço",
        description = "Desativa logicamente um serviço do catálogo",
    )
    fun desativar(@PathVariable id: UUID) {
        service.desativar(id)
    }

    @PatchMapping("/{id}/reativar")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Reativar serviço",
        description = "Reativa um serviço previamente desativado",
    )
    fun reativar(@PathVariable id: UUID): ServicoResponse = service.reativar(id)
}
