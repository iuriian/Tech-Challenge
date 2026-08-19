package br.com.fiap.oficina.presentation.dto

import br.com.fiap.oficina.domain.enum.ServicoStatus
import jakarta.validation.constraints.NotNull

data class AlterarStatusDto(
    @field:NotNull
    val status: ServicoStatus,
)
