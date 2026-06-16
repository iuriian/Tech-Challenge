package br.com.fiap.oficina.domain.entity

import java.math.BigDecimal

data class PecaServico(
    val peca: Peca,
    val quantidade: BigDecimal
) {

    companion object {
        fun criar(peca: Peca, quantidade: BigDecimal): PecaServico {
            require(quantidade > BigDecimal.ZERO) {
                "Quantidade consumida da peça deve ser maior que zero"
            }

            return PecaServico(peca = peca, quantidade = quantidade)
        }
    }

    /** Valor da peça no serviço: preço de venda multiplicado pela quantidade consumida. */
    fun subtotal(): BigDecimal = peca.precoDeVenda * quantidade
}
