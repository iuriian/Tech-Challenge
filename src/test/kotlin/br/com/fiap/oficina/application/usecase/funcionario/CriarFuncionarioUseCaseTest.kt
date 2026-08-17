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
class CriarFuncionarioUseCaseTest {
    @Mock
    lateinit var funcionarioRepository: FuncionarioRepository

    @InjectMocks
    lateinit var useCase: CriarFuncionarioUseCase

    private lateinit var funcionario: Funcionario

    @BeforeEach
    fun setUp() {
        funcionario = Funcionario.criar(nome = "João", cargo = "ATENDENTE")
    }

    @Test
    fun `deve cadastrar um funcionario com sucesso`() {
        `when`(funcionarioRepository.salvar(funcionario)).thenReturn(funcionario)

        val resultado = useCase.executar(funcionario)

        assertNotNull(resultado)
        assertEquals(funcionario.id, resultado.id)
        assertEquals(funcionario.nome, resultado.nome)
        assertEquals(funcionario.cargo, resultado.cargo)

        verify(funcionarioRepository, times(1)).salvar(funcionario)
    }
}
