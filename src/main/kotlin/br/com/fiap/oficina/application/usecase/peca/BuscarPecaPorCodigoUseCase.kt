package br.com.fiap.oficina.application.usecase.peca

import br.com.fiap.oficina.application.port.out.PecaRepository
import br.com.fiap.oficina.domain.entity.Peca
import org.springframework.stereotype.Service

@Service
class BuscarPecaPorCodigoUseCase(
    private val repository: PecaRepository,
) {
    fun executar(codigo: String): Peca =
        repository.buscarAtivoPorCodigo(codigo)
            ?: throw IllegalArgumentException("Peça não encontrada")
}
