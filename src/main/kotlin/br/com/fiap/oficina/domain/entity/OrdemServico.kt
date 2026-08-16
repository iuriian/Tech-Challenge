package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Orcamento
import java.math.BigDecimal
import java.time.Instant

data class OrdemServico(
    val id: Id,
    val descricao: String,
    val status: ServicoStatus = ServicoStatus.RECEBIDA,
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
            status: ServicoStatus = ServicoStatus.RECEBIDA,
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

    fun alterarStatus(
        novoStatus: ServicoStatus,
        agora: Instant = Instant.now(),
    ): OrdemServico =
        when (novoStatus) {
            ServicoStatus.EM_EXECUCAO -> copy(status = novoStatus, dataInicioExecucao = agora)
            ServicoStatus.FINALIZADA -> copy(status = novoStatus, dataFinalizacao = agora)
            else -> copy(status = novoStatus)
        }

    /**
     * Gera o orçamento do serviço, discriminando cada peça consumida e
     * totalizando o valor das peças (preço de venda × quantidade).
     */
    fun gerarOrcamento(): Orcamento {
        val itens =
            pecas.map { item ->
                ItemOrcamento(
                    pecaId = item.peca.id,
                    codigo = item.peca.codigo,
                    nome = item.peca.nome,
                    precoUnitario = item.peca.precoDeVenda,
                    quantidade = item.quantidade,
                    subtotal = item.subtotal(),
                )
            }
        val valorTotal = itens.fold(BigDecimal.ZERO) { acc, item -> acc + item.subtotal }

        return Orcamento(servicoId = id, itens = itens, valorTotal = valorTotal)
    }
}
