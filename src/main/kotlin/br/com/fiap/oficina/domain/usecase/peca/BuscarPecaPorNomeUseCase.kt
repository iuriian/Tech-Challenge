package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.exception.PecaNaoEncontradoException
import br.com.fiap.oficina.domain.repository.PecaRepository

class BuscarPecaPorNomeUseCase(private val repository: PecaRepository) {
    fun executar(nome: String): Peca =
        repository.buscarAtivoPorNome(nome)
            ?: throw PecaNaoEncontradoException.porNome(nome)
}
