package br.com.fiap.oficina.application.servico.usecase

import br.com.fiap.oficina.application.servico.repository.ServicoRepository
import br.com.fiap.oficina.domain.servico.Servico
import java.math.BigDecimal

data class CriarServicoInput(val descricao: String, val valor: BigDecimal)

class CriarServicoUseCase(private val repository: ServicoRepository) {
    fun executar(input: CriarServicoInput): Servico {
        val servico =
            Servico.criar(
                descricao = input.descricao,
                valor = input.valor,
            )

        return repository.salvar(servico)
    }
}
