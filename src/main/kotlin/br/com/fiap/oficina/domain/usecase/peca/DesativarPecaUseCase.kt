package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.exception.PecaNaoEncontradoException
import br.com.fiap.oficina.domain.repository.PecaRepository

class DesativarPecaUseCase(private val repository: PecaRepository) {
    fun executar(codigo: String): Boolean {
        val peca =
            repository.buscarAtivoPorCodigo(codigo)
                ?: throw PecaNaoEncontradoException.porCodigo(codigo)

        repository.salvar(peca.desativar())
        return true
    }
}
