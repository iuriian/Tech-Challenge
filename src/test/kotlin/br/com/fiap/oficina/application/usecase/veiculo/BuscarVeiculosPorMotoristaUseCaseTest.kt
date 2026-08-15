package br.com.fiap.oficina.application.usecase.veiculo

import br.com.fiap.oficina.application.port.out.VeiculoRepository
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
class BuscarVeiculosPorMotoristaUseCaseTest {
    @Mock
    lateinit var veiculoRepository: VeiculoRepository

    @InjectMocks
    lateinit var useCase: BuscarVeiculosPorMotoristaUseCase

    private lateinit var motorista: Cliente
    private lateinit var veiculo: Veiculo

    @BeforeEach
    fun setUp() {
        motorista =
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
    fun `deve buscar veiculos por motorista`() {
        `when`(veiculoRepository.buscarPorMotorista(motorista.id)).thenReturn(listOf(veiculo))

        val resultado = useCase.executar(motorista.id)

        assertEquals(listOf(veiculo), resultado)
        verify(veiculoRepository, times(1)).buscarPorMotorista(motorista.id)
    }
}
