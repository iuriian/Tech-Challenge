package br.com.fiap.oficina.infrastructure.servico.config

import br.com.fiap.oficina.application.servico.repository.ServicoRepository
import br.com.fiap.oficina.application.servico.usecase.AtualizarServicoUseCase
import br.com.fiap.oficina.application.servico.usecase.BuscarServicoUseCase
import br.com.fiap.oficina.application.servico.usecase.CriarServicoUseCase
import br.com.fiap.oficina.application.servico.usecase.DesativarServicoUseCase
import br.com.fiap.oficina.application.servico.usecase.ListarServicosAtivosUseCase
import br.com.fiap.oficina.application.servico.usecase.ListarTodosServicosUseCase
import br.com.fiap.oficina.application.servico.usecase.ReativarServicoUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServicoUseCaseConfig {
    @Bean
    fun criarServicoUseCase(repository: ServicoRepository): CriarServicoUseCase = CriarServicoUseCase(repository)

    @Bean
    fun buscarServicoUseCase(repository: ServicoRepository): BuscarServicoUseCase = BuscarServicoUseCase(repository)

    @Bean
    fun atualizarServicoUseCase(repository: ServicoRepository): AtualizarServicoUseCase =
        AtualizarServicoUseCase(repository)

    @Bean
    fun listarServicosAtivosUseCase(repository: ServicoRepository): ListarServicosAtivosUseCase =
        ListarServicosAtivosUseCase(repository)

    @Bean
    fun listarTodosServicosUseCase(repository: ServicoRepository): ListarTodosServicosUseCase =
        ListarTodosServicosUseCase(repository)

    @Bean
    fun desativarServicoUseCase(repository: ServicoRepository): DesativarServicoUseCase =
        DesativarServicoUseCase(repository)

    @Bean
    fun reativarServicoUseCase(repository: ServicoRepository): ReativarServicoUseCase =
        ReativarServicoUseCase(repository)
}
