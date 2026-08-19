package br.com.fiap.oficina.application.port.`in`

import br.com.fiap.oficina.domain.entity.Servico
import java.math.BigDecimal

data class CriarServicoCommand(
    val descricao: String,
    val valor: BigDecimal,
)

interface CriarServicoUseCase {
    fun executar(command: CriarServicoCommand): Servico
}