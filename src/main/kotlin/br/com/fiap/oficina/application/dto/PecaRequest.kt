package br.com.fiap.oficina.application.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PecaRequest(
    @field:Size(min = 3, max = 10)
    val codigo: String? = null,
    @field:NotBlank
    @field:Size(min = 3, max = 100)
    val nome: String,
    @field:Size(max = 255)
    val descricao: String? = null,
    @field:Size(max = 100)
    val fabricante: String? = null,
    @field:Size(max = 100)
    val fornecedor: String? = null,
    @field:DecimalMin("0.00")
    val precoDeCompra: Double? = null,
    @field:DecimalMin("0.00")
    val precoDeVenda: Double,
    @field:Min(0)
    val qtdEstoque: Int = 0,
)
