package br.com.fiap.oficina.domain.usecase.servico

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.repository.ServicoRepository
import org.springframework.stereotype.Service

@Service
class ListarServicosAtivosUseCase(private val repository: ServicoRepository) {
    fun executar(): List<Servico> = repository.listarAtivos()
}
