package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.repository.PecaRepository
import org.springframework.stereotype.Service

@Service
class RetirarPecasUseCase(
    private val repository: PecaRepository,
) {
    fun executar(
        codigo: String,
        qtd: Int,
    ): Peca? {
        val peca =
            repository.buscarAtivoPorCodigo(codigo)
                ?: throw IllegalArgumentException("Peça não encontrada")

        return repository.salvar(peca.retirarPecas(qtd))
    }
}
