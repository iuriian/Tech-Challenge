package br.com.fiap.oficina.application.usecase.peca

import br.com.fiap.oficina.application.port.out.PecaRepository
import br.com.fiap.oficina.domain.entity.Peca
import org.springframework.stereotype.Service

@Service
class CriarPecaUseCase(
    private val repository: PecaRepository,
) {
    fun executar(peca: Peca): Peca {
        require(!repository.existePorCodigo(peca.codigo)) { "Peça já cadastrada" }

        return repository.salvar(peca)
    }
}
