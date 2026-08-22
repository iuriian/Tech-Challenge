package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.valueobject.Id
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
class BuscarFuncionarioPorIdUseCaseTest {
    @Mock
    lateinit var funcionarioRepository: FuncionarioRepository

    @InjectMocks
    lateinit var useCase: BuscarFuncionarioPorIdUseCase

    private lateinit var funcionario: Funcionario
    private lateinit var funcionarioId: Id

    @BeforeEach
    fun setUp() {
        funcionarioId = Id.generate()
        funcionario =
            Funcionario(
                id = funcionarioId,
                nome = "João",
                cargo = br.com.fiap.oficina.domain.enum.Cargo.ATENDENTE,
            )
    }

    @Test
    fun `deve buscar funcionario por id com sucesso`() {
        `when`(funcionarioRepository.buscarPorId(funcionarioId)).thenReturn(funcionario)

        val resultado = useCase.executar(funcionarioId)

        assertNotNull(resultado)
        assertEquals(funcionario.id, resultado.id)
        verify(funcionarioRepository, times(1)).buscarPorId(funcionarioId)
    }
}
