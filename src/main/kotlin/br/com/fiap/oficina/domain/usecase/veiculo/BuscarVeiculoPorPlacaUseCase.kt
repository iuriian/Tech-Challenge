package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.exception.VeiculoNaoEncontradoException
import br.com.fiap.oficina.domain.repository.VeiculoRepository

class BuscarVeiculoPorPlacaUseCase(private val veiculoRepository: VeiculoRepository) {
    fun executar(placa: String): Veiculo = veiculoRepository.buscarPorPlaca(placa)
        ?: throw VeiculoNaoEncontradoException.porPlaca(placa)
}
