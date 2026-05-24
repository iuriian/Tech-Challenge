package br.com.fiap.oficina.presentation.dto

import br.com.fiap.oficina.domain.enum.ServicoStatus
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull

data class ServicoDto(
    val id: Long? = null,

    @field:NotNull
    @field:NotBlank
    val descricao: String,

    val status: ServicoStatus? = ServicoStatus.EM_DIAGNOSTICO,

    @field:NotNull
    @field:NotBlank
    val funcionarioId: String,


    @field:NotNull
    @field:NotBlank
    val cliente: Long,

    @field:NotNull
    @field:NotBlank
    val veiculoId: Long,

    val pecasIds: List<Long> = emptyList()
)
