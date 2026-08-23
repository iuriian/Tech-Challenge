package br.com.fiap.oficina.application.usecase.servico

import br.com.fiap.oficina.application.exception.servico.ServicoNaoEncontradoException
import br.com.fiap.oficina.application.repository.servico.ServicoRepository
import br.com.fiap.oficina.domain.entity.servico.Servico
import br.com.fiap.oficina.domain.valueobject.Id

class DesativarServicoUseCase(
    private val repository: ServicoRepository,
) {
    fun executar(id: Id): Servico {
        val servico =
            repository.buscarPorId(id)
                ?: throw ServicoNaoEncontradoException(id)

        return repository.salvar(servico.desativar())
    }
}