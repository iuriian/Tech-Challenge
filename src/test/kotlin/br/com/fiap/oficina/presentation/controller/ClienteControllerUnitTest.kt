package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.dto.ClienteResponse
import br.com.fiap.oficina.application.service.ClienteService
import br.com.fiap.oficina.presentation.dto.ClienteDto
import br.com.fiap.oficina.presentation.mapper.ClienteMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID

class ClienteControllerUnitTest {
    private val service = mock(ClienteService::class.java)
    private val mapper = ClienteMapper()
    private val controller = ClienteController(service, mapper)

    private val clienteResponse =
        ClienteResponse(
            id = "00000000-0000-0000-0000-000000000001",
            nome = "João Silva",
            numeroDocumento = "39053344705",
            tipoPessoa = "PESSOA_FISICA",
            email = "joao@example.com",
        )

    private fun clienteDto() = ClienteDto(
        nome = "João Silva",
        numeroDocumento = "39053344705",
        tipoPessoa = "PESSOA_FISICA",
        email = "joao@example.com",
    )

    @BeforeEach
    fun setupRequestContext() {
        val request = MockHttpServletRequest("POST", "/clientes")
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(request))
    }

    @AfterEach
    fun clearRequestContext() {
        RequestContextHolder.resetRequestAttributes()
    }

    @Test
    fun `criar deve retornar dto do cliente salvo`() {
        `when`(service.criar(anyObject())).thenReturn(clienteResponse)

        val response = controller.criar(clienteDto())

        assertEquals("João Silva", response.body?.nome)
        assertEquals(UUID.fromString(clienteResponse.id), response.body?.id)
        assertEquals(201, response.statusCode.value())
    }

    @Test
    fun `alterar deve retornar dto do cliente atualizado`() {
        `when`(service.alterar(anyObject(), anyObject())).thenReturn(clienteResponse)

        val dto = controller.alterar("00000000-0000-0000-0000-000000000001", clienteDto())

        assertEquals("João Silva", dto.nome)
    }

    @Test
    fun `remover deve delegar ao service`() {
        val id = UUID.randomUUID()

        controller.remover(id.toString())

        verify(service).remover(id.toString())
    }

    @Test
    fun `buscarPorNome deve mapear resultado`() {
        `when`(service.buscarPorNome("João Silva")).thenReturn(clienteResponse)

        assertEquals("João Silva", controller.buscarPorNome("João Silva").nome)
    }

    @Test
    fun `buscarPorCpf deve mapear resultado`() {
        `when`(service.buscarPorDocumento("39053344705")).thenReturn(clienteResponse)

        assertEquals("João Silva", controller.buscarPorCpf("39053344705").nome)
    }
}
