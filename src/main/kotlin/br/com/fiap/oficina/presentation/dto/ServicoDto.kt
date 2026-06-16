package br.com.fiap.oficina.presentation.dto

import br.com.fiap.oficina.domain.enum.ServicoStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.util.UUID

data class PecaServicoDto(
    @field:NotNull
    val pecaId: UUID,

    @field:NotNull
    @field:Positive
    val quantidade: BigDecimal
)

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

    @field:Valid
    val pecas: List<PecaServicoDto> = emptyList()
)
