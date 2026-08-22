package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.repository.PecaRepository
import org.springframework.stereotype.Service

@Service
class BuscarPecaPorNomeUseCase(
    private val repository: PecaRepository,
) {
    fun executar(nome: String): Peca? = repository.buscarAtivoPorNome(nome)
}
