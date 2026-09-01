package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.valueobject.Id

interface ServicoRepository {
    fun salvar(servico: Servico): Servico

    fun buscarPorId(id: Id): Servico?

    fun listarAtivos(): List<Servico>

    fun listarTodos(): List<Servico>
}
