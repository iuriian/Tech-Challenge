package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.repository.PecaRepository

class ListarPecasUseCase(private val repository: PecaRepository) {
    fun executar(): List<Peca> = repository.listarAtivos()
}
