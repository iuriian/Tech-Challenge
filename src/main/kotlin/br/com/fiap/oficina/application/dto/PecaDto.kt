package br.com.fiap.oficina.application.dto

import java.math.BigDecimal

data class PecaDto(
    val codigo: String,
    val nome: String,
    val descricao: String? = null,
    val fabricante: String? = null,
    val fornecedor: String? = null,
    val precoDeCompra: BigDecimal? = null,
    val precoDeVenda: BigDecimal,
    val qtdEstoque: Int,
)
