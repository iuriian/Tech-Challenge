package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.entity.Veiculo
import org.springframework.stereotype.Service

@Service
class ListarVeiculosUseCase(
    private val veiculoRepository: VeiculoRepository,
) {
    fun executar(): List<Veiculo> = veiculoRepository.listarTodos()
}
