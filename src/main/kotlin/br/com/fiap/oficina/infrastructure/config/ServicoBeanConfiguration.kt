package br.com.fiap.oficina.infrastructure.config

import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.usecase.servico.AtualizarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.BuscarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.CriarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.DesativarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.ListarServicosAtivosUseCase
import br.com.fiap.oficina.domain.usecase.servico.ListarTodosServicosUseCase
import br.com.fiap.oficina.domain.usecase.servico.ReativarServicoUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServicoBeanConfiguration {
    @Bean
    fun criarServicoUseCase(servicoRepository: ServicoRepository): CriarServicoUseCase =
        CriarServicoUseCase(servicoRepository)

    @Bean
    fun buscarServicoUseCase(servicoRepository: ServicoRepository): BuscarServicoUseCase =
        BuscarServicoUseCase(servicoRepository)

    @Bean
    fun atualizarServicoUseCase(servicoRepository: ServicoRepository): AtualizarServicoUseCase =
        AtualizarServicoUseCase(servicoRepository)

    @Bean
    fun listarServicosAtivosUseCase(servicoRepository: ServicoRepository): ListarServicosAtivosUseCase =
        ListarServicosAtivosUseCase(servicoRepository)

    @Bean
    fun listarTodosServicosUseCase(servicoRepository: ServicoRepository): ListarTodosServicosUseCase =
        ListarTodosServicosUseCase(servicoRepository)

    @Bean
    fun desativarServicoUseCase(servicoRepository: ServicoRepository): DesativarServicoUseCase =
        DesativarServicoUseCase(servicoRepository)

    @Bean
    fun reativarServicoUseCase(servicoRepository: ServicoRepository): ReativarServicoUseCase =
        ReativarServicoUseCase(servicoRepository)
}
