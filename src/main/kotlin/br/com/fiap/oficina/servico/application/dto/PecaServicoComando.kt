package br.com.fiap.oficina.servico.application.dto

import br.com.fiap.oficina.domain.valueobject.Id
import java.math.BigDecimal

data class PecaServicoComando(val pecaId: Id, val quantidade: BigDecimal)
