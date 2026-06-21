package br.com.fiap.oficina.application

import br.com.fiap.oficina.application.service.VeiculoComando
import br.com.fiap.oficina.application.service.VeiculoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import br.com.fiap.oficina.anyObject
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class VeiculoServiceTest {

    @Mock
    lateinit var repository: VeiculoRepository

    @Mock
    lateinit var clienteRepository: ClienteRepository

    private lateinit var service: VeiculoService

    private lateinit var motorista: Cliente
    private lateinit var veiculo: Veiculo
    private lateinit var comando: VeiculoComando

    @BeforeEach
    fun setup() {
        service = VeiculoService(repository, clienteRepository)
        motorista = Cliente(
            id = Id.gerar(),
            nome = "Dono",
            documento = Documento.cpf("39053344705"),
            email = "dono@example.com"
        )
        veiculo = Veiculo(
            id = Id.gerar(),
            marca = "Volkswagen",
            nome = "Gol",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motorista = motorista
        )
        comando = VeiculoComando(
            marca = "Volkswagen",
            nome = "Gol",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motoristaId = motorista.id
        )
    }

    @Test
    fun `deve salvar veiculo quando placa nao existe`() {
        `when`(repository.existePorPlaca("ABC1D23")).thenReturn(false)
        `when`(clienteRepository.buscarPorId(motorista.id)).thenReturn(motorista)
        `when`(repository.salvar(anyObject())).thenReturn(veiculo)

        val resultado = service.salvarVeiculo(comando)

        assertEquals(veiculo, resultado)
        verify(repository).salvar(anyObject())
    }

    @Test
    fun `deve rejeitar veiculo com placa ja cadastrada`() {
        `when`(repository.existePorPlaca("ABC1D23")).thenReturn(true)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.salvarVeiculo(comando)
        }

        assertEquals("Veiculo já cadastrado", exception.message)
        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `deve lancar excecao quando motorista nao encontrado`() {
        `when`(repository.existePorPlaca("ABC1D23")).thenReturn(false)
        `when`(clienteRepository.buscarPorId(motorista.id)).thenReturn(null)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.salvarVeiculo(comando)
        }

        assertTrue(exception.message!!.contains("Cliente não encontrado"))
    }

    @Test
    fun `deve buscar veiculo por id`() {
        `when`(repository.buscarPorId(veiculo.id)).thenReturn(veiculo)

        assertEquals(veiculo, service.buscarPorId(veiculo.id))
    }

    @Test
    fun `deve buscar veiculo por placa`() {
        `when`(repository.buscarPorPlaca("ABC1D23")).thenReturn(veiculo)

        assertEquals(veiculo, service.buscarPorPlaca("ABC1D23"))
    }

    @Test
    fun `deve buscar veiculos por motorista`() {
        `when`(repository.buscarPorMotorista(motorista.id)).thenReturn(listOf(veiculo))

        assertEquals(listOf(veiculo), service.buscarPorMotorista(motorista.id))
    }
}
