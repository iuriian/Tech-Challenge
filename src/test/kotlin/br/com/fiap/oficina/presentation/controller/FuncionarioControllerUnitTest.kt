package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.usecase.funcionario.AtualizarFuncionarioUseCase
import br.com.fiap.oficina.application.usecase.funcionario.BuscarFuncionarioPorIdUseCase
import br.com.fiap.oficina.application.usecase.funcionario.BuscarFuncionarioPorNomeUseCase
import br.com.fiap.oficina.application.usecase.funcionario.CriarFuncionarioUseCase
import br.com.fiap.oficina.application.usecase.funcionario.ListarFuncionariosUseCase
import br.com.fiap.oficina.application.usecase.funcionario.RemoverFuncionarioUseCase
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import br.com.fiap.oficina.presentation.mapper.FuncionarioMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.UUID

class FuncionarioControllerUnitTest {
    private val criarFuncionarioUseCase = mock(CriarFuncionarioUseCase::class.java)
    private val listarFuncionariosUseCase = mock(ListarFuncionariosUseCase::class.java)
    private val buscarFuncionarioPorIdUseCase = mock(BuscarFuncionarioPorIdUseCase::class.java)
    private val buscarFuncionarioPorNomeUseCase = mock(BuscarFuncionarioPorNomeUseCase::class.java)
    private val atualizarFuncionarioUseCase = mock(AtualizarFuncionarioUseCase::class.java)
    private val removerFuncionarioUseCase = mock(RemoverFuncionarioUseCase::class.java)
    private val controller =
        FuncionarioController(
            criarFuncionarioUseCase,
            listarFuncionariosUseCase,
            buscarFuncionarioPorIdUseCase,
            buscarFuncionarioPorNomeUseCase,
            atualizarFuncionarioUseCase,
            removerFuncionarioUseCase,
            FuncionarioMapper(),
        )

    private val funcionario =
        Funcionario(
            id = Id.generate(),
            nome = "João",
            cargo = Cargo.ATENDENTE,
        )

    private fun funcionarioDto() = FuncionarioDto(nome = "João", cargo = "ATENDENTE")

    @Test
    fun `cadastrar deve retornar dto do funcionario salvo`() {
        `when`(criarFuncionarioUseCase.executar(anyObject())).thenReturn(funcionario)

        val dto = controller.cadastrar(funcionarioDto())

        assertEquals("João", dto.nome)
        assertEquals(funcionario.id.valor.toString(), dto.id)
    }

    @Test
    fun `alterar deve retornar dto do funcionario atualizado`() {
        `when`(atualizarFuncionarioUseCase.executar(anyObject())).thenReturn(funcionario)

        val dto = controller.alterar("00000000-0000-0000-0000-000000000001", funcionarioDto())

        assertEquals("João", dto.nome)
    }

    @Test
    fun `deletar deve delegar ao use case`() {
        val id = UUID.randomUUID()

        controller.deletar(id.toString())

        verify(removerFuncionarioUseCase).executar(Id.fromString(id.toString()))
    }

    @Test
    fun `buscarPorNome deve mapear resultado`() {
        `when`(buscarFuncionarioPorNomeUseCase.executar("João")).thenReturn(funcionario)

        assertEquals("João", controller.buscarPorNome("João")?.nome)
    }

    @Test
    fun `listarTodos deve mapear resultados`() {
        `when`(listarFuncionariosUseCase.executar()).thenReturn(listOf(funcionario))

        assertEquals(1, controller.listarTodos().size)
        assertEquals("João", controller.listarTodos().first().nome)
    }
}
