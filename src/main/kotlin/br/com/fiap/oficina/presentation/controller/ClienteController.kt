package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.ClienteService
import br.com.fiap.oficina.presentation.dto.ClienteDto
import br.com.fiap.oficina.presentation.mapper.ClienteMapper
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/clientes")
class ClienteController(
    private val service: ClienteService,
    private val mapper: ClienteMapper
) {

    @PostMapping
    fun criar(@Valid @RequestBody cliente: ClienteDto): ClienteDto {


        val entity = this.mapper.toEntity(cliente)

        return mapper.toResponse(service.salvarCliente(entity))
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ClienteDto? {
        return service.buscarPorId(id)?.let { mapper.toResponse(it) }
    }

    @GetMapping("/nome/{nome}")
    fun buscarPorNome(@PathVariable nome: String): ClienteDto? {
        return service.buscarPorNome(nome)?.let { mapper.toResponse(it) }
    }

    @GetMapping("/cpf/{cpf}")
    fun buscarPorCpf(@PathVariable cpf: String): ClienteDto? {
        return service.buscarPorCpf(cpf)?.let { mapper.toResponse(it) }
    }
}