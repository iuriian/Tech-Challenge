package br.com.fiap.oficina.servico.presentation.request

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class AtualizarServicoRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val descricao: String,

    @field:DecimalMin("0.00")
    val valor: BigDecimal,
)
