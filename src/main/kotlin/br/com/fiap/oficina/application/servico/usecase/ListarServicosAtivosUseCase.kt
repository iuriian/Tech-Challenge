package br.com.fiap.oficina.application.servico.usecase

import br.com.fiap.oficina.application.servico.repository.ServicoRepository
import br.com.fiap.oficina.domain.servico.Servico

class ListarServicosAtivosUseCase(
    private val repository: ServicoRepository,
) {
    fun executar(): List<Servico> =
        repository.listarAtivos()
}