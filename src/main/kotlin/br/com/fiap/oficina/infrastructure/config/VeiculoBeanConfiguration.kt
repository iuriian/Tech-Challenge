package br.com.fiap.oficina.infrastructure.config

import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.usecase.veiculo.AtualizarVeiculoUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculoPorIdUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculoPorPlacaUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculosPorMotoristaUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.CriarVeiculoUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.ListarVeiculosUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.RemoverVeiculoUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class VeiculoBeanConfiguration {
    @Bean
    fun criarVeiculoUseCase(
        veiculoRepository: VeiculoRepository,
        clienteRepository: ClienteRepository,
    ): CriarVeiculoUseCase = CriarVeiculoUseCase(veiculoRepository, clienteRepository)

    @Bean
    fun listarVeiculosUseCase(veiculoRepository: VeiculoRepository): ListarVeiculosUseCase =
        ListarVeiculosUseCase(veiculoRepository)

    @Bean
    fun buscarVeiculoPorIdUseCase(veiculoRepository: VeiculoRepository): BuscarVeiculoPorIdUseCase =
        BuscarVeiculoPorIdUseCase(veiculoRepository)

    @Bean
    fun buscarVeiculoPorPlacaUseCase(veiculoRepository: VeiculoRepository): BuscarVeiculoPorPlacaUseCase =
        BuscarVeiculoPorPlacaUseCase(veiculoRepository)

    @Bean
    fun buscarVeiculosPorMotoristaUseCase(veiculoRepository: VeiculoRepository): BuscarVeiculosPorMotoristaUseCase =
        BuscarVeiculosPorMotoristaUseCase(veiculoRepository)

    @Bean
    fun atualizarVeiculoUseCase(
        veiculoRepository: VeiculoRepository,
        clienteRepository: ClienteRepository,
    ): AtualizarVeiculoUseCase = AtualizarVeiculoUseCase(veiculoRepository, clienteRepository)

    @Bean
    fun removerVeiculoUseCase(veiculoRepository: VeiculoRepository): RemoverVeiculoUseCase =
        RemoverVeiculoUseCase(veiculoRepository)
}
