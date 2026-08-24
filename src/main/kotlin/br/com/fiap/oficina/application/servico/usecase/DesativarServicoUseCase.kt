package br.com.fiap.oficina.application.servico.usecase

import br.com.fiap.oficina.application.servico.exception.ServicoNaoEncontradoException
import br.com.fiap.oficina.application.servico.repository.ServicoRepository
import br.com.fiap.oficina.domain.servico.Servico
import br.com.fiap.oficina.domain.valueobject.Id

class DesativarServicoUseCase(private val repository: ServicoRepository) {
    fun executar(id: Id): Servico {
        val servico =
            repository.buscarPorId(id)
                ?: throw ServicoNaoEncontradoException(id)

        return repository.salvar(servico.desativar())
    }
}
