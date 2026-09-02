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
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class BuscarFuncionarioPorNomeUseCaseTest {
    @Mock
    lateinit var funcionarioRepository: FuncionarioRepository

    @InjectMocks
    lateinit var useCase: BuscarFuncionarioPorNomeUseCase

    private lateinit var funcionario: Funcionario

    @BeforeEach
    fun setUp() {
        funcionario = Funcionario.criar(nome = "João", cargo = "ATENDENTE")
    }

    @Test
    fun `deve buscar funcionario por nome com sucesso`() {
        `when`(funcionarioRepository.buscarPorNome("João")).thenReturn(funcionario)

        val resultado = useCase.executar("João")

        assertEquals(funcionario.nome, resultado.nome)
        verify(funcionarioRepository, times(1)).buscarPorNome("João")
    }

    @Test
    fun `deve lancar excecao quando funcionario nao encontrado`() {
        `when`(funcionarioRepository.buscarPorNome("Inexistente")).thenReturn(null)

        assertThrows<FuncionarioNaoEncontradoException> {
            useCase.executar("Inexistente")
        }

        verify(funcionarioRepository, times(1)).buscarPorNome("Inexistente")
    }
}
