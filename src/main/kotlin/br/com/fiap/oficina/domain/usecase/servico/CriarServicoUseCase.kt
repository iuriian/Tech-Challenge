package br.com.fiap.oficina.domain.usecase.servico

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.repository.ServicoRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

data class CriarServicoInput(val descricao: String, val valor: BigDecimal)

@Service
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
