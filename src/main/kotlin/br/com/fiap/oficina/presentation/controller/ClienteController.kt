package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.presentation.dto.ClienteResponse
import br.com.fiap.oficina.presentation.mapper.ClienteMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/clientes")
class ClienteController(private val mapper: ClienteMapper) {


    @GetMapping
    fun listar(): ClienteResponse {
        val cliente = Cliente().apply { this.nome = "João Silva"; this.cpf = "123.456.789-00" }
        return mapper.toResponse(cliente)
    }
}