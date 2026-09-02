package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository

class CriarVeiculoUseCase(
    private val veiculoRepository: VeiculoRepository,
    private val clienteRepository: ClienteRepository,
) {
    fun executar(veiculo: Veiculo): Veiculo {
        require(!veiculoRepository.existePorPlaca(veiculo.placa)) { "Veiculo já cadastrado" }

        val motorista =
            clienteRepository.buscarPorId(veiculo.motorista.id)
                ?: throw ClienteNaoEncontradoException.porId(veiculo.motorista.id.valor.toString())

        return veiculoRepository.salvar(veiculo.copy(motorista = motorista))
    }
}
