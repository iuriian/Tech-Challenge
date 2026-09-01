package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.OrcamentoResponse
import br.com.fiap.oficina.domain.valueobject.Orcamento
import org.springframework.stereotype.Component

@Component
class OrcamentoMapper(private val itemOrcamentoMapper: ItemOrcamentoMapper) {
    fun toResponse(orcamento: Orcamento): OrcamentoResponse = OrcamentoResponse(
        itens = orcamento.itens.map(itemOrcamentoMapper::toResponse),
        valorTotal = orcamento.valorTotal,
    )
}
