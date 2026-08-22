package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Documento
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

@ExtendWith(MockitoExtension::class)
class ListarVeiculosUseCaseTest {
    @Mock
    lateinit var veiculoRepository: VeiculoRepository

    @InjectMocks
    lateinit var useCase: ListarVeiculosUseCase

    private lateinit var veiculo: Veiculo

    @BeforeEach
    fun setUp() {
        val motorista =
            Cliente(
                id = Id.generate(),
                nome = "Dono",
                documento = Documento.cpf("39053344705"),
                email = "dono@example.com",
            )
        veiculo =
            Veiculo(
                id = Id.generate(),
                marca = "Volkswagen",
                nome = "Gol",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motorista = motorista,
            )
    }

    @Test
    fun `deve listar todos os veiculos`() {
        `when`(veiculoRepository.listarTodos()).thenReturn(listOf(veiculo))

        val resultado = useCase.executar()

        assertEquals(listOf(veiculo), resultado)
        verify(veiculoRepository, times(1)).listarTodos()
    }
}
