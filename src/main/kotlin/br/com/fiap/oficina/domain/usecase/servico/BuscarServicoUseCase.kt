package br.com.fiap.oficina.domain.usecase.servico

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.exception.ServicoNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.valueobject.Id

class BuscarServicoUseCase(private val repository: ServicoRepository) {
    fun executar(id: Id): Servico = repository.buscarPorId(id)
        ?: throw ServicoNaoEncontradoException(id)
}
