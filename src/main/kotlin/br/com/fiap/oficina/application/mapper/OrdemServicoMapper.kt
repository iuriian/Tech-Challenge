package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.command.ItemOrcamentoComando
import br.com.fiap.oficina.application.command.OrdemServicoComando
import br.com.fiap.oficina.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Component

@Component
class OrdemServicoMapper(private val itemOrcamentoMapper: ItemOrcamentoMapper) {
    fun toCommand(request: OrdemServicoRequest, id: Id? = null): OrdemServicoComando = OrdemServicoComando(
        id = id,
        descricao = request.descricao,
        funcionarioId = Id.fromString(request.funcionarioId),
        clienteId = Id.fromString(request.clienteId),
        veiculoId = Id.fromString(request.veiculoId),
        itens =
        request.itens.map { item ->
            ItemOrcamentoComando(
                tipo = item.tipo,
                referenciaId = Id.fromString(item.referenciaId),
                quantidade = item.quantidade,
            )
        },
    )

    fun toResponse(ordemServico: OrdemServico): OrdemServicoResponse = OrdemServicoResponse(
        id = ordemServico.id.valor,
        descricao = ordemServico.descricao,
        status = ordemServico.status,
        funcionarioId = ordemServico.funcionarioId.valor.toString(),
        clienteId = ordemServico.clienteId.valor.toString(),
        veiculoId = ordemServico.veiculoId.valor.toString(),
        itens = ordemServico.orcamento.itens.map(itemOrcamentoMapper::toResponse),
        dataAbertura = ordemServico.dataAbertura,
        dataInicioExecucao = ordemServico.dataInicioExecucao,
        dataFinalizacao = ordemServico.dataFinalizacao,
    )
}
