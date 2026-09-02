package br.com.fiap.oficina.application.command

import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Id
import java.math.BigDecimal

data class ItemOrcamentoComando(val tipo: TipoItemOrcamento, val referenciaId: Id, val quantidade: BigDecimal)
