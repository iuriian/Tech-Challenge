package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.Cliente

interface ClienteRepository {

    fun salvar(cliente: Cliente): Cliente

    fun buscarPorId(id: Long): Cliente?

    fun buscarPorDocumento(numeroDocumento: String): Cliente?

    fun buscarPorNome(nome: String): Cliente?

    fun remover(id: Long)
}
