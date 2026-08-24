package br.com.fiap.oficina.presentation.dto

import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import jakarta.validation.constraints.NotNull

data class AlterarStatusDto(
    @field:NotNull
    val status: OrdemServicoStatus,
)
