package br.com.fiap.oficina.servico.domain.entities

import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import br.com.fiap.oficina.servico.domain.valueobjects.ItemOrcamento
import br.com.fiap.oficina.servico.domain.valueobjects.NumeroOrdemServico
import br.com.fiap.oficina.servico.domain.valueobjects.Orcamento
import java.time.Duration
import java.time.Instant

data class OrdemServico(
    val id: Id,
    val osNumber: NumeroOrdemServico? = null,
    val descricao: String,
    val status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
    val funcionarioId: Id,
    val clienteId: Id,
    val veiculoId: Id,
    val pecas: List<PecaServico> = emptyList(),
    val prazo: Duration? = null,
    val dataAbertura: Instant = Instant.now(),
    val dataInicioExecucao: Instant? = null,
    val dataFinalizacao: Instant? = null,
) {
    init {
        require(prazo == null || prazo.toMinutes() > 0) {
            "Prazo da ordem de serviço deve ser maior que zero"
        }
    }

    companion object {
        fun criar(
            osNumber: NumeroOrdemServico,
            descricao: String,
            funcionarioId: Id,
            clienteId: Id,
            veiculoId: Id,
            status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
            pecas: List<PecaServico> = emptyList(),
            prazo: Duration? = null,
        ): OrdemServico {
            require(descricao.isNotBlank()) {
                "Descrição do serviço é obrigatória"
            }

            return OrdemServico(
                id = Id.generate(),
                osNumber = osNumber,
                descricao = descricao,
                status = status,
                funcionarioId = funcionarioId,
                clienteId = clienteId,
                veiculoId = veiculoId,
                pecas = pecas,
                prazo = prazo,
                dataAbertura = Instant.now(),
            )
        }
    }

    fun avancarStatus(agora: Instant = Instant.now()): OrdemServico {
        val proximoStatus =
            status.proximoStatus()
                ?: error(
                    "Ordem de serviço no status '$status' é um estado final e não pode ser alterada.",
                )
        return alterarStatus(proximoStatus, agora)
    }

    fun alterarStatus(novoStatus: OrdemServicoStatus, agora: Instant = Instant.now()): OrdemServico {
        val permitidas = status.transicoesPermitidas()

        check(novoStatus in permitidas) {
            "Transição inválida de '$status' para '$novoStatus'. " +
                "Transições permitidas a partir de '$status': $permitidas"
        }
        return when (novoStatus) {
            OrdemServicoStatus.EM_EXECUCAO ->
                copy(
                    status = novoStatus,
                    dataInicioExecucao = agora,
                )

            OrdemServicoStatus.FINALIZADA ->
                copy(
                    status = novoStatus,
                    dataFinalizacao = agora,
                )

            else -> copy(status = novoStatus)
        }
    }

    val orcamento: Orcamento
        get() =
            Orcamento(
                ordemServicoId = id,
                itens =
                pecas.map { item ->
                    ItemOrcamento.dePeca(
                        peca = item.peca,
                        quantidade = item.quantidade,
                    )
                },
            )
}
