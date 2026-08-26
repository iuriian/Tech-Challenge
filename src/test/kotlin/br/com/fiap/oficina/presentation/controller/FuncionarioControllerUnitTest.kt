package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.dto.FuncionarioResponse
import br.com.fiap.oficina.application.service.FuncionarioService
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import br.com.fiap.oficina.presentation.mapper.FuncionarioMapper
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

class FuncionarioControllerUnitTest {
    private val service = mock(FuncionarioService::class.java)
    private val mapper = FuncionarioMapper()
    private val controller = FuncionarioController(service, mapper)

    private val funcionarioResponse = FuncionarioResponse(
        id = "00000000-0000-0000-0000-000000000001",
        nome = "João",
        cargo = "ATENDENTE",
    )

    private fun funcionarioRequestDto() = FuncionarioDto(nome = "João", cargo = "ATENDENTE")

    @BeforeEach
    fun setupRequestContext() {
        val request = MockHttpServletRequest("POST", "/funcionarios")
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(request))
    }

    @AfterEach
    fun clearRequestContext() {
        RequestContextHolder.resetRequestAttributes()
    }

    @Test
    fun `cadastrar deve retornar dto do funcionario salvo`() {
        `when`(service.cadastrar(anyObject())).thenReturn(funcionarioResponse)

        val response = controller.cadastrar(funcionarioRequestDto())

        assertEquals("João", response.body?.nome)
        assertEquals(funcionarioResponse.id, response.body?.id)
        assertEquals(201, response.statusCode.value())
    }

    @Test
    fun `alterar deve retornar dto do funcionario atualizado`() {
        `when`(service.editar(anyObject(), anyObject())).thenReturn(funcionarioResponse)

        val dto = controller.alterar("00000000-0000-0000-0000-000000000001", funcionarioRequestDto())

        assertEquals("João", dto.nome)
    }

    @Test
    fun `deletar deve delegar ao service`() {
        val id = UUID.randomUUID()

        controller.deletar(id.toString())

        verify(service).deletar(id.toString())
    }

    @Test
    fun `buscarPorNome deve mapear resultado`() {
        `when`(service.buscarPorNome("João")).thenReturn(funcionarioResponse)

        assertEquals("João", controller.buscarPorNome("João").nome)
    }

    @Test
    fun `listarTodos deve mapear resultados`() {
        `when`(service.listarTodos()).thenReturn(listOf(funcionarioResponse))

        assertEquals(1, controller.listarTodos().size)
        assertEquals("João", controller.listarTodos().first().nome)
    }
}
