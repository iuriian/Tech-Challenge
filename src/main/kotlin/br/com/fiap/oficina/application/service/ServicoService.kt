package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class ServicoComando(
    val id: Id? = null,
    val descricao: String,
    val funcionarioId: Long,
    val status: ServicoStatus = ServicoStatus.RECEBIDA,
    val clienteId: Id,
    val veiculoId: Id,
    val pecasIds: List<Id> = emptyList()
)

@Service
class ServicoService(
    private val repository: ServicoRepository,
    private val clienteRepository: ClienteRepository,
    private val veiculoRepository: VeiculoRepository,
    private val pecaRepository: PecaRepository
) {

    @Transactional
    fun salvar(comando: ServicoComando): Servico {
        val cliente = clienteRepository.buscarPorId(comando.clienteId)
            ?: throw IllegalArgumentException("Cliente não encontrado com o ID: ${comando.clienteId}")

        val veiculo = veiculoRepository.buscarPorId(comando.veiculoId)
            ?: throw IllegalArgumentException("Veículo não encontrado com o ID: ${comando.veiculoId}")

        val pecas = comando.pecasIds.mapNotNull { pecaRepository.buscarPorId(it) }

        val servico = comando.id?.let { id ->
            Servico(
                id = id,
                descricao = comando.descricao,
                status = comando.status,
                funcionarioId = comando.funcionarioId,
                cliente = cliente,
                veiculo = veiculo,
                pecas = pecas
            )
        } ?: Servico.criar(
            descricao = comando.descricao,
            funcionarioId = comando.funcionarioId,
            cliente = cliente,
            veiculo = veiculo,
            status = comando.status,
            pecas = pecas
        )

        return repository.salvar(servico)
    }

    fun listarPorId(id: Id): Servico? = repository.buscarPorId(id)

    fun listarTodos(): List<Servico> = repository.listarTodos()

    @Transactional
    fun deletarPorId(id: Id): String {
        require(repository.existePorId(id)) { "Serviço não encontrado para deletar." }
        repository.deletarPorId(id)

        return "Servico deletado."
    }

}
