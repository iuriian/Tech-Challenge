package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.valueobject.Id

interface ServicoRepository {

    fun salvar(ordemServico: OrdemServico): OrdemServico

    fun buscarPorId(id: Id): OrdemServico?

    fun listarTodos(): List<OrdemServico>

    fun listarPorCliente(clienteId: Id): List<OrdemServico>

    fun existePorId(id: Id): Boolean

    fun deletarPorId(id: Id)
}
