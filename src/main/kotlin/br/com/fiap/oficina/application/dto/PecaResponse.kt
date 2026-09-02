package br.com.fiap.oficina.application.dto

data class PecaResponse(
    val id: String,
    val codigo: String,
    val nome: String,
    val descricao: String? = null,
    val fabricante: String? = null,
    val fornecedor: String? = null,
    val precoDeCompra: Double? = null,
    val precoDeVenda: Double,
    val qtdEstoque: Int,
    val ativo: Boolean,
)
