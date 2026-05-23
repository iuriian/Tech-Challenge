package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.PecaService
import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import br.com.fiap.oficina.presentation.dto.PecaDto
import br.com.fiap.oficina.presentation.mapper.PecaMapper
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/pecas")
class PecaController(
    private val service: PecaService,
    private val mapper: PecaMapper
) {

    @PostMapping
    fun criar(@Valid @RequestBody peca: PecaDto): PecaDto {
        val entity = mapper.toEntity(peca)

        return try {
            mapper.toDto(service.salvarPeca(entity))
        } catch (exception: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.CONFLICT, exception.message, exception)
        }
    }

    @PutMapping("/{codigo}")
    fun atualizar(
        @PathVariable codigo: String,
        @Valid @RequestBody peca: PecaAtualizacaoDto
    ): PecaDto? {
        val entity = mapper.toEntity(peca)

        return service.atualizarPeca(codigo, entity)?.let { mapper.toDto(it) }
    }

    @PatchMapping("/{codigo}/estoque/retirar")
    fun retirarPecas(
        @PathVariable codigo: String,
        @RequestParam qtd: Int
    ): PecaDto? {
        return try {
            service.retirarPecas(codigo, qtd)?.let { mapper.toDto(it) }
        } catch (exception: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, exception.message, exception)
        }
    }

    @PatchMapping("/{codigo}/estoque/repor")
    fun reporPecas(
        @PathVariable codigo: String,
        @RequestParam qtd: Int
    ): PecaDto? {
        return try {
            service.reporPecas(codigo, qtd)?.let { mapper.toDto(it) }
        } catch (exception: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, exception.message, exception)
        }
    }

    @PatchMapping("/{codigo}/reativar")
    fun reativar(@PathVariable codigo: String): Boolean {
        return service.reativarPeca(codigo)
    }

    @DeleteMapping("/{codigo}")
    fun deletar(@PathVariable codigo: String): Boolean {
        return service.deletarPeca(codigo)
    }

    @GetMapping
    fun listar(): List<PecaDto> {
        return service.listarPecas().map { mapper.toDto(it) }
    }

    @GetMapping("/codigo/{codigo}")
    fun buscarPorCodigo(@PathVariable codigo: String): PecaDto? {
        return service.buscarPorCodigo(codigo)?.let { mapper.toDto(it) }
    }

    @GetMapping("/nome/{nome}")
    fun buscarPorNome(@PathVariable nome: String): PecaDto? {
        return service.buscarPorNome(nome)?.let { mapper.toDto(it) }
    }

}
