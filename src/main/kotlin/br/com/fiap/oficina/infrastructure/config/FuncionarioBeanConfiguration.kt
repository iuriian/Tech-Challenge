package br.com.fiap.oficina.infrastructure.config

import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.usecase.funcionario.AtualizarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorIdUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorNomeUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.CriarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.ListarFuncionariosUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.RemoverFuncionarioUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FuncionarioBeanConfiguration {
    @Bean
    fun criarFuncionarioUseCase(funcionarioRepository: FuncionarioRepository): CriarFuncionarioUseCase =
        CriarFuncionarioUseCase(funcionarioRepository)

    @Bean
    fun listarFuncionariosUseCase(funcionarioRepository: FuncionarioRepository): ListarFuncionariosUseCase =
        ListarFuncionariosUseCase(funcionarioRepository)

    @Bean
    fun buscarFuncionarioPorIdUseCase(funcionarioRepository: FuncionarioRepository): BuscarFuncionarioPorIdUseCase =
        BuscarFuncionarioPorIdUseCase(funcionarioRepository)

    @Bean
    fun buscarFuncionarioPorNomeUseCase(
        funcionarioRepository: FuncionarioRepository,
    ): BuscarFuncionarioPorNomeUseCase = BuscarFuncionarioPorNomeUseCase(funcionarioRepository)

    @Bean
    fun atualizarFuncionarioUseCase(funcionarioRepository: FuncionarioRepository): AtualizarFuncionarioUseCase =
        AtualizarFuncionarioUseCase(funcionarioRepository)

    @Bean
    fun removerFuncionarioUseCase(funcionarioRepository: FuncionarioRepository): RemoverFuncionarioUseCase =
        RemoverFuncionarioUseCase(funcionarioRepository)
}
