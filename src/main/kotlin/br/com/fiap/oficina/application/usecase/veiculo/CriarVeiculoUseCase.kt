package br.com.fiap.oficina.application.usecase.veiculo

import br.com.fiap.oficina.application.port.out.VeiculoRepository
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.ClienteRepository
import org.springframework.stereotype.Service

@Service
class CriarVeiculoUseCase(
    private val veiculoRepository: VeiculoRepository,
    private val clienteRepository: ClienteRepository,
) {
    fun executar(veiculo: Veiculo): Veiculo {
        require(!veiculoRepository.existePorPlaca(veiculo.placa)) { "Veiculo já cadastrado" }

        val motorista =
            clienteRepository.buscarPorId(veiculo.motorista.id)
                ?: throw IllegalArgumentException("Cliente não encontrado com o ID: ${veiculo.motorista.id}")

        return veiculoRepository.salvar(veiculo.copy(motorista = motorista))
    }
}
