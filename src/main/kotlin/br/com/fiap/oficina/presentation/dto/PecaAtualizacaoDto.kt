package br.com.fiap.oficina.presentation.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class PecaAtualizacaoDto(
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
    val precoDeCompra: BigDecimal? = null,
    @field:DecimalMin("0.00")
    val precoDeVenda: BigDecimal,
)
