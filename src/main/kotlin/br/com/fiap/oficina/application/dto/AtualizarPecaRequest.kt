package br.com.fiap.oficina.application.dto

import java.math.BigDecimal

data class AtualizarPecaRequest(
    val nome: String,
    val descricao: String? = null,
    val fabricante: String? = null,
    val fornecedor: String? = null,
    val precoDeCompra: BigDecimal? = null,
    val precoDeVenda: BigDecimal,
    val qtdEstoque: Int = 0,
)
