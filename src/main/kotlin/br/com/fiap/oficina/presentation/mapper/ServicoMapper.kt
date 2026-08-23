package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.application.service.TempoMedioExecucao
import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.valueobject.Orcamento
import br.com.fiap.oficina.presentation.dto.ItemOrcamentoDto
import br.com.fiap.oficina.presentation.dto.OrcamentoDto
import br.com.fiap.oficina.presentation.dto.PecaServicoDto
import br.com.fiap.oficina.presentation.dto.ServicoDto
import br.com.fiap.oficina.presentation.dto.TempoMedioExecucaoDto
import org.springframework.stereotype.Component

@Component
class ServicoMapper {
    fun toResponse(ordemServico: OrdemServico): ServicoDto =
        ServicoDto(
            id = ordemServico.id.valor,
            descricao = ordemServico.descricao,
            status = ordemServico.status,
            funcionarioId =
                ordemServico.funcionario.id.valor
                    .toString(),
            clienteId =
                ordemServico.cliente.id.valor
                    .toString(),
            veiculoId =
                ordemServico.veiculo.id.valor
                    .toString(),
            pecas =
                ordemServico.pecas.map {
                    PecaServicoDto(
                        it.peca.id.valor
                            .toString(),
                        it.quantidade,
                    )
                },
            dataAbertura = ordemServico.dataAbertura,
            dataInicioExecucao = ordemServico.dataInicioExecucao,
            dataFinalizacao = ordemServico.dataFinalizacao,
        )

    fun toResponse(tempo: TempoMedioExecucao): TempoMedioExecucaoDto =
        TempoMedioExecucaoDto(
            totalServicosFinalizados = tempo.totalServicosFinalizados,
            tempoMedioMinutos = tempo.tempoMedioMinutos,
        )

    fun toResponse(orcamento: Orcamento): OrcamentoDto =
        OrcamentoDto(
            servicoId = orcamento.ordemServicoId.valor,
            itens =
                orcamento.itens.map { item ->
                    ItemOrcamentoDto(
                        pecaId = item.referenciaId.valor,
                        codigo = item.codigoReferencia.orEmpty(),
                        nome = item.descricao,
                        precoUnitario = item.valorUnitario,
                        quantidade = item.quantidade,
                        subtotal = item.subtotal,
                    )
                },
            valorTotal = orcamento.valorTotal,
        )
}