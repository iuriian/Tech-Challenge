package br.com.fiap.oficina.domain.usecase.servico

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.exception.ServicoNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service
import java.math.BigDecimal

data class AtualizarServicoInput(val descricao: String, val valor: BigDecimal)

@Service
class AtualizarServicoUseCase(private val repository: ServicoRepository) {
    fun executar(id: Id, input: AtualizarServicoInput): Servico {
        val servico =
            repository.buscarPorId(id)
                ?: throw ServicoNaoEncontradoException(id)

        val atualizado =
            servico
                .alterarDescricao(input.descricao)
                .alterarValor(input.valor)

        return repository.salvar(atualizado)
    }
}
