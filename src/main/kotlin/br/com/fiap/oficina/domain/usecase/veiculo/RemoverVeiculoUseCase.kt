package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.domain.exception.VeiculoNaoEncontradoException
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id

class RemoverVeiculoUseCase(private val veiculoRepository: VeiculoRepository) {
    fun executar(id: Id) {
        veiculoRepository.buscarPorId(id)
            ?: throw VeiculoNaoEncontradoException.porId(id.valor.toString())
        veiculoRepository.remover(id)
    }
}
