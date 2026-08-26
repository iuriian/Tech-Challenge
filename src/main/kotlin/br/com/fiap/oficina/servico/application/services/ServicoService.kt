package br.com.fiap.oficina.servico.application.services

import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.application.dto.ServicoComando
import br.com.fiap.oficina.servico.application.dto.TempoMedioExecucao
import br.com.fiap.oficina.servico.domain.entities.OrdemServico
import br.com.fiap.oficina.servico.domain.entities.PecaServico
import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import br.com.fiap.oficina.servico.domain.repositories.OrdemServicoRepository
import br.com.fiap.oficina.servico.domain.repositories.SequenciaOrdemServicoRepository
import br.com.fiap.oficina.servico.domain.valueobjects.NumeroOrdemServico
import br.com.fiap.oficina.servico.domain.valueobjects.Orcamento
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
class ServicoService(
    private val repository: OrdemServicoRepository,
    private val clienteRepository: ClienteRepository,
    private val veiculoRepository: VeiculoRepository,
    private val pecaRepository: PecaRepository,
    private val funcionarioRepository: FuncionarioRepository,
    private val sequenciaOrdemServicoRepository: SequenciaOrdemServicoRepository,
) {
    @Transactional
    fun salvar(comando: ServicoComando): OrdemServico {
        val cliente =
            clienteRepository.buscarPorId(comando.clienteId)
                ?: throw IllegalArgumentException("Cliente não encontrado com o ID: ${comando.clienteId}")

        val funcionario =
            funcionarioRepository.buscarPorId(comando.funcionarioId)
                ?: throw IllegalArgumentException("Funcionário não encontrado com o ID: ${comando.funcionarioId}")

        val veiculo =
            veiculoRepository.buscarPorId(comando.veiculoId)
                ?: throw IllegalArgumentException("Veículo não encontrado com o ID: ${comando.veiculoId}")

        val pecas =
            comando.pecas.mapNotNull { item ->
                pecaRepository.buscarPorId(item.pecaId)?.let { peca ->
                    PecaServico.criar(peca, item.quantidade)
                }
            }

        val ordemServico =
            comando.id
                ?.let { id -> atualizarExistente(id, comando, pecas) }
                ?: OrdemServico.criar(
                    osNumber = NumeroOrdemServico.criar(
                        sequencial = sequenciaOrdemServicoRepository.obterProximoValor(),
                    ),
                    descricao = comando.descricao,
                    funcionarioId = funcionario.id,
                    clienteId = cliente.id,
                    veiculoId = veiculo.id,
                    status = comando.status,
                    pecas = pecas,
                )

        return repository.salvar(ordemServico)
    }

    private fun atualizarExistente(id: Id, comando: ServicoComando, pecas: List<PecaServico>): OrdemServico {
        val existente =
            repository.buscarPorId(id)
                ?: throw IllegalArgumentException("Serviço não encontrado com o ID: $id")

        return existente.copy(
            descricao = comando.descricao,
            funcionarioId = comando.funcionarioId,
            clienteId = comando.clienteId,
            veiculoId = comando.veiculoId,
            pecas = pecas,
        )
    }

    fun listarPorId(id: Id): OrdemServico? = repository.buscarPorId(id)

    fun listarTodos(): List<OrdemServico> = repository.listarTodos()

    fun listarPorCliente(clienteId: Id): List<OrdemServico> = repository.listarPorCliente(clienteId)

    /**
     * Retorna o orçamento do serviço, totalizando o valor das peças
     * (preço de venda × quantidade). Lança exceção se o serviço não existir.
     */
    fun obterOrcamento(id: Id): Orcamento = buscarObrigatorio(id).orcamento

    @Transactional
    fun deletarPorId(id: Id): String {
        require(repository.existePorId(id)) { "Serviço não encontrado para deletar." }
        repository.deletarPorId(id)

        return "Serviço deletado."
    }

    /**
     * Avança a ordem de serviço para o próximo estado permitido pelo domínio.
     *
     * A definição do fluxo e a validação da transição pertencem à [OrdemServico].
     */
    @Transactional
    fun avancarStatus(id: Id): OrdemServico {
        val ordemServico = buscarObrigatorio(id)

        return repository.salvar(ordemServico.avancarStatus())
    }

    /**
     * Solicita a alteração do status da ordem de serviço.
     *
     * A [OrdemServico] valida se a transição para [novoStatus] é permitida.
     */
    @Transactional
    fun alterarStatus(id: Id, novoStatus: OrdemServicoStatus): OrdemServico {
        val ordemServico = buscarObrigatorio(id)

        return repository.salvar(ordemServico.alterarStatus(novoStatus))
    }

    fun calcularTempoMedioExecucao(): TempoMedioExecucao {
        val finalizados =
            repository
                .listarTodos()
                .filter { it.dataInicioExecucao != null && it.dataFinalizacao != null }

        val tempoMedio =
            finalizados
                .takeIf { it.isNotEmpty() }
                ?.map { Duration.between(it.dataInicioExecucao, it.dataFinalizacao).toMinutes().toDouble() }
                ?.average()

        return TempoMedioExecucao(finalizados.size, tempoMedio)
    }

    private fun buscarObrigatorio(id: Id): OrdemServico = repository.buscarPorId(id)
        ?: throw IllegalArgumentException("Serviço não encontrado com o ID: $id")
}
