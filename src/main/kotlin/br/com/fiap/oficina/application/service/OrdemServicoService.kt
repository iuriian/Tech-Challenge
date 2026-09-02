package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.application.command.ItemOrcamentoComando
import br.com.fiap.oficina.application.command.OrdemServicoComando
import br.com.fiap.oficina.application.dto.OrcamentoResponse
import br.com.fiap.oficina.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.application.dto.TempoMedioExecucaoResponse
import br.com.fiap.oficina.application.mapper.OrcamentoMapper
import br.com.fiap.oficina.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.application.mapper.TempoMedioExecucaoMapper
import br.com.fiap.oficina.application.result.TempoMedioExecucaoResult
import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.repository.OrdemServicoRepository
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.repository.SequenciaOrdemServicoRepository
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.NumeroOrdemServico
import br.com.fiap.oficina.domain.valueobject.Orcamento
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
class OrdemServicoService(
    private val repository: OrdemServicoRepository,
    private val clienteRepository: ClienteRepository,
    private val veiculoRepository: VeiculoRepository,
    private val pecaRepository: PecaRepository,
    private val servicoRepository: ServicoRepository,
    private val funcionarioRepository: FuncionarioRepository,
    private val sequenciaOrdemServicoRepository: SequenciaOrdemServicoRepository,
    private val ordemServicoMapper: OrdemServicoMapper,
    private val orcamentoMapper: OrcamentoMapper,
    private val tempoMedioExecucaoMapper: TempoMedioExecucaoMapper,
) {
    @Transactional
    fun criar(request: OrdemServicoRequest): OrdemServicoResponse = ordemServicoMapper.toResponse(
        salvar(
            ordemServicoMapper.toCommand(request),
        ),
    )

    @Transactional
    fun atualizar(id: String, request: OrdemServicoRequest): OrdemServicoResponse = ordemServicoMapper.toResponse(
        salvar(
            ordemServicoMapper.toCommand(
                request = request,
                id = Id.fromString(id),
            ),
        ),
    )

    private fun salvar(comando: OrdemServicoComando): OrdemServico {
        val cliente =
            clienteRepository.buscarPorId(comando.clienteId)
                ?: throw IllegalArgumentException(
                    "Cliente não encontrado com o ID: ${comando.clienteId}",
                )

        val funcionario =
            funcionarioRepository.buscarPorId(comando.funcionarioId)
                ?: throw IllegalArgumentException(
                    "Funcionário não encontrado com o ID: ${comando.funcionarioId}",
                )

        val veiculo =
            veiculoRepository.buscarPorId(comando.veiculoId)
                ?: throw IllegalArgumentException(
                    "Veículo não encontrado com o ID: ${comando.veiculoId}",
                )

        val orcamento = montarOrcamento(comando.itens)

        val ordemServico =
            comando.id
                ?.let { id ->
                    atualizarExistente(
                        id = id,
                        comando = comando,
                        orcamento = orcamento,
                    )
                }
                ?: OrdemServico.criar(
                    osNumber =
                    NumeroOrdemServico.criar(
                        sequencial = sequenciaOrdemServicoRepository.obterProximoValor(),
                    ),
                    descricao = comando.descricao,
                    funcionarioId = funcionario.id,
                    clienteId = cliente.id,
                    veiculoId = veiculo.id,
                    status = comando.status,
                    orcamento = orcamento,
                )

        return repository.salvar(ordemServico)
    }

    private fun montarOrcamento(itens: List<ItemOrcamentoComando>): Orcamento = Orcamento(
        itens = itens.map(::resolverItemOrcamento),
    )

    private fun resolverItemOrcamento(item: ItemOrcamentoComando): ItemOrcamento = when (item.tipo) {
        TipoItemOrcamento.PECA -> {
            val peca =
                pecaRepository.buscarPorId(item.referenciaId)
                    ?: throw IllegalArgumentException(
                        "Peça não encontrada com o ID: ${item.referenciaId}",
                    )

            ItemOrcamento.dePeca(
                peca = peca,
                quantidade = item.quantidade,
            )
        }

        TipoItemOrcamento.SERVICO -> {
            val servico =
                servicoRepository.buscarPorId(item.referenciaId)
                    ?: throw IllegalArgumentException(
                        "Serviço do catálogo não encontrado com o ID: ${item.referenciaId}",
                    )

            require(servico.ativo) {
                "Serviço do catálogo está inativo: ${item.referenciaId}"
            }

            ItemOrcamento.deServico(
                servico = servico,
                quantidade = item.quantidade,
            )
        }
    }

    private fun atualizarExistente(id: Id, comando: OrdemServicoComando, orcamento: Orcamento): OrdemServico {
        val existente =
            repository.buscarPorId(id)
                ?: throw IllegalArgumentException(
                    "Ordem de serviço não encontrada com o ID: $id",
                )

        return existente.copy(
            descricao = comando.descricao,
            funcionarioId = comando.funcionarioId,
            clienteId = comando.clienteId,
            veiculoId = comando.veiculoId,
            orcamento = orcamento,
        )
    }

    fun listarPorId(id: String): OrdemServicoResponse? = repository
        .buscarPorId(Id.fromString(id))
        ?.let(ordemServicoMapper::toResponse)

    fun listarTodos(): List<OrdemServicoResponse> = repository
        .listarTodos()
        .map(ordemServicoMapper::toResponse)

    fun listarPorCliente(clienteId: String): List<OrdemServicoResponse> = repository
        .listarPorCliente(Id.fromString(clienteId))
        .map(ordemServicoMapper::toResponse)

    fun obterOrcamento(id: String): OrcamentoResponse = orcamentoMapper.toResponse(
        buscarObrigatorio(
            Id.fromString(id),
        ).orcamento,
    )

    @Transactional
    fun deletarPorId(id: String) {
        val ordemServicoId = Id.fromString(id)

        require(repository.existePorId(ordemServicoId)) {
            "Ordem de serviço não encontrada para deletar."
        }

        repository.deletarPorId(ordemServicoId)
    }

    @Transactional
    fun avancarStatus(id: String): OrdemServicoResponse {
        val ordemServico =
            buscarObrigatorio(
                Id.fromString(id),
            )

        return ordemServicoMapper.toResponse(
            repository.salvar(
                ordemServico.avancarStatus(),
            ),
        )
    }

    @Transactional
    fun alterarStatus(id: String, novoStatus: OrdemServicoStatus): OrdemServicoResponse {
        val ordemServico =
            buscarObrigatorio(
                Id.fromString(id),
            )

        return ordemServicoMapper.toResponse(
            repository.salvar(
                ordemServico.alterarStatus(novoStatus),
            ),
        )
    }

    fun calcularTempoMedioExecucao(): TempoMedioExecucaoResponse {
        val finalizados =
            repository
                .listarTodos()
                .filter {
                    it.dataInicioExecucao != null &&
                        it.dataFinalizacao != null
                }

        val tempoMedio =
            finalizados
                .takeIf { it.isNotEmpty() }
                ?.map {
                    Duration
                        .between(
                            it.dataInicioExecucao,
                            it.dataFinalizacao,
                        ).toMinutes()
                        .toDouble()
                }
                ?.average()

        return tempoMedioExecucaoMapper.toResponse(
            TempoMedioExecucaoResult(
                totalOrdensFinalizadas = finalizados.size,
                tempoMedioMinutos = tempoMedio,
            ),
        )
    }

    private fun buscarObrigatorio(id: Id): OrdemServico = repository.buscarPorId(id)
        ?: throw IllegalArgumentException(
            "Ordem de serviço não encontrada com o ID: $id",
        )
}
