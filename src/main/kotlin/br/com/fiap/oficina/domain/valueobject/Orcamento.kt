package br.com.fiap.oficina.domain.valueobject

import java.math.BigDecimal

/** Linha do orçamento referente a uma peça consumida no serviço. */
data class ItemOrcamento(
    val pecaId: Id,
    val codigo: String,
    val nome: String,
    val precoUnitario: BigDecimal,
    val quantidade: BigDecimal,
    val subtotal: BigDecimal,
)

/**
 * Orçamento de um serviço: discrimina cada peça consumida e totaliza o valor
 * das peças (preço de venda × quantidade).
 */
data class Orcamento(val servicoId: Id, val itens: List<ItemOrcamento>, val valorTotal: BigDecimal)
