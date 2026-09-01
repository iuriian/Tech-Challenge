package br.com.fiap.oficina.application.dto

import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import jakarta.validation.constraints.NotNull

data class AlterarStatusRequest(
    @field:NotNull
    val status: OrdemServicoStatus,
)
