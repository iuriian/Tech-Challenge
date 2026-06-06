package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.repository.PecaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ServicoService(
    private val repository: ServicoRepository,
    private val clienteRepository: ClienteRepository,
    private val veiculoRepository: VeiculoRepository,
    private val pecaRepository: PecaRepository
) {

    @Transactional
    fun salvar(servico: Servico, clienteId: Long, veiculoId: Long, pecasIds: List<Long>): Servico {
        val cliente = clienteRepository.buscarPorId(clienteId)
            ?: throw IllegalArgumentException("Cliente não encontrado com o ID: $clienteId")
        servico.cliente = cliente

        val veiculo = veiculoRepository.buscarPorId(veiculoId)
            ?: throw IllegalArgumentException("Veículo não encontrado com o ID: $veiculoId")
        servico.veiculo = veiculo

        if (pecasIds.isNotEmpty()) {
            val pecas = pecasIds.mapNotNull { pecaRepository.buscarPorId(it) }
            servico.pecas = pecas
        } else {
            servico.pecas = emptyList()
        }

        return repository.salvar(servico)
    }

    fun listarPorId(id: Long): Servico? {
        return repository.buscarPorId(id)
    }

    fun listarTodos(): List<Servico> {
        return repository.listarTodos()
    }

    @Transactional
    fun deletarPorId(id: Long): String? {
        require(repository.existePorId(id)) { "Serviço não encontrado para deletar." }
        repository.deletarPorId(id)

        return "Servico deletado."
    }

}
