package br.com.fiap.oficina.application.usecase.peca

import br.com.fiap.oficina.application.port.out.PecaRepository
import br.com.fiap.oficina.domain.entity.Peca
import org.springframework.stereotype.Service

@Service
class ListarPecasUseCase(
    private val repository: PecaRepository,
) {
    fun executar(): List<Peca> = repository.listarAtivos()
}
