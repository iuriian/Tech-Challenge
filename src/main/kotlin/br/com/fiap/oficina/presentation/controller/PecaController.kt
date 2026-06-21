package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.PecaService
import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import br.com.fiap.oficina.presentation.dto.PecaDto
import br.com.fiap.oficina.presentation.mapper.PecaMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/pecas")
@Tag(name = "Peças", description = "Operações relacionadas ao gerenciamento de peças e estoque")
class PecaController(
    private val service: PecaService,
    private val mapper: PecaMapper
) {

    @PostMapping
    @RolesAllowed("ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Criar uma nova peça",
        description = "Cadastra uma nova peça no sistema. Retorna conflito (409) se o código já existir."
    )
    fun criar(@Valid @RequestBody peca: PecaDto): PecaDto {
        val entity = mapper.toEntity(peca)

        return try {
            mapper.toDto(service.salvarPeca(entity))
        } catch (exception: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.CONFLICT, exception.message, exception)
        }
    }

    @PutMapping("/{codigo}")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Atualizar uma peça",
        description = "Atualiza os dados de uma peça existente identificada pelo código"
    )
    fun atualizar(
        @Parameter(description = "Código da peça a ser atualizada", required = true, example = "PEC001")
        @PathVariable codigo: String,
        @Valid @RequestBody peca: PecaAtualizacaoDto
    ): PecaDto? {
        val entity = mapper.toEntity(peca)

        return service.atualizarPeca(codigo, entity).let { mapper.toDto(it) }
    }

    @PatchMapping("/{codigo}/estoque/retirar")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Retirar peças do estoque",
        description = "Reduz a quantidade em estoque de uma peça pela quantidade informada"
    )
    fun retirarPecas(
        @Parameter(description = "Código da peça", required = true, example = "PEC001")
        @PathVariable codigo: String,
        @Parameter(description = "Quantidade a ser retirada do estoque", required = true, example = "5")
        @RequestParam qtd: Int
    ): PecaDto? {
        return try {
            service.retirarPecas(codigo, qtd)?.let { mapper.toDto(it) }
        } catch (exception: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, exception.message, exception)
        }
    }

    @PatchMapping("/{codigo}/estoque/repor")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Repor peças no estoque",
        description = "Aumenta a quantidade em estoque de uma peça pela quantidade informada"
    )
    fun reporPecas(
        @Parameter(description = "Código da peça", required = true, example = "PEC001")
        @PathVariable codigo: String,
        @Parameter(description = "Quantidade a ser reposta no estoque", required = true, example = "10")
        @RequestParam qtd: Int
    ): PecaDto? {
        return try {
            service.reporPecas(codigo, qtd)?.let { mapper.toDto(it) }
        } catch (exception: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, exception.message, exception)
        }
    }

    @PatchMapping("/{codigo}/reativar")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Reativar uma peça",
        description = "Reativa uma peça previamente desativada"
    )
    fun reativar(
        @Parameter(description = "Código da peça a ser reativada", required = true, example = "PEC001")
        @PathVariable codigo: String
    ): Boolean {
        return service.reativarPeca(codigo)
    }

    @DeleteMapping("/{codigo}")
    @RolesAllowed("ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Desativar uma peça",
        description = "Desativa (remoção lógica) uma peça identificada pelo código"
    )
    fun deletar(
        @Parameter(description = "Código da peça a ser desativada", required = true, example = "PEC001")
        @PathVariable codigo: String
    ): Boolean {
        return service.deletarPeca(codigo)
    }

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Listar peças",
        description = "Lista todas as peças cadastradas no sistema"
    )
    fun listar(): List<PecaDto> {
        return service.listarPecas().map { mapper.toDto(it) }
    }

    @GetMapping("/codigo/{codigo}")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Buscar peça por código",
        description = "Busca uma peça pelo seu código único"
    )
    fun buscarPorCodigo(
        @Parameter(description = "Código da peça", required = true, example = "PEC001")
        @PathVariable codigo: String
    ): PecaDto? {
        return service.buscarPorCodigo(codigo).let { mapper.toDto(it) }
    }

    @GetMapping("/nome/{nome}")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Buscar peça por nome",
        description = "Busca uma peça pelo seu nome"
    )
    fun buscarPorNome(
        @Parameter(description = "Nome da peça", required = true, example = "Filtro de óleo")
        @PathVariable nome: String
    ): PecaDto? {
        return service.buscarPorNome(nome)?.let { mapper.toDto(it) }
    }

}
