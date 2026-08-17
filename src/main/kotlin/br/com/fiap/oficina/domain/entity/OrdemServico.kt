package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Orcamento
import java.math.BigDecimal
import java.time.Instant

data class OrdemServico(
    val id: Id,
    val descricao: String,
    val status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
    val funcionario: Funcionario,
    val cliente: Cliente,
    val veiculo: Veiculo,
    val pecas: List<PecaServico> = emptyList(),
    val dataAbertura: Instant = Instant.now(),
    val dataInicioExecucao: Instant? = null,
    val dataFinalizacao: Instant? = null,
) {
    companion object {
        fun criar(
            descricao: String,
            funcionario: Funcionario,
            cliente: Cliente,
            veiculo: Veiculo,
            status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
            pecas: List<PecaServico> = emptyList(),
        ): OrdemServico {
            require(descricao.isNotBlank()) { "Descrição do serviço é obrigatória" }

            return OrdemServico(
                id = Id.generate(),
                descricao = descricao,
                status = status,
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                pecas = pecas,
                dataAbertura = Instant.now(),
            )
        }
    }

    fun adicionarPeca(pecaServico: PecaServico): OrdemServico = copy(pecas = pecas + pecaServico)

    fun adicionarPeca(
        peca: Peca,
        quantidade: BigDecimal,
    ): OrdemServico = adicionarPeca(PecaServico.criar(peca, quantidade))


    /**
     * Avança a ordem de serviço pelo fluxo principal.
     *
     * O cancelamento não faz parte do avanço automático e
     * deve ser realizado explicitamente por
     * alterarStatus(CANCELADA).
     */
    fun avancarStatus(
        agora: Instant = Instant.now(),
    ): OrdemServico {
        val proximoStatus =
            status.proximoStatus() ?: error(
                "Ordem de serviço no status `$status` é " +
                        "um estado final e não pode ser alterada."
            )

        return alterarStatus(proximoStatus, agora)
    }

    /**
     * Altera o status garantindo que
     * a transição seja permitida pelo domínio.
     */
    fun alterarStatus(
        novoStatus: OrdemServicoStatus,
        agora: Instant = Instant.now(),
    ): OrdemServico {
        val permitidas = status.transicoesPermitidas()

        check(novoStatus in permitidas) {
            "Transição inválida de '$status' para '$novoStatus'. " +
                    "Transições permitidas a partir de '$status': $permitidas."
        }

        return when (novoStatus) {
            OrdemServicoStatus.EM_EXECUCAO -> copy(
                status = novoStatus,
                dataInicioExecucao = agora,
            )

            OrdemServicoStatus.FINALIZADA -> copy(
                status = novoStatus,
                dataFinalizacao = agora,
            )

            else -> copy(status = novoStatus)
        }
    }

    /**
     * Gera o orçamento da ordem de serviço
     * a partir das pesças consumidas.
     *
     * Os serviços de catálogo serão incorporados
     * quando a ordem de serviço passar a manter a
     * sua própria lista de serviços solicitados.
     */
    fun gerarOrcamento(): Orcamento =
        Orcamento(
            ordemServicoId = id,
            itens =
                pecas.map { pecaServico ->
                    ItemOrcamento.dePeca(
                        pecaServico.peca,
                        pecaServico.quantidade
                    )
                },
        )
}
