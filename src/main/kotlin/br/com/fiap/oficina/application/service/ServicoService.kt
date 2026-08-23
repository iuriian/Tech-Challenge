package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.entity.PecaServico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.repository.OrdemServicoRepository
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.Orcamento
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Duration

data class TempoMedioExecucao(val totalServicosFinalizados: Int, val tempoMedioMinutos: Double?)

data class PecaServicoComando(val pecaId: Id, val quantidade: BigDecimal)

data class ServicoComando(
    val id: Id? = null,
    val descricao: String,
    val funcionarioId: Id,
    val status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
    val clienteId: Id,
    val veiculoId: Id,
    val pecas: List<PecaServicoComando> = emptyList(),
)

@Service
class ServicoService(
    private val repository: OrdemServicoRepository,
    private val clienteRepository: ClienteRepository,
    private val veiculoRepository: VeiculoRepository,
    private val pecaRepository: PecaRepository,
    private val funcionarioRepository: FuncionarioRepository,
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

        val servico =
            comando.id
                ?.let { id -> atualizarExistente(id, comando, funcionario, cliente, veiculo, pecas) }
                ?: OrdemServico.criar(
                    descricao = comando.descricao,
                    funcionario = funcionario,
                    cliente = cliente,
                    veiculo = veiculo,
                    status = comando.status,
                    pecas = pecas,
                )

        return repository.salvar(servico)
    }

    private fun atualizarExistente(
        id: Id,
        comando: ServicoComando,
        funcionario: Funcionario,
        cliente: Cliente,
        veiculo: Veiculo,
        pecas: List<PecaServico>,
    ): OrdemServico {
        val existente =
            repository.buscarPorId(id)
                ?: throw IllegalArgumentException("Serviço não encontrado com o ID: $id")

        return existente.copy(
            descricao = comando.descricao,
            funcionario = funcionario,
            cliente = cliente,
            veiculo = veiculo,
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
    fun obterOrcamento(id: Id): Orcamento = buscarObrigatorio(id).gerarOrcamento()

    @Transactional
    fun deletarPorId(id: Id): String {
        require(repository.existePorId(id)) { "Serviço não encontrado para deletar." }
        repository.deletarPorId(id)

        return "Servico deletado."
    }

    /**
     * Dá andamento à ordem de serviço, movendo-a para o próximo status na
     * ordem de declaração do enum [OrdemServicoStatus]. A partir de
     * [OrdemServicoStatus.AGUARDANDO_APROVACAO] o andamento segue para
     * [OrdemServicoStatus.EM_EXECUCAO]; para cancelar, use [alterarStatus].
     */
    @Transactional
    fun avancarStatus(id: Id): OrdemServico {
        val servico = buscarObrigatorio(id)
        val proximo =
            proximoNaOrdem(servico.status)
                ?: error("Serviço no status '${servico.status}' é um estado final e não pode avançar.")

        return repository.salvar(servico.alterarStatus(proximo))
    }

    /**
     * Transição de status guardada pela máquina de estados. Só permite mudar
     * para um status alcançável a partir do atual (ver [transicoesPermitidas]).
     */
    @Transactional
    fun alterarStatus(
        id: Id,
        novoStatus: OrdemServicoStatus,
    ): OrdemServico {
        val servico = buscarObrigatorio(id)
        val permitidas = transicoesPermitidas(servico.status)

        check(novoStatus in permitidas) {
            "Transição inválida de '${servico.status}' para '$novoStatus'. " +
                "Transições permitidas a partir de '${servico.status}': $permitidas."
        }

        return repository.salvar(servico.alterarStatus(novoStatus))
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

    private fun buscarObrigatorio(id: Id): OrdemServico =
        repository.buscarPorId(id)
            ?: throw IllegalArgumentException("Serviço não encontrado com o ID: $id")

    /**
     * Define a máquina de estados: a partir de cada status, o fluxo segue para
     * o próximo na ordem de declaração do enum. A única ramificação ocorre em
     * [OrdemServicoStatus.AGUARDANDO_APROVACAO], de onde pode ir para
     * [OrdemServicoStatus.EM_EXECUCAO] ou [OrdemServicoStatus.CANCELADA].
     */
    private fun transicoesPermitidas(atual: OrdemServicoStatus): Set<OrdemServicoStatus> =
        when (atual) {
            OrdemServicoStatus.AGUARDANDO_APROVACAO -> {
                setOf(OrdemServicoStatus.EM_EXECUCAO, OrdemServicoStatus.CANCELADA)
            }

            else -> {
                setOfNotNull(proximoNaOrdem(atual))
            }
        }

    /**
     * Próximo status no fluxo linear (ordem de declaração do enum). Retorna
     * null para os estados finais ([OrdemServicoStatus.ENTREGUE] e
     * [OrdemServicoStatus.CANCELADA]); CANCELADA é ignorada por não fazer parte
     * do fluxo linear, sendo alcançável apenas a partir de AGUARDANDO_APROVACAO.
     */
    private fun proximoNaOrdem(atual: OrdemServicoStatus): OrdemServicoStatus? =
        OrdemServicoStatus.entries
            .getOrNull(atual.ordinal + 1)
            ?.takeIf { it != OrdemServicoStatus.CANCELADA }
}