package br.com.fiap.oficina.servico.application.dto

import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus

data class ServicoComando(
    val id: Id? = null,
    val descricao: String,
    val funcionarioId: Id,
    val status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
    val clienteId: Id,
    val veiculoId: Id,
    val pecas: List<PecaServicoComando> = emptyList(),
)
