package br.com.fiap.oficina.infrastructure.config

import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.usecase.cliente.AtualizarClienteUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorDocumentoUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorIdUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorNomeUseCase
import br.com.fiap.oficina.domain.usecase.cliente.CriarClienteUseCase
import br.com.fiap.oficina.domain.usecase.cliente.ListarClientesUseCase
import br.com.fiap.oficina.domain.usecase.cliente.RemoverClienteUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClienteBeanConfiguration {
    @Bean
    fun criarClienteUseCase(clienteRepository: ClienteRepository): CriarClienteUseCase =
        CriarClienteUseCase(clienteRepository)

    @Bean
    fun listarClientesUseCase(clienteRepository: ClienteRepository): ListarClientesUseCase =
        ListarClientesUseCase(clienteRepository)

    @Bean
    fun buscarClientePorIdUseCase(clienteRepository: ClienteRepository): BuscarClientePorIdUseCase =
        BuscarClientePorIdUseCase(clienteRepository)

    @Bean
    fun buscarClientePorNomeUseCase(clienteRepository: ClienteRepository): BuscarClientePorNomeUseCase =
        BuscarClientePorNomeUseCase(clienteRepository)

    @Bean
    fun buscarClientePorDocumentoUseCase(clienteRepository: ClienteRepository): BuscarClientePorDocumentoUseCase =
        BuscarClientePorDocumentoUseCase(clienteRepository)

    @Bean
    fun atualizarClienteUseCase(clienteRepository: ClienteRepository): AtualizarClienteUseCase =
        AtualizarClienteUseCase(clienteRepository)

    @Bean
    fun removerClienteUseCase(clienteRepository: ClienteRepository): RemoverClienteUseCase =
        RemoverClienteUseCase(clienteRepository)
}
