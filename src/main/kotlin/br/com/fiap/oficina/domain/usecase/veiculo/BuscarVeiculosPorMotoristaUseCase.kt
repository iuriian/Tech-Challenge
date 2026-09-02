package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id

class BuscarVeiculosPorMotoristaUseCase(private val veiculoRepository: VeiculoRepository) {
    fun executar(motoristaId: Id): List<Veiculo> = veiculoRepository.buscarPorMotorista(motoristaId)
}
