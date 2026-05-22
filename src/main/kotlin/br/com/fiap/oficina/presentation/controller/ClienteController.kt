package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.ClienteService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.presentation.dto.ClienteResponse
import br.com.fiap.oficina.presentation.mapper.ClienteMapper
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/clientes")
class ClienteController(
    private val service: ClienteService,
    private val mapper: ClienteMapper
) {

    @PostMapping
    fun criar(@RequestBody cliente: Cliente): ClienteResponse {
        return mapper.toResponse(service.salvarCliente(cliente))
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: UUID): ClienteResponse? {
        return service.buscarPorId(id)?.let { mapper.toResponse(it) }
    }

    @GetMapping("/cpf/{cpf}")
    fun buscarPorCpf(@PathVariable cpf: String): ClienteResponse? {
        return service.buscarPorCpf(cpf)?.let { mapper.toResponse(it) }
    }
}