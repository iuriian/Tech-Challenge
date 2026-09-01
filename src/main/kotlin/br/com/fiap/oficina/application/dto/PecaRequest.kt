package br.com.fiap.oficina.application.dto

data class PecaRequest(
    val codigo: String? = null,
    val nome: String,
    val descricao: String? = null,
    val fabricante: String? = null,
    val fornecedor: String? = null,
    val precoDeCompra: Double? = null,
    val precoDeVenda: Double,
    val qtdEstoque: Int = 0,
)
