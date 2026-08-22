package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.repository.PecaRepository
import org.springframework.stereotype.Service

@Service
class BuscarPecaPorCodigoUseCase(private val repository: PecaRepository) {
    fun executar(codigo: String): Peca = repository.buscarAtivoPorCodigo(codigo)
        ?: throw IllegalArgumentException("Peça não encontrada")
}
