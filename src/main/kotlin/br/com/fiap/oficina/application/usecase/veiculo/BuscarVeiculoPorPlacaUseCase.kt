package br.com.fiap.oficina.application.usecase.veiculo

import br.com.fiap.oficina.application.port.out.VeiculoRepository
import br.com.fiap.oficina.domain.entity.Veiculo
import org.springframework.stereotype.Service

@Service
class BuscarVeiculoPorPlacaUseCase(
    private val veiculoRepository: VeiculoRepository,
) {
    fun executar(placa: String): Veiculo? = veiculoRepository.buscarPorPlaca(placa)
}
