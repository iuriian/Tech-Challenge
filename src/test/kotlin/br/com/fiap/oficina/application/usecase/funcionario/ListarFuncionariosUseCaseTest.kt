package br.com.fiap.oficina.application.usecase.funcionario

import br.com.fiap.oficina.application.port.out.FuncionarioRepository
import br.com.fiap.oficina.domain.entity.Funcionario
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ListarFuncionariosUseCaseTest {
    @Mock
    lateinit var funcionarioRepository: FuncionarioRepository

    @InjectMocks
    lateinit var useCase: ListarFuncionariosUseCase

    private lateinit var funcionario: Funcionario

    @BeforeEach
    fun setUp() {
        funcionario = Funcionario.criar(nome = "João", cargo = "ATENDENTE")
    }

    @Test
    fun `deve listar todos os funcionarios`() {
        `when`(funcionarioRepository.listarTodos()).thenReturn(listOf(funcionario))

        val resultado = useCase.executar()

        assertEquals(listOf(funcionario), resultado)
        verify(funcionarioRepository, times(1)).listarTodos()
    }
}
