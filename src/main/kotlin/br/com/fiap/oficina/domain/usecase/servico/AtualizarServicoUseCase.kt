package br.com.fiap.oficina.domain.usecase.servico

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.exception.ServicoNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ServicoRepository

class AtualizarServicoUseCase(private val repository: ServicoRepository) {
    fun executar(servico: Servico): Servico {
        val existente =
            repository.buscarPorId(servico.id)
                ?: throw ServicoNaoEncontradoException(servico.id)

        val atualizado =
            existente
                .alterarDescricao(servico.descricao)
                .alterarValor(servico.valor)

        return repository.salvar(atualizado)
    }
}
