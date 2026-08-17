package br.com.fiap.oficina.application.usecase.veiculo

import br.com.fiap.oficina.application.port.out.VeiculoRepository
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class BuscarVeiculoPorIdUseCase(
    private val veiculoRepository: VeiculoRepository,
) {
    fun executar(id: Id): Veiculo? = veiculoRepository.buscarPorId(id)
}
