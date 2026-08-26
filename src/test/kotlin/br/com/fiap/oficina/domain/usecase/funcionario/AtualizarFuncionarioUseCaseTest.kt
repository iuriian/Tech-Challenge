package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.exception.FuncionarioNaoEncontradoException
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class AtualizarFuncionarioUseCaseTest {
    @Mock
    lateinit var funcionarioRepository: FuncionarioRepository

    @InjectMocks
    lateinit var useCase: AtualizarFuncionarioUseCase

    private lateinit var funcionario: Funcionario

    @BeforeEach
    fun setUp() {
        funcionario = Funcionario.criar(nome = "João", cargo = "ATENDENTE")
    }

    @Test
    fun `deve atualizar um funcionario com sucesso`() {
        `when`(funcionarioRepository.buscarPorId(funcionario.id)).thenReturn(funcionario)
        `when`(funcionarioRepository.editar(funcionario)).thenReturn(funcionario)

        val resultado = useCase.executar(funcionario)

        assertNotNull(resultado)
        assertEquals(funcionario.id, resultado.id)
        assertEquals(funcionario.nome, resultado.nome)
        assertEquals(funcionario.cargo, resultado.cargo)

        verify(funcionarioRepository, times(1)).buscarPorId(funcionario.id)
        verify(funcionarioRepository, times(1)).editar(funcionario)
    }

    @Test
    fun `deve lancar excecao quando funcionario nao encontrado`() {
        `when`(funcionarioRepository.buscarPorId(funcionario.id)).thenReturn(null)

        assertThrows<FuncionarioNaoEncontradoException> {
            useCase.executar(funcionario)
        }

        verify(funcionarioRepository, times(1)).buscarPorId(funcionario.id)
        verify(funcionarioRepository, never()).editar(funcionario)
    }
}
