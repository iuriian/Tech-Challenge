package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.service.FuncionarioService
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.UUID

class FuncionarioControllerUnitTest {
    private val service = mock(FuncionarioService::class.java)
    private val controller = FuncionarioController(service)

    private val funcionarioDto = FuncionarioDto(
        id = "00000000-0000-0000-0000-000000000001",
        nome = "João",
        cargo = "ATENDENTE",
    )

    private fun funcionarioRequestDto() = FuncionarioDto(nome = "João", cargo = "ATENDENTE")

    @Test
    fun `cadastrar deve retornar dto do funcionario salvo`() {
        `when`(service.cadastrar(anyObject())).thenReturn(funcionarioDto)

        val dto = controller.cadastrar(funcionarioRequestDto())

        assertEquals("João", dto.nome)
        assertEquals(funcionarioDto.id, dto.id)
    }

    @Test
    fun `alterar deve retornar dto do funcionario atualizado`() {
        `when`(service.editar(anyObject(), anyObject())).thenReturn(funcionarioDto)

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
        `when`(service.buscarPorNome("João")).thenReturn(funcionarioDto)

        assertEquals("João", controller.buscarPorNome("João")?.nome)
    }

    @Test
    fun `listarTodos deve mapear resultados`() {
        `when`(service.listarTodos()).thenReturn(listOf(funcionarioDto))

        assertEquals(1, controller.listarTodos().size)
        assertEquals("João", controller.listarTodos().first().nome)
    }
}
