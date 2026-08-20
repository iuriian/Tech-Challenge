package br.com.fiap.oficina.application

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.service.VeiculoComando
import br.com.fiap.oficina.application.service.VeiculoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.application.port.out.ClienteRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
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
        comando =
            VeiculoComando(
                marca = "Volkswagen",
                nome = "Gol",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motoristaId = motorista.id,
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

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.salvarVeiculo(comando)
            }

        assertEquals("Veiculo já cadastrado", exception.message)
        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `deve lancar excecao quando motorista nao encontrado`() {
        `when`(repository.existePorPlaca("ABC1D23")).thenReturn(false)
        `when`(clienteRepository.buscarPorId(motorista.id)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
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

    @Test
    fun `deve listar todos os veiculos`() {
        `when`(repository.listarTodos()).thenReturn(listOf(veiculo))

        val resultado = service.listarTodos()

        assertEquals(listOf(veiculo), resultado)
        verify(repository).listarTodos()
    }

    @Test
    fun `deve atualizar veiculo mantendo a mesma placa`() {
        `when`(repository.buscarPorId(veiculo.id)).thenReturn(veiculo)
        `when`(clienteRepository.buscarPorId(motorista.id)).thenReturn(motorista)
        `when`(repository.salvar(anyObject())).thenReturn(veiculo)

        val resultado = service.atualizarVeiculo(veiculo.id, comando)

        assertEquals(veiculo, resultado)
        verify(repository, never()).existePorPlaca(anyObject())
        verify(repository).salvar(anyObject())
    }

    @Test
    fun `deve atualizar veiculo com nova placa disponivel`() {
        val novaPlaca = "XYZ9876"
        val comandoNovaPlaca = comando.copy(placa = novaPlaca)
        val veiculoAtualizado = veiculo.copy(placa = novaPlaca)
        `when`(repository.buscarPorId(veiculo.id)).thenReturn(veiculo)
        `when`(repository.existePorPlaca(novaPlaca)).thenReturn(false)
        `when`(clienteRepository.buscarPorId(motorista.id)).thenReturn(motorista)
        `when`(repository.salvar(anyObject())).thenReturn(veiculoAtualizado)

        val resultado = service.atualizarVeiculo(veiculo.id, comandoNovaPlaca)

        assertEquals(novaPlaca, resultado.placa)
        verify(repository).existePorPlaca(novaPlaca)
    }

    @Test
    fun `deve rejeitar atualizacao quando nova placa ja esta em uso`() {
        val novaPlaca = "XYZ9876"
        `when`(repository.buscarPorId(veiculo.id)).thenReturn(veiculo)
        `when`(repository.existePorPlaca(novaPlaca)).thenReturn(true)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.atualizarVeiculo(veiculo.id, comando.copy(placa = novaPlaca))
            }

        assertTrue(exception.message!!.contains(novaPlaca))
        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `deve lancar excecao ao atualizar veiculo inexistente`() {
        val idInexistente = Id.generate()
        `when`(repository.buscarPorId(idInexistente)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.atualizarVeiculo(idInexistente, comando)
            }

        assertTrue(exception.message!!.contains("não encontrado"))
        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `deve lancar excecao ao atualizar quando novo motorista nao existe`() {
        val motoristaIdInexistente = Id.generate()
        `when`(repository.buscarPorId(veiculo.id)).thenReturn(veiculo)
        `when`(clienteRepository.buscarPorId(motoristaIdInexistente)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.atualizarVeiculo(veiculo.id, comando.copy(motoristaId = motoristaIdInexistente))
            }

        assertTrue(exception.message!!.contains("não encontrado"))
        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `deve remover veiculo existente`() {
        `when`(repository.buscarPorId(veiculo.id)).thenReturn(veiculo)

        service.removerVeiculo(veiculo.id)

        verify(repository).remover(veiculo.id)
    }

    @Test
    fun `deve lancar excecao ao remover veiculo inexistente`() {
        val idInexistente = Id.generate()
        `when`(repository.buscarPorId(idInexistente)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.removerVeiculo(idInexistente)
            }

        assertTrue(exception.message!!.contains("não encontrado"))
        verify(repository, never()).remover(anyObject())
    }
}
