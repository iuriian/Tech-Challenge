package br.com.fiap.oficina.application.usecase.veiculo

import br.com.fiap.oficina.application.port.out.VeiculoRepository
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class BuscarVeiculosPorMotoristaUseCase(
    private val veiculoRepository: VeiculoRepository,
) {
    fun executar(motoristaId: Id): List<Veiculo> = veiculoRepository.buscarPorMotorista(motoristaId)
}
