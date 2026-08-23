package br.com.fiap.oficina.application.usecase.servico

import br.com.fiap.oficina.application.repository.servico.ServicoRepository
import br.com.fiap.oficina.domain.entity.servico.Servico

class ListarTodosServicosUseCase(
    private val repository: ServicoRepository,
) {
    fun executar(): List<Servico> =
        repository.listarTodos()
}