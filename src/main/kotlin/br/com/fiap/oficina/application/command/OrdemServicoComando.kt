package br.com.fiap.oficina.application.command

import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.valueobject.Id

data class OrdemServicoComando(
    val id: Id? = null,
    val descricao: String,
    val funcionarioId: Id,
    val status: OrdemServicoStatus = OrdemServicoStatus.RECEBIDA,
    val clienteId: Id,
    val veiculoId: Id,
    val itens: List<ItemOrcamentoComando> = emptyList(),
)
