package br.com.fiap.oficina.domain.valueobject

import java.math.BigDecimal

data class Orcamento(val itens: List<ItemOrcamento> = emptyList()) {
    val valorTotal: BigDecimal
        get() =
            itens.fold(BigDecimal.ZERO) { total, item ->
                total + item.subtotal
            }

    fun adicionarItem(item: ItemOrcamento): Orcamento = copy(itens = itens + item)
}
