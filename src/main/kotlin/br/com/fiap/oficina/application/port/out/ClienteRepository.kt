package br.com.fiap.oficina.application.port.out

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.valueobject.Id

interface ClienteRepository {
    fun salvar(cliente: Cliente): Cliente

    fun buscarPorId(id: Id): Cliente?

    fun buscarPorDocumento(numeroDocumento: String): Cliente?

    fun buscarPorNome(nome: String): Cliente?

    fun listarTodos(): List<Cliente>

    fun remover(id: Id)
}