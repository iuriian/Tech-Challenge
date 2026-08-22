package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
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

    private lateinit var funcionarios: List<Funcionario>

    @BeforeEach
    fun setUp() {
        funcionarios =
            listOf(
                Funcionario.criar(nome = "João", cargo = "ATENDENTE"),
                Funcionario.criar(nome = "Maria", cargo = "MECANICO"),
            )
    }

    @Test
    fun `deve listar todos os funcionarios`() {
        `when`(funcionarioRepository.listarTodos()).thenReturn(funcionarios)

        val resultado = useCase.executar()

        assertEquals(2, resultado.size)
        verify(funcionarioRepository, times(1)).listarTodos()
    }
}
