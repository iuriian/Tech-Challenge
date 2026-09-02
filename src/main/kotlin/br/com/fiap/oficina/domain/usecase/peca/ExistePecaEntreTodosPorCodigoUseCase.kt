package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.repository.PecaRepository

class ExistePecaEntreTodosPorCodigoUseCase(private val repository: PecaRepository) {
    fun executar(codigo: String): Boolean = repository.existePorCodigo(codigo)
}
