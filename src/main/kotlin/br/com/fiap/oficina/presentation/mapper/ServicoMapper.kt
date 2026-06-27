package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.application.service.TempoMedioExecucao
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.valueobject.Orcamento
import br.com.fiap.oficina.presentation.dto.ItemOrcamentoDto
import br.com.fiap.oficina.presentation.dto.OrcamentoDto
import br.com.fiap.oficina.presentation.dto.PecaServicoDto
import br.com.fiap.oficina.presentation.dto.ServicoDto
import br.com.fiap.oficina.presentation.dto.TempoMedioExecucaoDto
import org.springframework.stereotype.Component

@Component
class ServicoMapper {
    fun toResponse(servico: Servico): ServicoDto =
        ServicoDto(
            id = servico.id.valor,
            descricao = servico.descricao,
            status = servico.status,
            funcionarioId =
                servico.funcionario.id.valor
                    .toString(),
            clienteId =
                servico.cliente.id.valor
                    .toString(),
            veiculoId =
                servico.veiculo.id.valor
                    .toString(),
            pecas =
                servico.pecas.map {
                    PecaServicoDto(
                        it.peca.id.valor
                            .toString(),
                        it.quantidade,
                    )
                },
            dataAbertura = servico.dataAbertura,
            dataInicioExecucao = servico.dataInicioExecucao,
            dataFinalizacao = servico.dataFinalizacao,
        )

    fun toResponse(tempo: TempoMedioExecucao): TempoMedioExecucaoDto =
        TempoMedioExecucaoDto(
            totalServicosFinalizados = tempo.totalServicosFinalizados,
            tempoMedioMinutos = tempo.tempoMedioMinutos,
        )

    fun toResponse(orcamento: Orcamento): OrcamentoDto =
        OrcamentoDto(
            servicoId = orcamento.servicoId.valor,
            itens =
                orcamento.itens.map { item ->
                    ItemOrcamentoDto(
                        pecaId = item.pecaId.valor,
                        codigo = item.codigo,
                        nome = item.nome,
                        precoUnitario = item.precoUnitario,
                        quantidade = item.quantidade,
                        subtotal = item.subtotal,
                    )
                },
            valorTotal = orcamento.valorTotal,
        )
}
