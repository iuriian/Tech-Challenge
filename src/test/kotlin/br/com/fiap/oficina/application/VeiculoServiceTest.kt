package br.com.fiap.oficina.application

import br.com.fiap.oficina.application.service.VeiculoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.VeiculoRepository
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
class VeiculoServiceTest {

    @Mock
    lateinit var repository: VeiculoRepository

    @InjectMocks
    lateinit var service: VeiculoService

    private lateinit var veiculo: Veiculo

    @BeforeEach
    fun setup() {
        val cliente = Cliente(
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
            motorista = cliente
        )
    }

    @Test
    fun `deve salvar veiculo quando placa nao existe`() {
        `when`(repository.existePorPlaca("ABC1D23")).thenReturn(false)
        `when`(repository.salvar(veiculo)).thenReturn(veiculo)

        assertEquals(veiculo, service.salvarVeiculo(veiculo))
        verify(repository).salvar(veiculo)
    }

    @Test
    fun `deve rejeitar veiculo com placa ja cadastrada`() {
        `when`(repository.existePorPlaca("ABC1D23")).thenReturn(true)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.salvarVeiculo(veiculo)
        }

        assertEquals("Veiculo já cadastrado", exception.message)
        verify(repository, never()).salvar(veiculo)
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
        val motorista = veiculo.motorista
        `when`(repository.buscarPorMotorista(motorista)).thenReturn(listOf(veiculo))

        assertEquals(listOf(veiculo), service.buscarPorMotorista(motorista))
    }
}
