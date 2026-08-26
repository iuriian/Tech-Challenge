package br.com.fiap.oficina.servico.domain.usecases

import br.com.fiap.oficina.servico.domain.entities.Servico
import br.com.fiap.oficina.servico.domain.repositories.ServicoRepository
import org.springframework.stereotype.Service

@Service
class ListarServicosAtivosUseCase(private val repository: ServicoRepository) {
    fun executar(): List<Servico> = repository.listarAtivos()
}
