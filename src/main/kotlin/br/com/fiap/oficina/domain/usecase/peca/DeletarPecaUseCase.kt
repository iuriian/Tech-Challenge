package br.com.fiap.oficina.domain.usecase.peca

import org.springframework.stereotype.Service

@Service
class DeletarPecaUseCase(
    private val desativarPecaUseCase: DesativarPecaUseCase,
) {
    fun executar(codigo: String): Boolean = desativarPecaUseCase.executar(codigo)
}
