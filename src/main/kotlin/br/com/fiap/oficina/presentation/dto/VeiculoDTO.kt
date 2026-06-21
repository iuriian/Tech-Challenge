package br.com.fiap.oficina.presentation.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class VeiculoDTO(
    @Size(min = 3, max = 20) val nome: String,
    val marca: String,
    val modelo: String,
    val ano: String,
    @Size(min = 7, max = 7) val placa: String,
    @field:NotNull val motoristaId: UUID,
    val id: UUID? = null
)
