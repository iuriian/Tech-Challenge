package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AtualizarVeiculoUseCaseTest {
    @Mock
    lateinit var veiculoRepository: VeiculoRepository

    @Mock
    lateinit var clienteRepository: ClienteRepository

    @InjectMocks
    lateinit var useCase: AtualizarVeiculoUseCase

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
    fun `deve atualizar veiculo mantendo a mesma placa`() {
        `when`(veiculoRepository.buscarPorId(veiculo.id)).thenReturn(veiculo)
        `when`(clienteRepository.buscarPorId(motorista.id)).thenReturn(motorista)
        `when`(veiculoRepository.salvar(anyObject())).thenReturn(veiculo)

        val resultado = useCase.executar(veiculo)

        assertEquals(veiculo, resultado)
        verify(veiculoRepository, never()).existePorPlaca(anyObject())
        verify(veiculoRepository).salvar(anyObject())
    }

    @Test
    fun `deve atualizar veiculo com nova placa disponivel`() {
        val novaPlaca = "XYZ9B76"
        val veiculoAtualizado = veiculo.copy(placa = novaPlaca)
        `when`(veiculoRepository.buscarPorId(veiculo.id)).thenReturn(veiculo)
        `when`(veiculoRepository.existePorPlaca(novaPlaca)).thenReturn(false)
        `when`(clienteRepository.buscarPorId(motorista.id)).thenReturn(motorista)
        `when`(veiculoRepository.salvar(anyObject())).thenReturn(veiculoAtualizado)

        val resultado = useCase.executar(veiculo.copy(placa = novaPlaca))

        assertEquals(novaPlaca, resultado.placa)
        verify(veiculoRepository).existePorPlaca(novaPlaca)
    }

    @Test
    fun `deve rejeitar atualizacao quando nova placa ja esta em uso`() {
        val novaPlaca = "XYZ9B76"
        `when`(veiculoRepository.buscarPorId(veiculo.id)).thenReturn(veiculo)
        `when`(veiculoRepository.existePorPlaca(novaPlaca)).thenReturn(true)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                useCase.executar(veiculo.copy(placa = novaPlaca))
            }

        assertTrue(exception.message!!.contains(novaPlaca))
        verify(veiculoRepository, never()).salvar(anyObject())
    }

    @Test
    fun `deve lancar excecao ao atualizar veiculo inexistente`() {
        val idInexistente = Id.generate()
        val veiculoInexistente = veiculo.copy(id = idInexistente)
        `when`(veiculoRepository.buscarPorId(idInexistente)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                useCase.executar(veiculoInexistente)
            }

        assertTrue(exception.message!!.contains("não encontrado"))
        verify(veiculoRepository, never()).salvar(anyObject())
    }

    @Test
    fun `deve lancar excecao ao atualizar quando novo motorista nao existe`() {
        val motoristaIdInexistente = Id.generate()
        val veiculoComMotoristaInvalido =
            veiculo.copy(
                motorista =
                    Cliente(
                        id = motoristaIdInexistente,
                        nome = "-",
                        documento = Documento.cpf("39053344705"),
                        email = "ref@local",
                    ),
            )
        `when`(veiculoRepository.buscarPorId(veiculo.id)).thenReturn(veiculo)
        `when`(clienteRepository.buscarPorId(motoristaIdInexistente)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                useCase.executar(veiculoComMotoristaInvalido)
            }

        assertTrue(exception.message!!.contains("não encontrado"))
        verify(veiculoRepository, never()).salvar(anyObject())
    }
}
