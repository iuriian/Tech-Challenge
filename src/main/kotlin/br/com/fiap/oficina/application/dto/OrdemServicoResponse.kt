package br.com.fiap.oficina.application.dto

import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import java.time.Instant
import java.util.UUID

data class OrdemServicoResponse(
    val id: UUID,
    val descricao: String,
    val status: OrdemServicoStatus,
    val funcionarioId: String,
    val clienteId: String,
    val veiculoId: String,
    val itens: List<ItemOrcamentoResponse>,
    val dataAbertura: Instant,
    val dataInicioExecucao: Instant?,
    val dataFinalizacao: Instant?,
)
