package br.com.fiap.oficina.presentation.dto

import br.com.fiap.oficina.domain.enum.ServicoStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class ServicoDto(
    val id: UUID? = null,

    @field:NotNull
    @field:NotBlank
    val descricao: String,

    val status: ServicoStatus? = ServicoStatus.EM_DIAGNOSTICO,

    @field:NotNull
    val funcionarioId: Long,


    @field:NotNull
    val clienteId: UUID,

    @field:NotNull
    val veiculoId: UUID,

    val pecasIds: List<UUID> = emptyList()
)
