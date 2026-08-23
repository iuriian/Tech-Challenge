package br.com.fiap.oficina.application.repository.servico

import br.com.fiap.oficina.domain.entity.servico.Servico
import br.com.fiap.oficina.domain.valueobject.Id

interface ServicoRepository {
    fun salvar(servico: Servico): Servico

    fun buscarPorId(id: Id): Servico?

    fun listarAtivos(): List<Servico>

    fun listarTodos(): List<Servico>
}