package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.dto.ClienteResponse
import br.com.fiap.oficina.application.service.ClienteService
import br.com.fiap.oficina.presentation.mapper.ClienteMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.UUID

class ClienteControllerTest {
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

    private val clienteResponse2 =
        ClienteResponse(
            id = "00000000-0000-0000-0000-000000000002",
            nome = "Maria Souza",
            numeroDocumento = "12345678901",
            tipoPessoa = "PESSOA_FISICA",
            email = "maria@example.com",
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
    fun `buscarPorId deve mapear resultado`() {
        `when`(service.buscarPorId(clienteResponse.id)).thenReturn(clienteResponse)

        val dto = controller.buscarPorId(clienteResponse.id)

        assertEquals("João Silva", dto.nome)
        assertEquals(UUID.fromString(clienteResponse.id), dto.id)
    }

    @Test
    fun `listarTodos deve retornar lista mapeada`() {
        `when`(service.listarTodos()).thenReturn(listOf(clienteResponse, clienteResponse2))

        val lista = controller.listarTodos()

        assertEquals(2, lista.size)
        assertEquals("João Silva", lista[0].nome)
        assertEquals("Maria Souza", lista[1].nome)
    }
}
