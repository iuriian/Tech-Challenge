package br.com.fiap.oficina.application.dto

import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PecaServicoDto(
    @field:NotNull
    val pecaId: String,
    @field:NotNull
    @field:Positive
    val quantidade: BigDecimal,
)

data class ServicoDto(
    val id: UUID? = null,
    @field:NotNull
    @field:NotBlank
    val descricao: String,
    val status: OrdemServicoStatus? = null,
    @field:NotNull
    val funcionarioId: String,
    @field:NotNull
    val clienteId: String,
    @field:NotNull
    val veiculoId: String,
    @field:Valid
    val pecas: List<PecaServicoDto> = emptyList(),
    val dataAbertura: Instant? = null,
    val dataInicioExecucao: Instant? = null,
    val dataFinalizacao: Instant? = null,
)

data class TempoMedioExecucaoDto(val totalServicosFinalizados: Int, val tempoMedioMinutos: Double?)
