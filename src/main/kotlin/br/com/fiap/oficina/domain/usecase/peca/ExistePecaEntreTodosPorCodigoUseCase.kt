package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.repository.PecaRepository
import org.springframework.stereotype.Service

@Service
class ExistePecaEntreTodosPorCodigoUseCase(
    private val repository: PecaRepository,
) {
    fun executar(codigo: String): Boolean = repository.existePorCodigo(codigo)
}
