package br.com.fiap.oficina.infrastructure.config

import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.usecase.peca.AtualizarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.BuscarPecaEntreTodosPorCodigoUseCase
import br.com.fiap.oficina.domain.usecase.peca.BuscarPecaPorCodigoUseCase
import br.com.fiap.oficina.domain.usecase.peca.BuscarPecaPorIdUseCase
import br.com.fiap.oficina.domain.usecase.peca.BuscarPecaPorNomeUseCase
import br.com.fiap.oficina.domain.usecase.peca.CriarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.DeletarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.DesativarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.ExistePecaEntreTodosPorCodigoUseCase
import br.com.fiap.oficina.domain.usecase.peca.ExistePecaPorCodigoUseCase
import br.com.fiap.oficina.domain.usecase.peca.ListarPecasUseCase
import br.com.fiap.oficina.domain.usecase.peca.ReativarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.ReporPecasUseCase
import br.com.fiap.oficina.domain.usecase.peca.RetirarPecasUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PecaBeanConfiguration {
    @Bean
    fun criarPecaUseCase(pecaRepository: PecaRepository): CriarPecaUseCase = CriarPecaUseCase(pecaRepository)

    @Bean
    fun listarPecasUseCase(pecaRepository: PecaRepository): ListarPecasUseCase = ListarPecasUseCase(pecaRepository)

    @Bean
    fun buscarPecaPorCodigoUseCase(pecaRepository: PecaRepository): BuscarPecaPorCodigoUseCase =
        BuscarPecaPorCodigoUseCase(pecaRepository)

    @Bean
    fun buscarPecaPorNomeUseCase(pecaRepository: PecaRepository): BuscarPecaPorNomeUseCase =
        BuscarPecaPorNomeUseCase(pecaRepository)

    @Bean
    fun buscarPecaPorIdUseCase(pecaRepository: PecaRepository): BuscarPecaPorIdUseCase =
        BuscarPecaPorIdUseCase(pecaRepository)

    @Bean
    fun buscarPecaEntreTodosPorCodigoUseCase(pecaRepository: PecaRepository): BuscarPecaEntreTodosPorCodigoUseCase =
        BuscarPecaEntreTodosPorCodigoUseCase(pecaRepository)

    @Bean
    fun existePecaPorCodigoUseCase(pecaRepository: PecaRepository): ExistePecaPorCodigoUseCase =
        ExistePecaPorCodigoUseCase(pecaRepository)

    @Bean
    fun existePecaEntreTodosPorCodigoUseCase(pecaRepository: PecaRepository): ExistePecaEntreTodosPorCodigoUseCase =
        ExistePecaEntreTodosPorCodigoUseCase(pecaRepository)

    @Bean
    fun atualizarPecaUseCase(pecaRepository: PecaRepository): AtualizarPecaUseCase =
        AtualizarPecaUseCase(pecaRepository)

    @Bean
    fun retirarPecasUseCase(pecaRepository: PecaRepository): RetirarPecasUseCase = RetirarPecasUseCase(pecaRepository)

    @Bean
    fun reporPecasUseCase(pecaRepository: PecaRepository): ReporPecasUseCase = ReporPecasUseCase(pecaRepository)

    @Bean
    fun desativarPecaUseCase(pecaRepository: PecaRepository): DesativarPecaUseCase = DesativarPecaUseCase(pecaRepository)

    @Bean
    fun deletarPecaUseCase(desativarPecaUseCase: DesativarPecaUseCase): DeletarPecaUseCase =
        DeletarPecaUseCase(desativarPecaUseCase)

    @Bean
    fun reativarPecaUseCase(pecaRepository: PecaRepository): ReativarPecaUseCase = ReativarPecaUseCase(pecaRepository)
}
