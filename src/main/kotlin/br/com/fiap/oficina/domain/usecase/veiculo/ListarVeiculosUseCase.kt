package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.VeiculoRepository

class ListarVeiculosUseCase(private val veiculoRepository: VeiculoRepository) {
    fun executar(): List<Veiculo> = veiculoRepository.listarTodos()
}
