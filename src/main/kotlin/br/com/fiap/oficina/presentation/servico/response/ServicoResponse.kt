package br.com.fiap.oficina.presentation.servico.response

import java.math.BigDecimal
import java.util.UUID

data class ServicoResponse(val id: UUID, val descricao: String, val valor: BigDecimal, val ativo: Boolean)
