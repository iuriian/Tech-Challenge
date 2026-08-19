package br.com.fiap.oficina.presentation.dto

import java.math.BigDecimal
import java.util.UUID

data class ItemOrcamentoDto(
    val pecaId: UUID,
    val codigo: String,
    val nome: String,
    val precoUnitario: BigDecimal,
    val quantidade: BigDecimal,
    val subtotal: BigDecimal,
)

data class OrcamentoDto(
    val servicoId: UUID,
    val itens: List<ItemOrcamentoDto>,
    val valorTotal: BigDecimal,
)
