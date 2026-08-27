package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.exception.VeiculoNaoEncontradoException
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
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
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class BuscarVeiculoPorPlacaUseCaseTest {
    @Mock
    lateinit var veiculoRepository: VeiculoRepository

    @InjectMocks
    lateinit var useCase: BuscarVeiculoPorPlacaUseCase

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
    fun `deve buscar veiculo por placa com sucesso`() {
        `when`(veiculoRepository.buscarPorPlaca("ABC1D23")).thenReturn(veiculo)

        val resultado = useCase.executar("ABC1D23")

        assertNotNull(resultado)
        assertEquals(veiculo.placa, resultado.placa)
        verify(veiculoRepository, times(1)).buscarPorPlaca("ABC1D23")
    }

    @Test
    fun `deve lancar excecao quando veiculo nao encontrado`() {
        `when`(veiculoRepository.buscarPorPlaca("ABC1D23")).thenReturn(null)

        assertThrows<VeiculoNaoEncontradoException> {
            useCase.executar("ABC1D23")
        }
    }
}
