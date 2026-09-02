package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.repository.PecaRepository

class BuscarPecaEntreTodosPorCodigoUseCase(private val repository: PecaRepository) {
    fun executar(codigo: String): Peca? = repository.buscarPorCodigo(codigo)
}
