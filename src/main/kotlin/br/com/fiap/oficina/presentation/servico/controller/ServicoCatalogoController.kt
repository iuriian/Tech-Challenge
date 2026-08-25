package br.com.fiap.oficina.presentation.servico.controller

import br.com.fiap.oficina.domain.usecase.servico.AtualizarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.BuscarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.CriarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.DesativarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.ListarServicosAtivosUseCase
import br.com.fiap.oficina.domain.usecase.servico.ListarTodosServicosUseCase
import br.com.fiap.oficina.domain.usecase.servico.ReativarServicoUseCase
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.servico.mapper.ServicoPresentationMapper
import br.com.fiap.oficina.presentation.servico.request.AtualizarServicoRequest
import br.com.fiap.oficina.presentation.servico.request.CriarServicoRequest
import br.com.fiap.oficina.presentation.servico.response.ServicoResponse
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
class ServicoCatalogoController(
    private val criarServicoUseCase: CriarServicoUseCase,
    private val buscarServicoUseCase: BuscarServicoUseCase,
    private val atualizarServicoUseCase: AtualizarServicoUseCase,
    private val listarServicosAtivosUseCase: ListarServicosAtivosUseCase,
    private val listarTodosServicosUseCase: ListarTodosServicosUseCase,
    private val desativarServicoUseCase: DesativarServicoUseCase,
    private val reativarServicoUseCase: ReativarServicoUseCase,
    private val mapper: ServicoPresentationMapper,
) {
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
        request: CriarServicoRequest,
    ): ServicoResponse = mapper.toResponse(
        criarServicoUseCase.executar(
            mapper.fromRequest(request),
        ),
    )

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Listar serviços ativos",
        description = "Lista os serviços ativos disponíveis no catálogo",
    )
    fun listarAtivos(): List<ServicoResponse> = listarServicosAtivosUseCase
        .executar()
        .map(mapper::toResponse)

    @GetMapping("/todos")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Listar todos os serviços",
        description = "Lista serviços ativos e inativos para administração do catálogo",
    )
    fun listarTodos(): List<ServicoResponse> = listarTodosServicosUseCase
        .executar()
        .map(mapper::toResponse)

    @GetMapping("/{id}")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Buscar serviço",
        description = "Busca um serviço do catálogo pelo identificador",
    )
    fun buscar(@PathVariable id: UUID): ServicoResponse = mapper.toResponse(
        buscarServicoUseCase.executar(
            Id(id),
        ),
    )

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
        request: AtualizarServicoRequest,
    ): ServicoResponse = mapper.toResponse(
        atualizarServicoUseCase.executar(
            id = Id(id),
            input = mapper.fromRequest(request),
        ),
    )

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Desativar serviço",
        description = "Desativa logicamente um serviço do catálogo",
    )
    fun desativar(@PathVariable id: UUID) {
        desativarServicoUseCase.executar(Id(id))
    }

    @PatchMapping("/{id}/reativar")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Reativar serviço",
        description = "Reativa um serviço previamente desativado",
    )
    fun reativar(@PathVariable id: UUID): ServicoResponse = mapper.toResponse(
        reativarServicoUseCase.executar(
            Id(id),
        ),
    )
}
