package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.Servico

interface ServicoRepository {

    fun salvar(servico: Servico): Servico

    fun buscarPorId(id: Long): Servico?

    fun listarTodos(): List<Servico>

    fun existePorId(id: Long): Boolean

    fun deletarPorId(id: Long)
}
