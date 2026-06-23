package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.valueobject.Id

interface ServicoRepository {

    fun salvar(servico: Servico): Servico

    fun buscarPorId(id: Id): Servico?

    fun listarTodos(): List<Servico>

    fun listarPorCliente(clienteId: Id): List<Servico>

    fun existePorId(id: Id): Boolean

    fun deletarPorId(id: Id)
}
