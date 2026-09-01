package br.com.fiap.oficina.domain.usecase.servico

import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.entities.Servico
import br.com.fiap.oficina.servico.domain.exceptions.ServicoNaoEncontradoException
import br.com.fiap.oficina.servico.domain.repositories.ServicoRepository
import org.springframework.stereotype.Service

@Service
class BuscarServicoUseCase(private val repository: ServicoRepository) {
    fun executar(id: Id): Servico = repository.buscarPorId(id)
        ?: throw ServicoNaoEncontradoException(id)
}
