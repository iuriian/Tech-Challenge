package br.com.fiap.oficina.application.servico.usecase

import br.com.fiap.oficina.application.servico.exception.ServicoNaoEncontradoException
import br.com.fiap.oficina.application.servico.repository.ServicoRepository
import br.com.fiap.oficina.domain.servico.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import java.math.BigDecimal

data class AtualizarServicoInput(val descricao: String, val valor: BigDecimal)

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
