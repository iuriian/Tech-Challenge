package br.com.fiap.oficina.domain.usecase.servico

import br.com.fiap.oficina.servico.domain.entities.Servico
import br.com.fiap.oficina.servico.domain.repositories.ServicoRepository
import org.springframework.stereotype.Service

@Service
class ListarTodosServicosUseCase(private val repository: ServicoRepository) {
    fun executar(): List<Servico> = repository.listarTodos()
}
