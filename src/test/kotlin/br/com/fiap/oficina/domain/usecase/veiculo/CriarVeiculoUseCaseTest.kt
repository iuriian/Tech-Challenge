package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class CriarVeiculoUseCaseTest {
    @Mock
    lateinit var veiculoRepository: VeiculoRepository

    @Mock
    lateinit var clienteRepository: ClienteRepository

    @InjectMocks
    lateinit var useCase: CriarVeiculoUseCase

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
            Veiculo.criar(
                marca = "Volkswagen",
                nome = "Gol",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motorista = motorista,
            )
    }

    @Test
    fun `deve salvar veiculo quando placa nao existe`() {
        `when`(veiculoRepository.existePorPlaca("ABC1D23")).thenReturn(false)
        `when`(clienteRepository.buscarPorId(motorista.id)).thenReturn(motorista)
        `when`(veiculoRepository.salvar(anyObject())).thenReturn(veiculo)

        val resultado = useCase.executar(veiculo)

        assertEquals(veiculo, resultado)
        verify(veiculoRepository).salvar(anyObject())
    }

    @Test
    fun `deve rejeitar veiculo com placa ja cadastrada`() {
        `when`(veiculoRepository.existePorPlaca("ABC1D23")).thenReturn(true)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                useCase.executar(veiculo)
            }

        assertEquals("Veiculo já cadastrado", exception.message)
        verify(veiculoRepository, never()).salvar(anyObject())
    }

    @Test
    fun `deve lancar excecao quando motorista nao encontrado`() {
        `when`(veiculoRepository.existePorPlaca("ABC1D23")).thenReturn(false)
        `when`(clienteRepository.buscarPorId(motorista.id)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                useCase.executar(veiculo)
            }

        assertTrue(exception.message!!.contains("Cliente não encontrado"))
    }
}
