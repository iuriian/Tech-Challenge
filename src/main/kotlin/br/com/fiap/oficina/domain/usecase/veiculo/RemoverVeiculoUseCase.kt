package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class RemoverVeiculoUseCase(private val veiculoRepository: VeiculoRepository) {
    fun executar(id: Id) {
        veiculoRepository.buscarPorId(id)
            ?: throw IllegalArgumentException("Veículo não encontrado com o ID: $id")
        veiculoRepository.remover(id)
    }
}
