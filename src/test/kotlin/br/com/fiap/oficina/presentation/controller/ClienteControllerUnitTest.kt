package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.service.ClienteService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.ClienteDto
import br.com.fiap.oficina.presentation.mapper.ClienteMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.UUID

class ClienteControllerUnitTest {

    private val service = mock(ClienteService::class.java)
    private val controller = ClienteController(service, ClienteMapper())

    private val cliente = Cliente(
        id = Id.gerar(),
        nome = "João Silva",
        documento = Documento.cpf("39053344705"),
        email = "joao@example.com"
    )

    private fun clienteDto() = ClienteDto(
        nome = "João Silva",
        numeroDocumento = "39053344705",
        tipoPessoa = "PESSOA_FISICA",
        email = "joao@example.com"
    )

    @Test
    fun `criar deve retornar dto do cliente salvo`() {
        `when`(service.salvarCliente(anyObject())).thenReturn(cliente)

        val dto = controller.criar(clienteDto())

        assertEquals("João Silva", dto.nome)
        assertEquals(cliente.id.valor, dto.id)
    }

    @Test
    fun `alterar deve retornar dto do cliente atualizado`() {
        `when`(service.salvarCliente(anyObject())).thenReturn(cliente)

        val dto = controller.alterar(UUID.randomUUID(), clienteDto())

        assertEquals("João Silva", dto.nome)
    }

    @Test
    fun `remover deve delegar ao service`() {
        val id = UUID.randomUUID()

        controller.remover(id)

        verify(service).removerCliente(Id.from(id))
    }

    @Test
    fun `buscarPorNome deve mapear resultado`() {
        `when`(service.buscarPorNome("João Silva")).thenReturn(cliente)

        assertEquals("João Silva", controller.buscarPorNome("João Silva")?.nome)
    }

    @Test
    fun `buscarPorCpf deve mapear resultado`() {
        `when`(service.buscarPorDocumento("39053344705")).thenReturn(cliente)

        assertEquals("João Silva", controller.buscarPorCpf("39053344705")?.nome)
    }
}
