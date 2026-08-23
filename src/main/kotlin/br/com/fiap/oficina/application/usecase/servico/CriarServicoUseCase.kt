package br.com.fiap.oficina.application.usecase.servico

import br.com.fiap.oficina.application.repository.servico.ServicoRepository
import br.com.fiap.oficina.domain.entity.servico.Servico
import java.math.BigDecimal

data class CriarServicoCommand(
    val descricao: String,
    val valor: BigDecimal,
)

class CriarServicoUseCase(
    private val repository: ServicoRepository,
) {
    fun executar(command: CriarServicoCommand): Servico {
        val servico =
            Servico.criar(
                descricao = command.descricao,
                valor = command.valor,
            )

        return repository.salvar(servico)
    }
}