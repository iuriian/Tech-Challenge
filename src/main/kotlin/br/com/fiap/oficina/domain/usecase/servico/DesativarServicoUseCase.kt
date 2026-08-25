package br.com.fiap.oficina.domain.usecase.servico

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.exception.ServicoNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class DesativarServicoUseCase(private val repository: ServicoRepository) {
    fun executar(id: Id): Servico {
        val servico =
            repository.buscarPorId(id)
                ?: throw ServicoNaoEncontradoException(id)

        return repository.salvar(servico.desativar())
    }
}
