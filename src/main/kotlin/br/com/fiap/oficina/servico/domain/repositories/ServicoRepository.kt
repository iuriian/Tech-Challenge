package br.com.fiap.oficina.servico.domain.repositories

import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.entities.Servico

interface ServicoRepository {
    fun salvar(servico: Servico): Servico

    fun buscarPorId(id: Id): Servico?

    fun listarAtivos(): List<Servico>

    fun listarTodos(): List<Servico>
}
