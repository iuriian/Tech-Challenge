package br.com.fiap.oficina.application.dto

import java.math.BigDecimal
import java.util.UUID

data class ServicoResponse(val id: UUID, val descricao: String, val valor: BigDecimal, val ativo: Boolean)
