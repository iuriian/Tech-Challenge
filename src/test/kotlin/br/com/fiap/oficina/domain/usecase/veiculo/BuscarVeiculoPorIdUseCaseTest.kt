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
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class BuscarVeiculoPorIdUseCaseTest {
    @Mock
    lateinit var veiculoRepository: VeiculoRepository

    @InjectMocks
    lateinit var useCase: BuscarVeiculoPorIdUseCase

    private lateinit var veiculo: Veiculo
    private lateinit var veiculoId: Id

    @BeforeEach
    fun setUp() {
        veiculoId = Id.generate()
        val motorista =
            Cliente(
                id = Id.generate(),
                nome = "Dono",
                documento = Documento.cpf("39053344705"),
                email = "dono@example.com",
            )
        veiculo =
            Veiculo(
                id = veiculoId,
                marca = "Volkswagen",
                nome = "Gol",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motorista = motorista,
            )
    }

    @Test
    fun `deve buscar veiculo por id com sucesso`() {
        `when`(veiculoRepository.buscarPorId(veiculoId)).thenReturn(veiculo)

        val resultado = useCase.executar(veiculoId)

        assertNotNull(resultado)
        assertEquals(veiculo.id, resultado.id)
        verify(veiculoRepository, times(1)).buscarPorId(veiculoId)
    }
}
