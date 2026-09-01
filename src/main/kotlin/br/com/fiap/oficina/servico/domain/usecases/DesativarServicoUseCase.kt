package br.com.fiap.oficina.servico.domain.usecases

import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.entities.Servico
import br.com.fiap.oficina.servico.domain.exceptions.ServicoNaoEncontradoException
import br.com.fiap.oficina.servico.domain.repositories.ServicoRepository
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
