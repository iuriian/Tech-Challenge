package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.exception.PecaNaoEncontradoException
import br.com.fiap.oficina.domain.repository.PecaRepository

class ReporPecasUseCase(private val repository: PecaRepository) {
    fun executar(codigo: String, qtd: Int): Peca {
        val peca =
            repository.buscarAtivoPorCodigo(codigo)
                ?: throw PecaNaoEncontradoException.porCodigo(codigo)

        return repository.salvar(peca.reporPecas(qtd))
    }
}
