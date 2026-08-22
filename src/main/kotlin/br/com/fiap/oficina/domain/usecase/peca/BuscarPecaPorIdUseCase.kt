package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class BuscarPecaPorIdUseCase(
    private val repository: PecaRepository,
) {
    fun executar(id: Id): Peca? = repository.buscarPorId(id)
}
