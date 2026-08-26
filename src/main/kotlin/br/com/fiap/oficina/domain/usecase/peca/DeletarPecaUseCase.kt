package br.com.fiap.oficina.domain.usecase.peca

class DeletarPecaUseCase(private val desativarPecaUseCase: DesativarPecaUseCase) {
    fun executar(codigo: String): Boolean = desativarPecaUseCase.executar(codigo)
}
