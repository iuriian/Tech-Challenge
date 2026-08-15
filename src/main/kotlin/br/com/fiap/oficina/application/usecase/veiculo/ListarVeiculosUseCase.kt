package br.com.fiap.oficina.application.usecase.veiculo

import br.com.fiap.oficina.application.port.out.VeiculoRepository
import br.com.fiap.oficina.domain.entity.Veiculo
import org.springframework.stereotype.Service

@Service
class ListarVeiculosUseCase(
    private val veiculoRepository: VeiculoRepository,
) {
    fun executar(): List<Veiculo> = veiculoRepository.listarTodos()
}
