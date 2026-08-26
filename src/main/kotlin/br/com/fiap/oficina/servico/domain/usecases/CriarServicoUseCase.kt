package br.com.fiap.oficina.servico.domain.usecases

import br.com.fiap.oficina.servico.domain.entities.Servico
import br.com.fiap.oficina.servico.domain.repositories.ServicoRepository
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
