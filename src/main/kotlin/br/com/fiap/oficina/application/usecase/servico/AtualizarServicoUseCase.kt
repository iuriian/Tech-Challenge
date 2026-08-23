package br.com.fiap.oficina.application.usecase.servico

import br.com.fiap.oficina.application.exception.servico.ServicoNaoEncontradoException
import br.com.fiap.oficina.application.repository.servico.ServicoRepository
import br.com.fiap.oficina.domain.entity.servico.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import java.math.BigDecimal

data class AtualizarServicoCommand(
    val descricao: String,
    val valor: BigDecimal,
)

class AtualizarServicoUseCase(
    private val repository: ServicoRepository,
) {
    fun executar(
        id: Id,
        command: AtualizarServicoCommand,
    ): Servico {
        val servico =
            repository.buscarPorId(id)
                ?: throw ServicoNaoEncontradoException(id)

        val atualizado =
            servico
                .alterarDescricao(command.descricao)
                .alterarValor(command.valor)

        return repository.salvar(atualizado)
    }
}