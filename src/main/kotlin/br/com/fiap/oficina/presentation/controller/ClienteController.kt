package br.com.fiap.oficina.presentation.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/clientes")
class ClienteController {

    @GetMapping
    fun listar(): String {
        return "Lista de clientes"
    }
}