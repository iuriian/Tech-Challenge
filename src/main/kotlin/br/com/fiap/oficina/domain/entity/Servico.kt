package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Orcamento
import java.math.BigDecimal

data class Servico(
    val id: Id,
    val descricao: String,
    val status: ServicoStatus = ServicoStatus.RECEBIDA,
    val funcionarioId: Long,
    val cliente: Cliente,
    val veiculo: Veiculo,
    val pecas: List<PecaServico> = emptyList()
) {

    companion object {
        fun criar(
            descricao: String,
            funcionarioId: Long,
            cliente: Cliente,
            veiculo: Veiculo,
            status: ServicoStatus = ServicoStatus.RECEBIDA,
            pecas: List<PecaServico> = emptyList()
        ): Servico {
            require(descricao.isNotBlank()) { "Descrição do serviço é obrigatória" }

            return Servico(
                id = Id.gerar(),
                descricao = descricao,
                status = status,
                funcionarioId = funcionarioId,
                cliente = cliente,
                veiculo = veiculo,
                pecas = pecas
            )
        }
    }

    fun adicionarPeca(pecaServico: PecaServico): Servico = copy(pecas = pecas + pecaServico)

    fun adicionarPeca(peca: Peca, quantidade: BigDecimal): Servico =
        adicionarPeca(PecaServico.criar(peca, quantidade))

    fun alterarStatus(novoStatus: ServicoStatus): Servico = copy(status = novoStatus)

    /**
     * Gera o orçamento do serviço, discriminando cada peça consumida e
     * totalizando o valor das peças (preço de venda × quantidade).
     */
    fun gerarOrcamento(): Orcamento {
        val itens = pecas.map { item ->
            ItemOrcamento(
                pecaId = item.peca.id,
                codigo = item.peca.codigo,
                nome = item.peca.nome,
                precoUnitario = item.peca.precoDeVenda,
                quantidade = item.quantidade,
                subtotal = item.subtotal()
            )
        }
        val valorTotal = itens.fold(BigDecimal.ZERO) { acc, item -> acc + item.subtotal }

        return Orcamento(servicoId = id, itens = itens, valorTotal = valorTotal)
    }
}
