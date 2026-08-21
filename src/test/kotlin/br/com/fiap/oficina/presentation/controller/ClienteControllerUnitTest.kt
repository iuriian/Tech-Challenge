package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.usecase.cliente.AtualizarClienteUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorDocumentoUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorIdUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorNomeUseCase
import br.com.fiap.oficina.domain.usecase.cliente.CriarClienteUseCase
import br.com.fiap.oficina.domain.usecase.cliente.ListarClientesUseCase
import br.com.fiap.oficina.domain.usecase.cliente.RemoverClienteUseCase
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.ClienteDto
import br.com.fiap.oficina.presentation.mapper.ClienteMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.UUID
import kotlin.test.assertEquals

class ClienteControllerUnitTest {
    private val criarClienteUseCase = mock(CriarClienteUseCase::class.java)
    private val buscarClientePorIdUseCase = mock(BuscarClientePorIdUseCase::class.java)
    private val buscarClientePorNomeUseCase = mock(BuscarClientePorNomeUseCase::class.java)
    private val buscarClientePorDocumentoUseCase = mock(BuscarClientePorDocumentoUseCase::class.java)
    private val listarClientesUseCase = mock(ListarClientesUseCase::class.java)
    private val atualizarClienteUseCase = mock(AtualizarClienteUseCase::class.java)
    private val removerClienteUseCase = mock(RemoverClienteUseCase::class.java)
    private val controller =
        ClienteController(
            criarClienteUseCase,
            buscarClientePorIdUseCase,
            buscarClientePorNomeUseCase,
            buscarClientePorDocumentoUseCase,
            listarClientesUseCase,
            atualizarClienteUseCase,
            removerClienteUseCase,
            ClienteMapper(),
        )

    private val cliente =
        Cliente(
            id = Id.generate(),
            nome = "João Silva",
            documento = Documento.cpf("39053344705"),
            email = "joao@example.com",
        )

    private fun clienteDto() = ClienteDto(
        nome = "João Silva",
        numeroDocumento = "39053344705",
        tipoPessoa = "PESSOA_FISICA",
        email = "joao@example.com",
    )

    @Test
    fun `criar deve retornar dto do cliente salvo`() {
        `when`(criarClienteUseCase.executar(anyObject())).thenReturn(cliente)

        val dto = controller.criar(clienteDto())

        assertEquals("João Silva", dto.nome)
        assertEquals(cliente.id.valor, dto.id)
    }

    @Test
    fun `alterar deve retornar dto do cliente atualizado`() {
        `when`(atualizarClienteUseCase.executar(anyObject())).thenReturn(cliente)

        val dto = controller.alterar("00000000-0000-0000-0000-000000000001", clienteDto())

        assertEquals("João Silva", dto.nome)
    }

    @Test
    fun `remover deve delegar ao use case`() {
        val id = UUID.randomUUID()

        controller.remover(id.toString())

        verify(removerClienteUseCase).executar(Id.fromString(id.toString()))
    }

    @Test
    fun `buscarPorNome deve mapear resultado`() {
        `when`(buscarClientePorNomeUseCase.executar("João Silva")).thenReturn(cliente)

        assertEquals("João Silva", controller.buscarPorNome("João Silva")?.nome)
    }

    @Test
    fun `buscarPorCpf deve mapear resultado`() {
        `when`(buscarClientePorDocumentoUseCase.executar("39053344705")).thenReturn(cliente)

        assertEquals("João Silva", controller.buscarPorCpf("39053344705")?.nome)
    }
}
