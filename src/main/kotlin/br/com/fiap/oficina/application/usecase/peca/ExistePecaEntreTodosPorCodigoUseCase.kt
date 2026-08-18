package br.com.fiap.oficina.application.usecase.peca

import br.com.fiap.oficina.application.port.out.PecaRepository
import org.springframework.stereotype.Service

@Service
class ExistePecaEntreTodosPorCodigoUseCase(
    private val repository: PecaRepository,
) {
    fun executar(codigo: String): Boolean = repository.existePorCodigo(codigo)
}
