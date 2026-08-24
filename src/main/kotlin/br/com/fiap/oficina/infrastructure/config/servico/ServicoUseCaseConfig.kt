package br.com.fiap.oficina.infrastructure.config.servico

import br.com.fiap.oficina.application.repository.servico.ServicoRepository
import br.com.fiap.oficina.application.usecase.servico.AtualizarServicoUseCase
import br.com.fiap.oficina.application.usecase.servico.BuscarServicoUseCase
import br.com.fiap.oficina.application.usecase.servico.CriarServicoUseCase
import br.com.fiap.oficina.application.usecase.servico.DesativarServicoUseCase
import br.com.fiap.oficina.application.usecase.servico.ListarServicosAtivosUseCase
import br.com.fiap.oficina.application.usecase.servico.ListarTodosServicosUseCase
import br.com.fiap.oficina.application.usecase.servico.ReativarServicoUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServicoUseCaseConfig {
    @Bean
    fun criarServicoUseCase(
        repository: ServicoRepository,
    ): CriarServicoUseCase =
        CriarServicoUseCase(repository)

    @Bean
    fun buscarServicoUseCase(
        repository: ServicoRepository,
    ): BuscarServicoUseCase =
        BuscarServicoUseCase(repository)

    @Bean
    fun atualizarServicoUseCase(
        repository: ServicoRepository,
    ): AtualizarServicoUseCase =
        AtualizarServicoUseCase(repository)

    @Bean
    fun listarServicosAtivosUseCase(
        repository: ServicoRepository,
    ): ListarServicosAtivosUseCase =
        ListarServicosAtivosUseCase(repository)

    @Bean
    fun listarTodosServicosUseCase(
        repository: ServicoRepository,
    ): ListarTodosServicosUseCase =
        ListarTodosServicosUseCase(repository)

    @Bean
    fun desativarServicoUseCase(
        repository: ServicoRepository,
    ): DesativarServicoUseCase =
        DesativarServicoUseCase(repository)

    @Bean
    fun reativarServicoUseCase(
        repository: ServicoRepository,
    ): ReativarServicoUseCase =
        ReativarServicoUseCase(repository)
}