package br.com.fiap.oficina.application

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.dto.FuncionarioRequest
import br.com.fiap.oficina.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.application.service.FuncionarioService
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.exception.FuncionarioNaoEncontradoException
import br.com.fiap.oficina.domain.usecase.funcionario.AtualizarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorIdUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorNomeUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.CriarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.ListarFuncionariosUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.RemoverFuncionarioUseCase
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class FuncionarioServiceTest {
    @Mock
    lateinit var criarFuncionarioUseCase: CriarFuncionarioUseCase

    @Mock
    lateinit var listarFuncionariosUseCase: ListarFuncionariosUseCase

    @Mock
    lateinit var buscarFuncionarioPorIdUseCase: BuscarFuncionarioPorIdUseCase

    @Mock
    lateinit var buscarFuncionarioPorNomeUseCase: BuscarFuncionarioPorNomeUseCase

    @Mock
    lateinit var atualizarFuncionarioUseCase: AtualizarFuncionarioUseCase

    @Mock
    lateinit var removerFuncionarioUseCase: RemoverFuncionarioUseCase

    private val mapper = FuncionarioMapper()
    private lateinit var service: FuncionarioService
    private lateinit var funcionario: Funcionario

    @BeforeEach
    fun setup() {
        service =
            FuncionarioService(
                criarFuncionarioUseCase,
                listarFuncionariosUseCase,
                buscarFuncionarioPorIdUseCase,
                buscarFuncionarioPorNomeUseCase,
                atualizarFuncionarioUseCase,
                removerFuncionarioUseCase,
                mapper,
            )
        funcionario =
            Funcionario(
                id = Id.generate(),
                nome = "João",
                cargo = Cargo.ATENDENTE,
            )
    }

    @Test
    fun `deve cadastrar funcionario`() {
        val request = FuncionarioRequest(nome = "João", cargo = "ATENDENTE")
        `when`(criarFuncionarioUseCase.executar(anyObject())).thenReturn(funcionario)

        val response = service.cadastrar(request)

        assertEquals(funcionario.id.valor.toString(), response.id)
        assertEquals("João", response.nome)
        verify(criarFuncionarioUseCase).executar(anyObject())
    }

    @Test
    fun `deve listar todos os funcionarios`() {
        `when`(listarFuncionariosUseCase.executar()).thenReturn(listOf(funcionario))

        val response = service.listarTodos()

        assertEquals(1, response.size)
        assertEquals("João", response.first().nome)
    }

    @Test
    fun `deve buscar funcionario por id`() {
        `when`(buscarFuncionarioPorIdUseCase.executar(funcionario.id)).thenReturn(funcionario)

        val response = service.buscarPorId(funcionario.id.valor.toString())

        assertEquals("João", response.nome)
    }

    @Test
    fun `deve propagar excecao ao buscar por id inexistente`() {
        val id = Id.generate()
        `when`(
            buscarFuncionarioPorIdUseCase.executar(id),
        ).thenThrow(FuncionarioNaoEncontradoException.porId(id.valor.toString()))

        assertThrows(FuncionarioNaoEncontradoException::class.java) {
            service.buscarPorId(id.valor.toString())
        }
    }

    @Test
    fun `deve buscar funcionario por nome`() {
        `when`(buscarFuncionarioPorNomeUseCase.executar("João")).thenReturn(funcionario)

        val response = service.buscarPorNome("João")

        assertEquals("João", response.nome)
    }

    @Test
    fun `deve propagar excecao ao buscar por nome inexistente`() {
        `when`(buscarFuncionarioPorNomeUseCase.executar("Inexistente"))
            .thenThrow(FuncionarioNaoEncontradoException.porNome("Inexistente"))

        assertThrows(FuncionarioNaoEncontradoException::class.java) {
            service.buscarPorNome("Inexistente")
        }
    }

    @Test
    fun `deve editar funcionario`() {
        val request = FuncionarioRequest(nome = "Maria", cargo = "MECANICO")
        val atualizado = funcionario.copy(nome = "Maria", cargo = Cargo.MECANICO)
        `when`(atualizarFuncionarioUseCase.executar(anyObject())).thenReturn(atualizado)

        val response = service.editar(funcionario.id.valor.toString(), request)

        assertEquals("Maria", response.nome)
        assertEquals("MECANICO", response.cargo)
    }

    @Test
    fun `deve deletar funcionario`() {
        service.deletar(funcionario.id.valor.toString())

        verify(removerFuncionarioUseCase).executar(funcionario.id)
    }

    @Test
    fun `deve propagar excecao ao deletar funcionario inexistente`() {
        val id = Id.generate()
        doThrow(FuncionarioNaoEncontradoException.porId(id.valor.toString()))
            .`when`(removerFuncionarioUseCase)
            .executar(id)

        assertThrows(FuncionarioNaoEncontradoException::class.java) {
            service.deletar(id.valor.toString())
        }

        verify(removerFuncionarioUseCase).executar(id)
    }
}
