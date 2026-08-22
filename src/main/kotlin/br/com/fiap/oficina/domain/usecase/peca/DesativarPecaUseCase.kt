package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.repository.PecaRepository
import org.springframework.stereotype.Service

@Service
class DesativarPecaUseCase(private val repository: PecaRepository) {
    fun executar(codigo: String): Boolean {
        val peca =
            repository.buscarAtivoPorCodigo(codigo)
                ?: throw IllegalArgumentException("Peça não encontrada")

        repository.salvar(peca.desativar())
        return true
    }
}
