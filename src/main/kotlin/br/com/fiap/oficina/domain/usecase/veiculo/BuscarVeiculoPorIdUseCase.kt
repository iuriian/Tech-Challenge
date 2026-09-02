package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.exception.VeiculoNaoEncontradoException
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id

class BuscarVeiculoPorIdUseCase(private val veiculoRepository: VeiculoRepository) {
    fun executar(id: Id): Veiculo = veiculoRepository.buscarPorId(id)
        ?: throw VeiculoNaoEncontradoException.porId(id.valor.toString())
}
