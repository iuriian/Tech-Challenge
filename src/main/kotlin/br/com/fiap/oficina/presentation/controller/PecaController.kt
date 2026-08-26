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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping("/pecas")
@Tag(name = "Peças", description = "Operações relacionadas ao gerenciamento de peças e estoque")
class PecaController(private val pecaService: PecaService, private val mapper: PecaMapper) {
    @PostMapping
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Criar uma nova peça",
        description = "Cadastra uma nova peça no sistema. Retorna conflito (409) se o código já existir.",
    )
    fun criar(@Valid @RequestBody peca: PecaDto): ResponseEntity<PecaDto> {
        val request = mapper.toCriarRequest(peca)
        val response = mapper.toDto(pecaService.criar(request))
        val location =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/codigo/{codigo}")
                .buildAndExpand(response.codigo)
                .toUri()
        return ResponseEntity.created(location).body(response)
    }

    @PutMapping("/{codigo}")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Atualizar uma peça",
        description = "Atualiza os dados de uma peça existente identificada pelo código",
    )
    fun atualizar(
        @Parameter(description = "Código da peça a ser atualizada", required = true, example = "PEC001")
        @PathVariable codigo: String,
        @Valid @RequestBody peca: PecaAtualizacaoDto,
    ): PecaDto {
        val request = mapper.toAtualizarRequest(peca)
        return mapper.toDto(pecaService.atualizar(codigo, request))
    }

    @PatchMapping("/{codigo}/estoque/retirar")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Retirar peças do estoque",
        description = "Reduz a quantidade em estoque de uma peça pela quantidade informada",
    )
    fun retirarPecas(
        @Parameter(description = "Código da peça", required = true, example = "PEC001")
        @PathVariable codigo: String,
        @Parameter(description = "Quantidade a ser retirada do estoque", required = true, example = "5")
        @RequestParam qtd: Int,
    ): PecaDto = mapper.toDto(pecaService.retirar(codigo, qtd))

    @PatchMapping("/{codigo}/estoque/repor")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Repor peças no estoque",
        description = "Aumenta a quantidade em estoque de uma peça pela quantidade informada",
    )
    fun reporPecas(
        @Parameter(description = "Código da peça", required = true, example = "PEC001")
        @PathVariable codigo: String,
        @Parameter(description = "Quantidade a ser reposta no estoque", required = true, example = "10")
        @RequestParam qtd: Int,
    ): PecaDto = mapper.toDto(pecaService.repor(codigo, qtd))

    @PatchMapping("/{codigo}/reativar")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Reativar uma peça",
        description = "Reativa uma peça previamente desativada",
    )
    fun reativar(
        @Parameter(description = "Código da peça a ser reativada", required = true, example = "PEC001")
        @PathVariable codigo: String,
    ): Boolean = pecaService.reativar(codigo)

    @DeleteMapping("/{codigo}")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Desativar uma peça",
        description = "Desativa (remoção lógica) uma peça identificada pelo código",
    )
    fun deletar(
        @Parameter(description = "Código da peça a ser desativada", required = true, example = "PEC001")
        @PathVariable codigo: String,
    ) {
        pecaService.deletar(codigo)
    }

    @GetMapping
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Listar peças",
        description = "Lista todas as peças cadastradas no sistema",
    )
    fun listar(): List<PecaDto> = pecaService.listar().map { mapper.toDto(it) }

    @GetMapping("/codigo/{codigo}")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Buscar peça por código",
        description = "Busca uma peça pelo seu código único",
    )
    fun buscarPorCodigo(
        @Parameter(description = "Código da peça", required = true, example = "PEC001")
        @PathVariable codigo: String,
    ): PecaDto = mapper.toDto(pecaService.buscarPorCodigo(codigo))

    @GetMapping("/nome/{nome}")
    @RolesAllowed("ATENDENTE", "ADMIN", "MECANICO")
    @Operation(
        summary = "Buscar peça por nome",
        description = "Busca uma peça pelo seu nome",
    )
    fun buscarPorNome(
        @Parameter(description = "Nome da peça", required = true, example = "Filtro de óleo")
        @PathVariable nome: String,
    ): PecaDto = mapper.toDto(pecaService.buscarPorNome(nome))
}
