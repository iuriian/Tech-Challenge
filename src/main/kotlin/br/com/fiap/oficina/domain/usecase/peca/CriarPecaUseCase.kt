package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.repository.PecaRepository

class CriarPecaUseCase(private val repository: PecaRepository) {
    fun executar(peca: Peca): Peca {
        require(!repository.existePorCodigo(peca.codigo)) { "Peça já cadastrada" }

        return repository.salvar(peca)
    }
}
