package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.repository.PecaRepository
import org.springframework.stereotype.Service

@Service
class ReativarPecaUseCase(private val repository: PecaRepository) {
    fun executar(codigo: String): Boolean {
        val peca = repository.buscarPorCodigo(codigo) ?: return false

        repository.salvar(peca.reativar())
        return true
    }
}
