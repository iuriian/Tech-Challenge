package br.com.fiap.oficina.application.dto

import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import jakarta.validation.constraints.NotNull

data class AlterarStatusDto(
    @field:NotNull
    val status: OrdemServicoStatus,
)
