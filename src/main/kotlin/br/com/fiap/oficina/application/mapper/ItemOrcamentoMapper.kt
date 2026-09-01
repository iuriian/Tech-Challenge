package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.ItemOrcamentoResponse
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import org.springframework.stereotype.Component

@Component
class ItemOrcamentoMapper {
    fun toResponse(item: ItemOrcamento): ItemOrcamentoResponse = ItemOrcamentoResponse(
        tipo = item.tipo,
        referenciaId = item.referenciaId.valor,
        codigoReferencia = item.codigoReferencia,
        descricao = item.descricao,
        valorUnitario = item.valorUnitario,
        quantidade = item.quantidade,
        subtotal = item.subtotal,
    )
}
