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
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class BuscarFuncionarioPorNomeUseCaseTest {
    @Mock
    lateinit var funcionarioRepository: FuncionarioRepository

    @InjectMocks
    lateinit var useCase: BuscarFuncionarioPorNomeUseCase

    private lateinit var funcionario: Funcionario
    private val nome = "João"

    @BeforeEach
    fun setUp() {
        funcionario = Funcionario.criar(nome = nome, cargo = "ATENDENTE")
    }

    @Test
    fun `deve buscar funcionario por nome com sucesso`() {
        `when`(funcionarioRepository.buscarPorNome(nome)).thenReturn(funcionario)

        val resultado = useCase.executar(nome)

        assertNotNull(resultado)
        assertEquals(nome, resultado.nome)
        verify(funcionarioRepository, times(1)).buscarPorNome(nome)
    }
}
